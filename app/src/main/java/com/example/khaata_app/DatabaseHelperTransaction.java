    package com.example.khaata_app;

    import android.content.ContentValues;
    import android.content.Context;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.sqlite.SQLiteOpenHelper;
    import android.widget.Toast;

    import androidx.annotation.Nullable;

    import java.util.ArrayList;

    public class DatabaseHelperTransaction {
        private final String DATABASE_NAME = "TransactionDB";
        private final int DATABASE_VERSION = 1;

        private final String TABLE_NAME = "Transaction_Table";
        private final String KEY_VENDOR_ID = "_vendorid";
        private final String KEY_CUSTOMER_ID = "_customerid";

        private final String KEY_ID = "_id";
        private final String KEY_NAME = "_name";
        private final String KEY_DATE="_date";
        private final String KEY_TIME="_time";
        private final String KEY_SEND="_send";
        private final String KEY_RECEIVE="_receive";
        private final String KEY_AMOUNT="_amount";



        DatabaseHelperTransaction.CreateDataBase helper;
        SQLiteDatabase database;
        Context context;

        public DatabaseHelperTransaction(Context context)
        {
            this.context = context;

        }

        public void updateTransaction(int id, String name,int amount)
        {
            ContentValues cv = new ContentValues();
            cv.put(KEY_ID,id);
            cv.put(KEY_NAME, name);
            cv.put(KEY_AMOUNT,amount);

            int records = database.update(TABLE_NAME, cv,  KEY_ID + "=?", new String[]{String.valueOf(id)});
            if(records>0)
            {
                Toast.makeText(context, "Transaction updated", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(context, "Transaction not updated", Toast.LENGTH_SHORT).show();
            }
        }

        public void deleteTransaction(int id)
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

        public void insertTransaction(int vendorid,int customer_id,String name, String date,String time,int send,int receive,int amount)
        {
            ContentValues cv = new ContentValues();
            cv.put(KEY_VENDOR_ID,vendorid);
            cv.put(KEY_CUSTOMER_ID,customer_id);
            cv.put(KEY_NAME,name);
            cv.put(KEY_DATE,date);
            cv.put(KEY_TIME,time);
            cv.put(KEY_SEND,send);
            cv.put(KEY_RECEIVE,receive);
            cv.put(KEY_AMOUNT,amount);

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

        public ArrayList<Transaction> readAllTransactions(int customerid)
        {
            ArrayList<Transaction> records = new ArrayList<>();
            String[] selectionArgs = { String.valueOf(customerid) };
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_CUSTOMER_ID + " = ?", selectionArgs);
            int vendor_id_index=cursor.getColumnIndex(KEY_VENDOR_ID);
            int customer_id_index=cursor.getColumnIndex(KEY_CUSTOMER_ID);
            int id_Index = cursor.getColumnIndex(KEY_ID);
            int name_Index = cursor.getColumnIndex(KEY_NAME);
            int date_Index = cursor.getColumnIndex(KEY_DATE);
            int time_index = cursor.getColumnIndex(KEY_TIME);
            int send_index=cursor.getColumnIndex(KEY_SEND);
            int receive_index=cursor.getColumnIndex(KEY_RECEIVE);
            int amount_index=cursor.getColumnIndex(KEY_AMOUNT);

            if(cursor.moveToFirst())
            {
                do{
                    Transaction c = new Transaction();

                    c.setVid(cursor.getInt(vendor_id_index));
                    c.setCid(cursor.getInt(customer_id_index));
                    c.setTid(cursor.getInt(id_Index));
                    c.setName(cursor.getString(name_Index));
                    c.setDate(cursor.getString(date_Index));
                    c.setTime(cursor.getString(time_index));
                    c.setSend(cursor.getInt(send_index));
                    c.setReceive(cursor.getInt(receive_index));
                    c.setAmount(cursor.getInt(amount_index));

                    records.add(c);
                }while(cursor.moveToNext());
            }

            cursor.close();

            return records;
        }

        public int getSendValue(int transactionId) {
            int sendValue = -1; // Default value if transaction_id doesn't exist

            String[] selectionArgs = { String.valueOf(transactionId) };
            Cursor cursor = database.rawQuery("SELECT " + KEY_SEND + " FROM " + TABLE_NAME + " WHERE " + KEY_ID + " = ?", selectionArgs);

            if(cursor.moveToFirst()) {
                sendValue = cursor.getInt(cursor.getColumnIndex(KEY_SEND));
            }

            cursor.close();

            return sendValue;
        }
        public ArrayList<Transaction> readTransactionsWithinDateRange(int customerId, String startDate, String endDate) {
            ArrayList<Transaction> records = new ArrayList<>();
            String[] selectionArgs = {String.valueOf(customerId), startDate, endDate};
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_CUSTOMER_ID + " = ? AND " + KEY_DATE + " BETWEEN ? AND ?", selectionArgs);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Transaction transaction = new Transaction();
                    transaction.setVid(cursor.getInt(cursor.getColumnIndex(KEY_VENDOR_ID)));
                    transaction.setCid(cursor.getInt(cursor.getColumnIndex(KEY_CUSTOMER_ID)));
                    transaction.setTid(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                    transaction.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                    transaction.setDate(cursor.getString(cursor.getColumnIndex(KEY_DATE)));
                    transaction.setTime(cursor.getString(cursor.getColumnIndex(KEY_TIME)));
                    transaction.setSend(cursor.getInt(cursor.getColumnIndex(KEY_SEND)));
                    transaction.setReceive(cursor.getInt(cursor.getColumnIndex(KEY_RECEIVE)));
                    transaction.setAmount(cursor.getInt(cursor.getColumnIndex(KEY_AMOUNT)));

                    records.add(transaction);
                } while (cursor.moveToNext());

                cursor.close();
            }

            return records;
        }


        public int getReceiveValue(int transactionId) {
            int receiveValue = -1; // Default value if transaction_id doesn't exist

            String[] selectionArgs = { String.valueOf(transactionId) };
            Cursor cursor = database.rawQuery("SELECT " + KEY_RECEIVE + " FROM " + TABLE_NAME + " WHERE " + KEY_ID + " = ?", selectionArgs);

            if(cursor.moveToFirst()) {
                receiveValue = cursor.getInt(cursor.getColumnIndex(KEY_RECEIVE));
            }

            cursor.close();

            return receiveValue;
        }

        public int getAmount(int transactionId) {
            int amount = -1; // Default value if transaction_id doesn't exist

            String[] selectionArgs = { String.valueOf(transactionId) };
            Cursor cursor = database.rawQuery("SELECT " + KEY_AMOUNT + " FROM " + TABLE_NAME + " WHERE " + KEY_ID + " = ?", selectionArgs);

            if(cursor.moveToFirst()) {
                amount = cursor.getInt(cursor.getColumnIndex(KEY_AMOUNT));
            }

            cursor.close();

            return amount;
        }

        public String getName(int transactionId) {
            String name = null; // Default value if transaction_id doesn't exist

            String[] selectionArgs = { String.valueOf(transactionId) };
            Cursor cursor = database.rawQuery("SELECT " + KEY_NAME + " FROM " + TABLE_NAME + " WHERE " + KEY_ID + " = ?", selectionArgs);

            if(cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
            }

            cursor.close();

            return name;
        }

        public void open()
        {
            helper = new DatabaseHelperTransaction.CreateDataBase(context, DATABASE_NAME, null, DATABASE_VERSION);
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
                        KEY_CUSTOMER_ID + " INTEGER NOT NULL," +
                        KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        KEY_NAME + " TEXT NOT NULL," +
                        KEY_DATE + " TEXT NOT NULL," +
                        KEY_TIME + " TEXT NOT NULL," +
                        KEY_SEND + " INTEGER NOT NULL," +
                        KEY_RECEIVE + " INTEGER NOT NULL," +
                        KEY_AMOUNT + " INTEGER NOT NULL" +
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
