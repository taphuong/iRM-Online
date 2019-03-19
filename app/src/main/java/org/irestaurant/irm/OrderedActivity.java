package org.irestaurant.irm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.irestaurant.irm.Database.DatabaseFood;
import org.irestaurant.irm.Database.DatabaseOrdered;
import org.irestaurant.irm.Database.DatabaseTable;
import org.irestaurant.irm.Database.Food;
import org.irestaurant.irm.Database.FoodAdapter;
import org.irestaurant.irm.Database.FoodOrderedAdapter;
import org.irestaurant.irm.Database.Number;
import org.irestaurant.irm.Database.Ordered;
import org.irestaurant.irm.Database.OredredAdapter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderedActivity extends Activity {
    ListView lvFood, lvOrdered;
    TextView tvNumber, tvIdNumber, tvTotal, tvDate;
    public static String getIdNumber, getNumber;
    long tongtien;
    private long price;

    Button btnHome;
    List<Food> foodList;
    List<Ordered>orderedList;
    FoodOrderedAdapter foodOrderedAdapter;
    OredredAdapter oredredAdapter;

    DatabaseFood databaseFood;
    DatabaseOrdered databaseOrdered;
    DatabaseTable databaseTable;

    private void Anhxa(){
        lvFood = findViewById(R.id.lv_food);
        lvOrdered = findViewById(R.id.lv_ordered);
        tvNumber = findViewById(R.id.tv_number);
        tvIdNumber = findViewById(R.id.tv_idnumber);
        tvTotal = findViewById(R.id.tv_total);
        tvDate = findViewById(R.id.tv_date);
        btnHome = findViewById(R.id.btn_home);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordered);
        Anhxa();
        Intent intent = getIntent();
        getIdNumber = intent.getExtras().getString("idnumber");
        getNumber = intent.getExtras().getString("number");
        tvIdNumber.setText(getIdNumber);
        tvNumber.setText("Bàn số: "+getNumber);
        String date = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());
        tvDate.setText(date);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderedActivity.this, MainActivity.class));
                finish();
            }
        });

        databaseFood = new DatabaseFood(this);
        foodList = databaseFood.getallFood();
        databaseOrdered = new DatabaseOrdered(this);
        orderedList = databaseOrdered.getallOrdered(getNumber);
        setLvFood();
        setLvOrdered();
        lvOrdered.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                android.support.v7.widget.PopupMenu popupMenu = new android.support.v7.widget.PopupMenu(OrderedActivity.this,view);
                popupMenu.getMenuInflater().inflate(R.menu.ordered_popup,popupMenu.getMenu());
                popupMenu.setGravity(Gravity.RIGHT);
                popupMenu.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.popup_edit:
                                final Dialog dialog = new Dialog(OrderedActivity.this);
                                dialog.setContentView(R.layout.dialog_addnumber);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.setCanceledOnTouchOutside(false);
                                final Button btnMinus = (Button) dialog.findViewById(R.id.btn_minus);
                                final Button btnAdd = (Button) dialog.findViewById(R.id.btn_add);
                                Button btnClose     = (Button) dialog.findViewById(R.id.btn_close);
                                Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);
                                btnConfirm.setText("Lưu");
                                TextView tvFood = dialog.findViewById(R.id.themban);
                                tvFood.setText(orderedList.get(position).getFoodname());
                                final EditText edtAmount = (EditText) dialog.findViewById(R.id.edt_amount);
                                edtAmount.setText(orderedList.get(position).getAmount());
                                if (Integer.valueOf(orderedList.get(position).getAmount())>1){
                                    btnMinus.setVisibility(View.VISIBLE);
                                }else {btnMinus.setVisibility(View.INVISIBLE);}

                                if (Integer.valueOf(orderedList.get(position).getAmount())>999){
                                    btnAdd.setVisibility(View.INVISIBLE);
                                }else {btnAdd.setVisibility(View.VISIBLE);}
                                price = Integer.valueOf(orderedList.get(position).getPrice());
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
                                        if (edtAmount.getText().toString().equals("") || edtAmount.getText().toString().equals("0")){
                                            edtAmount.setError("Nhập số phần");
                                            edtAmount.requestFocus();
                                        }else {
                                            String foodname = orderedList.get(position).getFoodname();
                                            String newamount = edtAmount.getText().toString();
                                            String date = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());
                                            String total = String.valueOf(price*Integer.valueOf(newamount));
                                            updateOrdered(foodname,newamount, date, String.valueOf(price), total, dialog);

                                        }
                                    }
                                });
                                break;
                            case R.id.popup_delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(OrderedActivity.this);
                                builder.setMessage("Bạn muốn xóa "+orderedList.get(position).getAmount()+" phần "+orderedList.get(position).getFoodname()+" ?");
                                builder.setCancelable(false);
                                builder.setPositiveButton("Không", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                builder.setNegativeButton("Xóa", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        databaseOrdered.deleteOrdered(orderedList.get(position).getId());
                                        setLvOrdered();
                                        orderedList = databaseOrdered.getallOrdered(getNumber);
                                        if (orderedList.size()==0){
                                            databaseTable = new DatabaseTable(OrderedActivity.this);
                                            Number number = new Number();
                                            number.setStatus("free");
                                            databaseTable.updateTable(number, getNumber);
                                        }
                                    }
                                });

                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();

                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
                return false;
            }
        });
    }

    public void setLvFood() {
        if (foodOrderedAdapter == null) {
            foodOrderedAdapter = new FoodOrderedAdapter(OrderedActivity.this, R.layout.item_table, foodList, this);
            lvFood.setAdapter(foodOrderedAdapter);
        } else {
            foodList.clear();
            foodList.addAll(databaseFood.getallFood());
            foodOrderedAdapter.notifyDataSetChanged();
            lvFood.setSelection(foodOrderedAdapter.getCount() - 1);
        }
    }
    public void setLvOrdered() {
        if (oredredAdapter == null) {
            tongtien=0;
            oredredAdapter = new OredredAdapter(OrderedActivity.this, R.layout.item_ordered, orderedList);
            lvOrdered.setAdapter(oredredAdapter);
            for (int a =0; a<orderedList.size();a++){
                tongtien += Integer.valueOf(orderedList.get(a).getTotal());
            }
            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            formatter.applyPattern("#,###,###,###");
            tvTotal.setText(formatter.format(tongtien));
        } else {
            tongtien=0;
            orderedList.clear();
            orderedList.addAll(databaseOrdered.getallOrdered(getNumber));
            oredredAdapter.notifyDataSetChanged();
            lvOrdered.setSelection(oredredAdapter.getCount() - 1);
            for (int a =0; a<orderedList.size();a++){
                tongtien += Integer.valueOf(orderedList.get(a).getTotal());
            }
            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            formatter.applyPattern("#,###,###,###");
            tvTotal.setText(formatter.format(tongtien));
        }
    }

    private void updateOrdered (String foodname, String newamout, String date, String price, String newtotal, Dialog dialog){
        databaseOrdered = new DatabaseOrdered(this);
        Ordered ordered = new Ordered();
        ordered.setNumber(getNumber);
        ordered.setFoodname(foodname);
        ordered.setAmount(newamout);
        ordered.setStatus("notyet");
        ordered.setDate(date);
        ordered.setPrice(price);
        ordered.setTotal(newtotal);
        int result = databaseOrdered.updateOrdered(ordered, getIdNumber);
        if (result>0){
            Toast.makeText(OrderedActivity.this, "Đã thay đổi " + newamout + " phần "+ foodname, Toast.LENGTH_LONG).show();
            dialog.dismiss();
            setLvOrdered();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
                startActivity(new Intent(OrderedActivity.this, MainActivity.class));
                finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
