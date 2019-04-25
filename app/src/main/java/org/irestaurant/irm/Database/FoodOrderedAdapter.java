package org.irestaurant.irm.Database;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.irestaurant.irm.MainActivity;
import org.irestaurant.irm.MenuActivity;
import org.irestaurant.irm.OrderedActivity;
import org.irestaurant.irm.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FoodOrderedAdapter extends ArrayAdapter {
    private Context context;
    private List<Food> foodList;
    private List<Ordered>orderedList;
    LayoutInflater inflater;
    private int layout;
    private long price;
    DatabaseOrdered databaseOrdered;
    OrderedActivity orderedActivity;
    DatabaseTable databaseTable;

    public FoodOrderedAdapter(@NonNull Context context, int layout, @NonNull List<Food> foodList, OrderedActivity orderedActivity) {
        super(context, layout, foodList);
        this.context = context;
        this.layout = layout;
        this.foodList = foodList;
        this.orderedActivity = orderedActivity;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        databaseOrdered = new DatabaseOrdered(context);
        databaseTable = new DatabaseTable(context);

        if(inflater == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView ==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_food,parent,false);
        }
        final FoodViewHolder vholder = new FoodViewHolder(convertView);
        final Food food = foodList.get(position);
        int stt = position +1;
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###,###,###");
        vholder.tvFood.setText(stt+". "+food.getFoondname());
        vholder.layoutButton.setVisibility(View.GONE);

        vholder.tvPrice.setText(formatter.format(Integer.valueOf(food.getFoodprice())));

        vholder.setItemLongClickListener(new ItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View v) {
                return true;
            }
        });

        vholder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_addnumber);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCanceledOnTouchOutside(false);
                final Button btnMinus = (Button) dialog.findViewById(R.id.btn_minus);
                final Button btnAdd = (Button) dialog.findViewById(R.id.btn_add);
                Button btnClose     = (Button) dialog.findViewById(R.id.btn_close);
                Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);
                btnConfirm.setText("Chọn");
                TextView tvFood = dialog.findViewById(R.id.themban);
                tvFood.setText(food.getFoondname());
                final EditText edtAmount = (EditText) dialog.findViewById(R.id.edt_amount);
                price = Integer.valueOf(food.getFoodprice());
                dialog.show();
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnMinus.setVisibility(View.VISIBLE);
                        if (edtAmount.getText().toString().equals("")){
                            edtAmount.setText("1");
                        } else if (Integer.valueOf(edtAmount.getText().toString())==999){
                            edtAmount.setText("1000");
                            btnAdd.setVisibility(View.INVISIBLE);
                        } else {
                            edtAmount.setText(String.valueOf(Integer.valueOf(edtAmount.getText().toString())+1));
                        }
                    }
                });
                btnMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnAdd.setVisibility(View.VISIBLE);
                        String stramount = edtAmount.getText().toString();
                        if (stramount.isEmpty() || stramount.equals("2")){
                            edtAmount.setText("1");
                            btnMinus.setVisibility(View.INVISIBLE);
                        }  else {
                            long amount = Integer.valueOf(edtAmount.getText().toString());
                            edtAmount.setText(String.valueOf(amount-1));
                        }

                    }
                });
                edtAmount.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String amout = edtAmount.getText().toString();
                        if (amout.isEmpty() || amout.equals("1") || amout.equals("0")) {
                            btnMinus.setVisibility(View.INVISIBLE);
                            btnAdd.setVisibility(View.VISIBLE);
                        } else if (Integer.valueOf(amout) > 1000) {
                            edtAmount.setText("1000");
                            edtAmount.setSelection(edtAmount.getText().length());
                            btnAdd.setVisibility(View.INVISIBLE);
                            btnMinus.setVisibility(View.VISIBLE);
                        } else {
                            btnMinus.setVisibility(View.VISIBLE);
                            btnAdd.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String number = orderedActivity.getNumber;
                        String foodname = food.getFoondname();
                        String amount = edtAmount.getText().toString();
                        String date = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());
                        String total = String.valueOf(price*Integer.valueOf(amount));
                        if (edtAmount.getText().toString().equals("") || edtAmount.getText().toString().equals("0")){
                            edtAmount.setError("Nhập số phần");
                            edtAmount.requestFocus();
                        }else {
                            orderedList = databaseOrdered.getallOrdered(number);
                            if (orderedList.size()>0){
                                int kiemtra = 0;
                                for (int b = 0; b < orderedList.size(); b++) {
                                    if (orderedList.get(b).getNumber().equals(number) && orderedList.get(b).getFoodname().equals(foodname) && orderedList.get(b).getStatus().equals("notyet")){
                                       kiemtra = 1;
                                       long newam = Integer.valueOf(amount) + Integer.valueOf(orderedList.get(b).getAmount());
                                       String newamount = String.valueOf(newam);
                                       String newtotal = String.valueOf(price*Integer.valueOf(newamount));
                                       int id = orderedList.get(b).getId();
                                       updateOrdered(String.valueOf(id),number,foodname,newamount, amount, String.valueOf(price), date, newtotal, dialog);
                                    }
                                }
                                if (kiemtra == 0){
                                    addOrdered(number, foodname, amount, String.valueOf(price), total, dialog);
                                }
                            }else {
                                addOrdered(number, foodname, amount, String.valueOf(price), total, dialog);
                            }
                        }
                    }
                });
            }
        });
        return convertView;
    }

    private void addOrdered (String number, String foodname, String amout, String price, String total, Dialog dialog){
        String status = "notyet";
        String date = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());
        String time = new SimpleDateFormat("kk:mm", Locale.getDefault()).format(new Date());

        databaseOrdered = new DatabaseOrdered(context);
        Ordered ordered = new Ordered();
        ordered.setNumber(number);
        ordered.setFoodname(foodname);
        ordered.setAmount(amout);
        ordered.setStatus(status);
        ordered.setDate(date);
        ordered.setTime(time);
        ordered.setPrice(price);
        ordered.setTotal(total);
        if (databaseOrdered.creat(ordered)){
            orderedActivity.setLvOrdered();
            updateTable("busy", number);
            Toast.makeText(context, "Đã chọn " + amout + " phần "+ foodname, Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.error);
            builder.setMessage(R.string.cannot_create);
            builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

    }
    private void updateOrdered (String id, String number, String foodname, String newamout, String oldamount, String price, String date, String newtotal, Dialog dialog){
        databaseOrdered = new DatabaseOrdered(context);
        Ordered ordered = new Ordered();
        ordered.setNumber(number);
        ordered.setFoodname(foodname);
        ordered.setAmount(newamout);
        ordered.setStatus("notyet");
        ordered.setDate(date);
        ordered.setPrice(price);
        ordered.setTotal(newtotal);
        int result = databaseOrdered.updateOrdered(ordered, id);
        if (result>0){
            Toast.makeText(context, "Đã thêm " + oldamount + " phần "+ foodname, Toast.LENGTH_LONG).show();
            dialog.dismiss();
            orderedActivity.setLvOrdered();
        }
    }

    private void updateTable (String status, String tb){
        databaseTable = new DatabaseTable(context);
        Number number = new Number();
        number.setStatus(status);
        databaseTable.updateTable(number, tb);
    }


}
