package com.example.khaata_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class KhaataManager extends AppCompatActivity implements KhaataAdapter.ItemSelected {

    int Id;
    int Customer_id;
    String customer_name;
    String selected_currency;
    Button btnBackKhaataManager,btnaddCustomer;

    // SharedPreferences key
    private static final String SHARED_PREFS = "com.example.khaata_app.shared_prefs";
    private static final String SELECTED_CURRENCY_KEY = "selected_currency";

    RecyclerView rvKhaata;
    LinearLayoutManager manager;
    KhaataAdapter adapter;
    ArrayList<Customer> customers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khaata_manager);
        init();

        btnBackKhaataManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnaddCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KhaataManager.this, AddCustomer.class);
                intent.putExtra("user_id", Id);
                startActivity(intent);
                finish();
            }
        });

    }

    private void init()
    {
        Id = getIntent().getIntExtra("user_id", -1);
        // Accepting selected currency from intent
        selected_currency = getIntent().getStringExtra("selected_currency");
        customer_name=getIntent().getStringExtra("customer_name");
        btnBackKhaataManager=findViewById(R.id.btnBackKhaataManager);
        btnaddCustomer=findViewById(R.id.btnaddCustomer);
        rvKhaata = findViewById(R.id.rvKhaata);
        rvKhaata.setHasFixedSize(true);
        manager = new LinearLayoutManager(this);
        rvKhaata.setLayoutManager(manager);

        DatabaseHelperCustomer database = new DatabaseHelperCustomer(this);
        database.open();
        customers = database.readAllCustomers(Id);
        database.close();

        // Retrieve the stored currency from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        selected_currency = sharedPreferences.getString(SELECTED_CURRENCY_KEY, "Rupees");

        // Retrieve the SharedPreferences instance
        sharedPreferences = getSharedPreferences("com.example.khaata_app.shared_prefs", Context.MODE_PRIVATE);

        adapter = new KhaataAdapter(this, customers, sharedPreferences,Customer_id);
        rvKhaata.setAdapter(adapter);
    }

    @Override
    public void onItemClicked(int index) {
        Toast.makeText(this, String.valueOf(customers.get(index).getCid()), Toast.LENGTH_SHORT).show();
        Customer_id=customers.get(index).getCid();
        customer_name=customers.get(index).getName();
        Intent intent = new Intent(KhaataManager.this, SingleKhaataRecord.class);
        intent.putExtra("user_id", Id);
        intent.putExtra("selected_currency", selected_currency);
        intent.putExtra("customer_user_id",Customer_id);
        intent.putExtra("customer_name",customer_name);
        startActivity(intent);
        finish();
    }
}