package com.example.khaata_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import kotlin.Triple;

public class LoginActivity extends AppCompatActivity {

    EditText etEmailLogin;
    EditText etPasswordLogin;
    Button btnLoginPage;
    Button btnBackLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLoginPage=findViewById(R.id.btnLoginPage);
        btnBackLogin=findViewById(R.id.btnBackLogin);
        etEmailLogin=findViewById(R.id.etEmailLogin);
        etPasswordLogin=findViewById(R.id.etPasswordLogin);

        btnBackLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=etEmailLogin.getText().toString().trim();
                String password=etPasswordLogin.getText().toString().trim();
                if(email.isEmpty()){etEmailLogin.setError("Field cannot be empty");}
                if(password.isEmpty()){etPasswordLogin.setError("Field cannot be empty");}
                successful_login();
            }
        });
    }

    private void successful_login(){
        String email=etEmailLogin.getText().toString().trim();
        String password=etPasswordLogin.getText().toString().trim();
        DatabaseHelperVendor dbHelper = new DatabaseHelperVendor(LoginActivity.this);
        dbHelper.open();

        Triple<Boolean, Integer,String> validationResult = dbHelper.isValidUser(email, password);
        boolean isValidUser = validationResult.getFirst();
        int userId = validationResult.getSecond();
        String username=validationResult.getThird();
        dbHelper.close();

        if (isValidUser) {
            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, UserNameDisplay.class);
            intent.putExtra("user_name",username);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        } else {
            Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }
}