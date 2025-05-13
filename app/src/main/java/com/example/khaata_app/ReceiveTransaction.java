package com.example.khaata_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.telephony.SmsManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReceiveTransaction extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1001;
    Button btnBackReceiveTransaction,btnReceiveTransaction;
    TextView etNameReceiveTransaction,etAmountReceiveTransaction;
    int vendor_id,customer_id;
    String customer_name;
    String selected_currency;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_transaction);

        btnBackReceiveTransaction=findViewById(R.id.btnBackReceiveTransaction);
        btnReceiveTransaction=findViewById(R.id.btnReceiveTransaction);
        etNameReceiveTransaction=findViewById(R.id.etNameReceiveTransaction);
        etAmountReceiveTransaction=findViewById(R.id.etAmountReceiveTransaction);
        vendor_id=getIntent().getIntExtra("user_id", -1);
        customer_id=getIntent().getIntExtra("customer_user_id", -1);
        customer_name=getIntent().getStringExtra("customer_name");
        selected_currency = getIntent().getStringExtra("selected_currency");

        btnBackReceiveTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnReceiveTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=etNameReceiveTransaction.getText().toString().trim();
                String amount=etAmountReceiveTransaction.getText().toString().trim();
                if (name.isEmpty()) {
                    etNameReceiveTransaction.setError("Field cannot be empty");
                } else if (amount.isEmpty()) {
                    etAmountReceiveTransaction.setError("Field cannot be empty");
                } else {
                    String amountInRupees = getRupees(amount);
                    if (!amountInRupees.equals("Invalid Amount")) {
                        addTransaction(amountInRupees);
                        updateRemainingAmount(amountInRupees);
                        DatabaseHelperCustomer db = new DatabaseHelperCustomer(ReceiveTransaction.this);
                        db.open();
                        String phoneNumber = db.getPhoneNumber(customer_id);
                        db.close();
                        if (phoneNumber != null) {
                            sendSMS(phoneNumber, "Your transaction with Name : " + name + " and Amount : " + amount + " " + selected_currency + " has been added to khata which has been received by us");
                        }
                        Intent intent = new Intent(ReceiveTransaction.this, SingleKhaataRecord.class);
                        intent.putExtra("customer_user_id", customer_id);
                        intent.putExtra("customer_name", customer_name);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid amount entered!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void addTransaction(String amount){
        String name=etNameReceiveTransaction.getText().toString().trim();
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = dateFormat.format(currentDate);

        Date currentTime = new Date();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String formattedTime = timeFormat.format(currentTime);

        DatabaseHelperTransaction myDatabaseHelper = new DatabaseHelperTransaction(this);
        myDatabaseHelper.open();
        myDatabaseHelper.insertTransaction(vendor_id,customer_id,name,formattedDate,formattedTime,0,1,Integer.parseInt(amount));
        myDatabaseHelper.close();

        // Adding to Firestore
        Map<String, Object> transaction = new HashMap<>();
        transaction.put("_customerid", customer_id);
        transaction.put("_amount", Integer.parseInt(amount));
        transaction.put("_date", formattedDate);
        transaction.put("_id", "1");
        transaction.put("_name", name);
        transaction.put("_receive", 1);
        transaction.put("_send", 0);
        transaction.put("_time", formattedTime);
        transaction.put("_vendorid", vendor_id);

        db.collection("Transaction_Table")
                .add(transaction)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("firebase3", "DocumentSnapshot added with ID: " + documentReference.getId());
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("firebase3", "Error adding document", e);
                    }
                });
    }

    public void updateRemainingAmount(String amount){
        int subtracting_amount=Integer.parseInt(amount);

        DatabaseHelperCustomer db=new DatabaseHelperCustomer(this);
        db.open();
        int remaining_amount=db.getRemainingAmountForCustomer(customer_id);
        db.close();

        int new_amount=remaining_amount-subtracting_amount;

        DatabaseHelperCustomer db_update=new DatabaseHelperCustomer(this);
        db_update.open();
        db_update.updateCustomerRemainingAmount(customer_id,new_amount);
        db.close();
    }

    private void sendSMS(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void requestSMSPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
    }

    private String getRupees(String amount) {
        try {
            double convertedAmount = Double.parseDouble(amount);
            double rupees = 0;
            switch (selected_currency) {
                case "Rupees":
                    rupees = convertedAmount;
                    break;
                case "Dollar":
                    rupees = convertedAmount * 278.05;
                    break;
                case "Riyal":
                    rupees = convertedAmount * 74.13;
                    break;
                case "Yen":
                    rupees = convertedAmount * 1.82;
                    break;
            }
            int r = (int) Math.round(rupees);
            return r + "";
        } catch (NumberFormatException e) {
            // Handle the case where amount is not a valid number
            e.printStackTrace(); // Or log the error
            return "Invalid Amount";
        }
    }
}