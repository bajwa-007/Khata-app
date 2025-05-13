package com.example.khaata_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.Nullable;

import kotlin.Triple;

public class DatabaseHelperVendor {

    private final String DATABASE_NAME = "VendorsDB";
    private final int DATABASE_VERSION = 1;

    private final String TABLE_NAME = "Vendor_Table";
    private final String KEY_ID = "_id";
    private final String KEY_Email = "_email";
    private final String KEY_Password = "_password";

    private final String KEY_Username = "_username";

    CreateDataBase helper;
    SQLiteDatabase database;
    Context context;

    public DatabaseHelperVendor(Context context)
    {
        this.context = context;
    }

    public void insert(String username,String email, String password)
    {
        ContentValues cv = new ContentValues();
        cv.put(KEY_Email,email);
        cv.put(KEY_Password,password);
        cv.put(KEY_Username,username);

        long records = database.insert(TABLE_NAME, null, cv);
        if(records == -1)
        {
            Toast.makeText(context, "Data not inserted", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(context, "Total "+records+" vendors added", Toast.LENGTH_SHORT).show();
        }
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

    public Triple<Boolean, Integer, String> isValidUser(String email, String password) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] columns = {KEY_ID,KEY_Username};
        String selection = KEY_Email + " = ? AND " + KEY_Password + " = ?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs,
                null, null, null);

        boolean isValid = cursor != null && cursor.moveToFirst();
        int userId = -1;
        String username = null;
        if (isValid&& cursor.moveToFirst()) {
            // Retrieve the user ID from the cursor
            userId = cursor.getInt(cursor.getColumnIndex(KEY_ID));
            username = cursor.getString(cursor.getColumnIndex(KEY_Username));
        }

        if (cursor != null) {
            cursor.close();
        }

        return new Triple<>(isValid, userId, username);
    }

    private class CreateDataBase extends SQLiteOpenHelper
    {
        public CreateDataBase(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String query = "CREATE TABLE " + TABLE_NAME + "(" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_Password + " TEXT NOT NULL," +
                    KEY_Email + " TEXT NOT NULL," +
                    KEY_Username + " TEXT NOT NULL" +
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
