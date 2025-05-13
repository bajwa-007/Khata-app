package com.example.khaata_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DatabaseHelperCustomer {

    private final String DATABASE_NAME = "CustomerDB";
    private final int DATABASE_VERSION = 1;

    private final String TABLE_NAME = "Customer_Table";
    private final String KEY_VENDOR_ID = "_vendorid";
    private final String KEY_ID = "_id";
    private final String KEY_NAME = "_name";
    private final String KEY_DATE="_date";
    private final String KEY_TIME="_time";
    private final String KEY_REMAINING_AMOUNT="_remaining_amount";
    private final String KEY_PHONE_NUMBER="_phone_number";



    CreateDataBase helper;
    SQLiteDatabase database;
    Context context;

    public DatabaseHelperCustomer(Context context)
    {
        this.context = context;

    }

    public void updateCustomer(int id, String name,String phone)
    {
        ContentValues cv = new ContentValues();
        cv.put(KEY_ID,id);
        cv.put(KEY_NAME, name);
        cv.put(KEY_PHONE_NUMBER,phone);

        int records = database.update(TABLE_NAME, cv,  KEY_ID + "=?", new String[]{String.valueOf(id)});
        if(records>0)
        {
            Toast.makeText(context, "Customer updated", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(context, "Customer not updated", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateCustomerRemainingAmount(int id,int amount){
        ContentValues cv = new ContentValues();
        cv.put(KEY_ID,id);
        cv.put(KEY_REMAINING_AMOUNT, amount);

        int records = database.update(TABLE_NAME, cv,  KEY_ID + "=?", new String[]{String.valueOf(id)});
        if(records>0)
        {
            Toast.makeText(context, "Customer Remaining Amount updated", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(context, "Customer Remaining Amount not updated", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteCustomer(int id)
    {
        int rows = database.delete(TABLE_NAME,  KEY_ID + "=?", new String[]{String.valueOf(id)});

        if(rows>0)
        {
            Toast.makeText(context, "User deleted", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(context, "User not deleted", Toast.LENGTH_SHORT).show();
        }
    }

    public void insertCustomer(int vendorid,String name, String date,String time,String phone_number)
    {
        ContentValues cv = new ContentValues();
        cv.put(KEY_VENDOR_ID,vendorid);
        cv.put(KEY_NAME,name);
        cv.put(KEY_DATE,date);
        cv.put(KEY_TIME,time);
        cv.put(KEY_PHONE_NUMBER,phone_number);

        long records = database.insert(TABLE_NAME, null, cv);
        if(records == -1)
        {
            Toast.makeText(context, "Data not inserted", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(context, "Total "+records+"  added", Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayList<Customer> readAllCustomers(int vendorid)
    {
        ArrayList<Customer> records = new ArrayList<>();
        String[] selectionArgs = { String.valueOf(vendorid) };
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_VENDOR_ID + " = ?", selectionArgs);
        int vendor_id_index=cursor.getColumnIndex(KEY_VENDOR_ID);
        int id_Index = cursor.getColumnIndex(KEY_ID);
        int name_Index = cursor.getColumnIndex(KEY_NAME);
        int date_Index = cursor.getColumnIndex(KEY_DATE);
        int time_index = cursor.getColumnIndex(KEY_TIME);
        int remaining_amount_index=cursor.getColumnIndex(KEY_REMAINING_AMOUNT);
        int phone_number_index=cursor.getColumnIndex(KEY_PHONE_NUMBER);

        if(cursor.moveToFirst())
        {
            do{
                Customer c = new Customer();

                c.setVid(cursor.getInt(vendor_id_index));
                c.setCid(cursor.getInt(id_Index));
                c.setName(cursor.getString(name_Index));
                c.setDate(cursor.getString(date_Index));
                c.setTime(cursor.getString(time_index));
                c.setRemaining_amount(cursor.getInt(remaining_amount_index));
                c.setPhone_number(cursor.getString(phone_number_index));

                records.add(c);
            }while(cursor.moveToNext());
        }

        cursor.close();

        return records;
    }

    public int getRemainingAmountForCustomer(int customerId) {
        int remainingAmount = 0;
        String[] selectionArgs = {String.valueOf(customerId)};
        Cursor cursor = database.rawQuery("SELECT " + KEY_REMAINING_AMOUNT + " FROM " + TABLE_NAME + " WHERE " + KEY_ID + " = ?", selectionArgs);
        if (cursor.moveToFirst()) {
            remainingAmount = cursor.getInt(cursor.getColumnIndex(KEY_REMAINING_AMOUNT));
        }
        cursor.close();
        return remainingAmount;
    }

    public String getPhoneNumber(int customerId) {
        String phone_number = null;
        String[] selectionArgs = {String.valueOf(customerId)};
        Cursor cursor = database.rawQuery("SELECT " + KEY_PHONE_NUMBER + " FROM " + TABLE_NAME + " WHERE " + KEY_ID + " = ?", selectionArgs);
        if (cursor.moveToFirst()) {
            phone_number = cursor.getString(cursor.getColumnIndex(KEY_PHONE_NUMBER));
        }
        cursor.close();
        return phone_number;
    }

    public void open()
    {
        helper = new CreateDataBase(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = helper.getWritableDatabase();
    }

    public void close()
    {
        database.close();
        helper.close();
    }

    private class CreateDataBase extends SQLiteOpenHelper
    {
        public CreateDataBase(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String query = "CREATE TABLE " + TABLE_NAME + "(" +
                    KEY_VENDOR_ID + " INTEGER NOT NULL," +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_NAME + " TEXT NOT NULL," +
                    KEY_DATE + " TEXT NOT NULL," +
                    KEY_TIME + " TEXT NOT NULL," +
                    KEY_REMAINING_AMOUNT+" INTEGER DEFAULT 0,"+
                    KEY_PHONE_NUMBER+" TEXT NOT NULL"+
                    ");";
            db.execSQL(query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // backup code here
            db.execSQL("DROP TABLE "+TABLE_NAME+" IF EXISTS");
            onCreate(db);
        }
    }
}
