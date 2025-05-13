package com.example.khaata_app;

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

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    EditText etEmailAddressRegistration;
    EditText etPasswordRegistration,etUserNameRegistration;
    Button btnBackRegistration;
    Button btnRegistrationPage;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        btnBackRegistration=findViewById(R.id.btnBackRegistration);
        btnRegistrationPage=findViewById(R.id.btnRegistrationPage);
        etEmailAddressRegistration=findViewById(R.id.etEmailAddressRegistration);
        etPasswordRegistration=findViewById(R.id.etPasswordRegistration);
        etUserNameRegistration=findViewById(R.id.etUserNameRegistration);

        btnBackRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnRegistrationPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=etEmailAddressRegistration.getText().toString().trim();
                String password=etPasswordRegistration.getText().toString().trim();
                String username=etPasswordRegistration.getText().toString().trim();
                if(email.isEmpty()){etEmailAddressRegistration.setError("Field cannot be empty");}
                if(password.isEmpty()){etPasswordRegistration.setError("Field cannot be empty");}
                if(username.isEmpty()){etUserNameRegistration.setError("Field cannot be empty");}
                else{
                    addVendor();
                    finish();}
            }
        });

    }
    private void addVendor()
    {
        String email = etEmailAddressRegistration.getText().toString().trim();
        String password = etPasswordRegistration.getText().toString().trim();
        String username=etUserNameRegistration.getText().toString().trim();

        DatabaseHelperVendor myDatabaseHelper = new DatabaseHelperVendor(this);
        myDatabaseHelper.open();

        myDatabaseHelper.insert(username,email, password);

        myDatabaseHelper.close();

        // Adding to Firestore
        Map<String, Object> user = new HashMap<>();
        user.put("_id", "1");
        user.put("_username", username);
        user.put("_email", email);
        user.put("_password", password);

        db.collection("Vendor_Table")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("firebase1", "DocumentSnapshot added with ID: " + documentReference.getId());
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("firebase1", "Error adding document", e);
                    }
                });
    }
}