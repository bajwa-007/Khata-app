package com.example.khaata_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddCustomer extends AppCompatActivity {

    Button btnBackAddCustomer,btnAddAddCustomer;
    EditText etNameAddCustomer,etPhoneAddCustomer;
    int Id;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        btnBackAddCustomer=findViewById(R.id.btnBackAddCustomer);
        btnAddAddCustomer=findViewById(R.id.btnAddAddCustomer);
        etNameAddCustomer=findViewById(R.id.etNameAddCustomer);
        etPhoneAddCustomer=findViewById(R.id.etPhoneAddCustomer);
        Id = getIntent().getIntExtra("user_id", -1);

        btnBackAddCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnAddAddCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=etNameAddCustomer.getText().toString().trim();
                String phone=etPhoneAddCustomer.getText().toString().trim();
                if(name.isEmpty()){etNameAddCustomer.setError("Field cannot be empty");}
                if(phone.isEmpty()){etPhoneAddCustomer.setError("Field cannot be empty");}
                else{
                    addCustomer();
                    Intent intent = new Intent(AddCustomer.this, KhaataManager.class);
                    intent.putExtra("user_id", Id);
                    startActivity(intent);
                    finish();}
            }
        });
    }

    public void addCustomer(){
        String name=etNameAddCustomer.getText().toString().trim();
        String phone=etPhoneAddCustomer.getText().toString().trim();

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = dateFormat.format(currentDate);

        Date currentTime = new Date();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String formattedTime = timeFormat.format(currentTime);

        DatabaseHelperCustomer myDatabaseHelper = new DatabaseHelperCustomer(this);
        myDatabaseHelper.open();
        myDatabaseHelper.insertCustomer(Id,name, formattedDate,formattedTime,phone);
        myDatabaseHelper.close();

        // Adding to Firestore
        Map<String, Object> customer = new HashMap<>();
        customer.put("_id", "1");
        customer.put("_date", formattedDate);
        customer.put("_name", name);
        customer.put("_phone_number", phone);
        customer.put("_remaining_amount", "0");
        customer.put("_time", formattedTime);
        customer.put("_vendorid", Id);

        db.collection("Customer_Table")
                .add(customer)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("firebase2", "DocumentSnapshot added with ID: " + documentReference.getId());
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("firebase2", "Error adding document", e);
                    }
                });

    }
}