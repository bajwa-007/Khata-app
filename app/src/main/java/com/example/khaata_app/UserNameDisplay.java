package com.example.khaata_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class UserNameDisplay extends AppCompatActivity {

    // SharedPreferences key
    private static final String SHARED_PREFS = "com.example.khaata_app.shared_prefs";
    private static final String SELECTED_CURRENCY_KEY = "selected_currency";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name_display);

        String username = getIntent().getStringExtra("user_name");
        int userId = getIntent().getIntExtra("user_id", -1);
        Button btnMoveToKhaata = findViewById(R.id.btnMoveToKhaata);
        TextView tvUserNameShow = findViewById(R.id.tvUserNameShow);
        Button btnBackUSerNameDisplay = findViewById(R.id.btnBackUSerNameDisplay);

        // Currency declaration
        Spinner currency = findViewById(R.id.spinnerCurrency);

        if (username != null) {
            tvUserNameShow.setText(username);
        } else {
            tvUserNameShow.setText("Unknown User");
        }

        // Initialize SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        btnBackUSerNameDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnMoveToKhaata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected currency
                String selectedCurrency = currency.getSelectedItem().toString();

                // Store selected currency in SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(SELECTED_CURRENCY_KEY, selectedCurrency);
                editor.apply();

                Intent intent = new Intent(UserNameDisplay.this, KhaataManager.class);
                intent.putExtra("user_id", userId);
                // Passing the currency to next activity
                intent.putExtra("selected_currency", selectedCurrency);
                startActivity(intent);
            }
        });
    }
}
