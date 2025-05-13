package com.example.khaata_app;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class SingleKhaataRecord extends AppCompatActivity implements TransactionAdapter.ItemSelected {

    private static final int STORAGE_PERMISSION_CODE = 100;
    private static final int REQUEST_CREATE_FILE = 101;

    Button btnBackSingleRecordKhaata, btnSend, btnReceive, btnPdfGenerator;
    TextView tvCustomerNameKhaata;
    String customer_name;
    String selected_currency;
    int customer_id;
    int vendor_id;

    RecyclerView rvSingleRecordKhaata;
    LinearLayoutManager manager;
    TransactionAdapter adapter;
    ArrayList<Transaction> transactions;
    String startDate, endDate;

    // SharedPreferences key
    private static final String SHARED_PREFS = "com.example.khaata_app.shared_prefs";
    private static final String SELECTED_CURRENCY_KEY = "selected_currency";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_khaata_record);
        init();

        btnBackSingleRecordKhaata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (customer_name != null) {
            tvCustomerNameKhaata.setText(customer_name);
        } else {
            tvCustomerNameKhaata.setText("Unknown User");
        }

        // Retrieve the stored currency from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        selected_currency = sharedPreferences.getString(SELECTED_CURRENCY_KEY, "Rupees");

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SingleKhaataRecord.this, SendTransaction.class);
                intent.putExtra("user_id", vendor_id);
                intent.putExtra("selected_currency", selected_currency);
                intent.putExtra("customer_user_id", customer_id);
                intent.putExtra("customer_name", customer_name);
                startActivity(intent);
                finish();
            }
        });

        btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SingleKhaataRecord.this, ReceiveTransaction.class);
                intent.putExtra("user_id", vendor_id);
                intent.putExtra("customer_user_id", customer_id);
                intent.putExtra("customer_name", customer_name);
                intent.putExtra("selected_currency", selected_currency);
                startActivity(intent);
                finish();
            }
        });
        btnPdfGenerator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateRangePicker();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showDateRangePicker();
            } else {
                Toast.makeText(this, "Permission DENIED to write to your external storage!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void init() {
        customer_name = getIntent().getStringExtra("customer_name");
        selected_currency = getIntent().getStringExtra("selected_currency");
        customer_id = getIntent().getIntExtra("customer_user_id", -1);
        vendor_id = getIntent().getIntExtra("user_id", -1);
        btnBackSingleRecordKhaata = findViewById(R.id.btnBackSingleRecordKhaata);
        tvCustomerNameKhaata = findViewById(R.id.tvCustomerNameKhaata);
        btnSend = findViewById(R.id.btnSend);
        btnReceive = findViewById(R.id.btnReceive);
        btnPdfGenerator = findViewById(R.id.btnPdfGenerator);

        rvSingleRecordKhaata = findViewById(R.id.rvSingleRecordKhaata);
        rvSingleRecordKhaata.setHasFixedSize(true);
        manager = new LinearLayoutManager(this);
        rvSingleRecordKhaata.setLayoutManager(manager);

        DatabaseHelperTransaction database = new DatabaseHelperTransaction(this);
        database.open();
        transactions = database.readAllTransactions(customer_id);
        database.close();

        // Retrieve the SharedPreferences instance
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.khaata_app.shared_prefs", Context.MODE_PRIVATE);

        adapter = new TransactionAdapter(this, transactions, sharedPreferences,customer_id);
        rvSingleRecordKhaata.setAdapter(adapter);
    }

    @Override
    public void onItemClicked(int index) {
        Toast.makeText(this, String.valueOf(transactions.get(index).getTid()), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SingleKhaataRecord.this, EditTransaction.class);
        intent.putExtra("user_id", vendor_id);
        intent.putExtra("customer_user_id", customer_id);
        intent.putExtra("customer_name", customer_name);
        intent.putExtra("selected_currency", selected_currency);
        intent.putExtra("transaction_id", transactions.get(index).getTid());
        startActivity(intent);
        finish();
    }

    private void generateAndSendTransactionDetails() {
        DatabaseHelperTransaction database = new DatabaseHelperTransaction(this);
        database.open();
        ArrayList<Transaction> transactionsWithinDateRange = database.readTransactionsWithinDateRange(customer_id, startDate, endDate);
        database.close();

        if (transactionsWithinDateRange != null && !transactionsWithinDateRange.isEmpty()) {
            // Prepare the message containing transaction details
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("Transaction Details:\n");
            for (Transaction transaction : transactionsWithinDateRange) {
                messageBuilder.append("Transaction ID: ").append(transaction.getTid()).append("\n");
                messageBuilder.append("Name: ").append(transaction.getName()).append("\n");
                messageBuilder.append("Date: ").append(transaction.getDate()).append("\n");
                messageBuilder.append("Time: ").append(transaction.getTime()).append("\n");
                messageBuilder.append("Send: ").append(transaction.getSend()).append("\n");
                messageBuilder.append("Receive: ").append(transaction.getReceive()).append("\n");
                messageBuilder.append("Amount: ").append(transaction.getAmount()).append("\n");
                messageBuilder.append("-------------------------------------\n");
            }

            // Send the message via SMS or any other preferred method
            sendTransactionDetailsViaSMS(messageBuilder.toString());
        } else {
            Toast.makeText(SingleKhaataRecord.this, "No transactions found within the specified date range.", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendTransactionDetailsViaSMS(String message) {
        // Retrieve the customer's phone number from the database
        DatabaseHelperCustomer database = new DatabaseHelperCustomer(this);
        database.open();
        String phoneNumber = database.getPhoneNumber(customer_id);
        database.close();

        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            // Send the message via SMS
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
            intent.putExtra("sms_body", message);
            startActivity(intent);
        } else {
            Toast.makeText(SingleKhaataRecord.this, "Customer's phone number not found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDateRangePicker() {
        final Calendar calendar = Calendar.getInstance();
        int startYear = calendar.get(Calendar.YEAR);
        int startMonth = calendar.get(Calendar.MONTH);
        int startDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                startDate = String.format("%02d-%02d-%04d", dayOfMonth, (monthOfYear + 1), year);
                showEndDatePicker();
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, startDateListener, startYear, startMonth, startDay);
        datePickerDialog.setTitle("Select Start Date");
        datePickerDialog.show();
    }

    private void showEndDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int endYear = calendar.get(Calendar.YEAR);
        int endMonth = calendar.get(Calendar.MONTH);
        int endDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                endDate = String.format("%02d-%02d-%04d", dayOfMonth, (monthOfYear + 1), year);
                generateAndSendTransactionDetails();
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, endDateListener, endYear, endMonth, endDay);
        datePickerDialog.setTitle("Select End Date");
        datePickerDialog.show();
    }


}
