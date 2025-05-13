package com.example.khaata_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class KhaataAdapter extends RecyclerView.Adapter<KhaataAdapter.ViewHolder>{

    ArrayList<Customer> customers;
    Context context;

    String selectedCurrency;

    ItemSelected parentActivity;
    Button btnSendReceiveCustomer;
    int customer_id;

    public interface ItemSelected{
        public void onItemClicked(int index);
    }

    public KhaataAdapter(Context context, ArrayList<Customer> list, SharedPreferences sharedPreferences,int customerId)
    {
        this.context = context;
        parentActivity=(ItemSelected) context;
        customers = list;
        selectedCurrency = sharedPreferences.getString("selected_currency", "Rupees");
        customer_id=customerId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_single_khaata, parent, false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setTag(customers.get(position));
        int red= Color.parseColor("#FF0000");
        int green=Color.parseColor("#008000");
        double amount = customers.get(position).getRemaining_amount();
        if(customers.get(position).getRemaining_amount()>=0) {
            holder.tvRemainingAmount.setTextColor(red);
            holder.tvRemainingAmount.setText(getConvertedAmount(amount));
            btnSendReceiveCustomer.setText("Request");
        }
        else{
            holder.tvRemainingAmount.setTextColor(green);
            double showed_value=Math.abs(amount);
            holder.tvRemainingAmount.setText(getConvertedAmount(showed_value));
            btnSendReceiveCustomer.setText("Send");
        }
        holder.tvName.setText(customers.get(position).getName());
        holder.tvDate.setText(customers.get(position).getDate());
        holder.tvTime.setText(customers.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    private String getConvertedAmount(double amount) {
        double convertedAmount = 0;
        String formattedAmount = "";
        switch (selectedCurrency) {
            case "Rupees":
                convertedAmount = amount;
                formattedAmount = "PKR " + String.format("%.2f", convertedAmount);
                break;
            case "Dollar":
                convertedAmount = amount / 278.05;
                formattedAmount = "$ " + String.format("%.2f", convertedAmount);
                break;
            case "Riyal":
                convertedAmount = amount / 74.13;
                formattedAmount = "SAR " + String.format("%.2f", convertedAmount);
                break;
            case "Yen":
                convertedAmount = amount / 1.82;
                formattedAmount = "¥ " + String.format("%.2f", convertedAmount);
                break;
        }
        return formattedAmount;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvName, tvDate,tvTime,tvRemainingAmount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime= itemView.findViewById(R.id.tvTime);
            tvRemainingAmount=itemView.findViewById(R.id.tvRemainingAmount);
            btnSendReceiveCustomer=itemView.findViewById(R.id.btnSendReceiveCustomer);

            btnSendReceiveCustomer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentPosition = getAdapterPosition();
                    if (currentPosition != RecyclerView.NO_POSITION) {

                        if (customers.get(currentPosition).getRemaining_amount() >= 0) {
                            int amount;
                            String phone_number;
                            phone_number = customers.get(currentPosition).getPhone_number();
                            amount = customers.get(currentPosition).getRemaining_amount();

                            String message = "You have to pay me an amount of: " + amount;
                            Uri uri = Uri.parse("smsto:" + phone_number);
                            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                            intent.putExtra("sms_body", message);
                            context.startActivity(intent);
                        } else {
                            Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.techlogix.mobilinkcustomer");
                            if (intent != null) {
                                context.startActivity(intent);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("JazzCash Not Installed");
                                builder.setMessage("JazzCash app is not installed on your device. Please install it to proceed.");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                            }
                        }
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parentActivity.onItemClicked(customers.indexOf((Customer) itemView.getTag()));
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int currentPosition = getAdapterPosition();
                    if (currentPosition != RecyclerView.NO_POSITION) {
                        showUpdateDeleteDialog(customers.get(currentPosition),currentPosition);
                        return true;
                    }
                    return false;
                }
            });
        }
    }


    private void showUpdateDeleteDialog(final Customer customer,final int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_update_delete_customer, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(dialogView);

        final EditText etName = dialogView.findViewById(R.id.etNameDialog);
        final EditText etPhoneNumber = dialogView.findViewById(R.id.etPhoneDialog);


        etName.setText(customer.getName());
        etPhoneNumber.setText(customer.getPhone_number());

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String newName = etName.getText().toString();
                        String newPhoneNumber = etPhoneNumber.getText().toString();
                        DatabaseHelperCustomer db=new DatabaseHelperCustomer(context);
                        db.open();
                        db.updateCustomer(customer.getCid(),newName,newPhoneNumber);
                        db.close();
                        customer.setName(newName);
                        customer.setPhone_number(newPhoneNumber);
                        notifyItemChanged(position);
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AlertDialog.Builder deleteConfirmationDialogBuilder = new AlertDialog.Builder(context);
                        deleteConfirmationDialogBuilder.setTitle("Delete Confirmation");
                        deleteConfirmationDialogBuilder.setMessage("Are you sure you want to delete this customer?");
                        deleteConfirmationDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseHelperCustomer db=new DatabaseHelperCustomer(context);
                                db.open();
                                db.deleteCustomer(customer.getCid());
                                db.close();
                                customers.remove(position);
                                notifyDataSetChanged();
                            }
                        });
                        deleteConfirmationDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        deleteConfirmationDialogBuilder.show();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }
}


