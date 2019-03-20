package org.irestaurant.irm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.irestaurant.irm.Database.DatabaseOrdered;
import org.irestaurant.irm.Database.DatabaseRevenue;
import org.irestaurant.irm.Database.DatabaseTable;
import org.irestaurant.irm.Database.Number;
import org.irestaurant.irm.Database.Ordered;
import org.irestaurant.irm.Database.OredredAdapter;
import org.irestaurant.irm.Database.PayAdapter;
import org.irestaurant.irm.Database.Revenue;
import org.irestaurant.irm.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PayActivity extends Activity {
    TextView tvTotal, tvTotalAll, tvNumber;
    EditText edtDiscount;
    ListView lvOrdered;
    Button btnPay, btnCancel;
    String getIdNumber, getNumber, total, totalall, discount;
    Switch swPrint;
    long tongtien, after;

    List<Ordered> payList;
    PayAdapter payAdapter;
    DatabaseOrdered databaseOrdered;
    DatabaseRevenue databaseRevenue;
    DatabaseTable databaseTable;

    private void Anhxa (){
        tvTotal     = findViewById(R.id.tv_tong);
        tvTotalAll  = findViewById(R.id.tv_totalall);
        tvNumber    = findViewById(R.id.tv_table);
        edtDiscount = findViewById(R.id.edt_discount);
        lvOrdered   = findViewById(R.id.lv_ordered);
        btnCancel   = findViewById(R.id.btn_cancel);
        btnPay      = findViewById(R.id.btn_pay);
        swPrint     = findViewById(R.id.sw_print);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        Anhxa();
        Intent intent = getIntent();
        getIdNumber = intent.getExtras().getString("idnumber");
        getNumber = intent.getExtras().getString("number");
        tvNumber.setText("Bàn số: "+getNumber);

        databaseOrdered = new DatabaseOrdered(this);
        payList = databaseOrdered.getallOrdered(getNumber);
        setLvPay();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        edtDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String Sdiscount = edtDiscount.getText().toString().trim();

                if (!Sdiscount.equals("")){
                    long discount =Integer.valueOf(Sdiscount);
                    if (discount>100){
                        edtDiscount.setText("100");
                        tvTotalAll.setText("0");
                        after = 0;
                    }else {
                        after = tongtien-(tongtien*discount/100);
                        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                        formatter.applyPattern("#,###,###,###");
                        tvTotalAll.setText(formatter.format(after));
                    }
                } else {
                    tvTotalAll.setText(tvTotal.getText());
                    after = tongtien;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String Sdiscount = edtDiscount.getText().toString().trim();
                if (Sdiscount.isEmpty()){
                    tvTotalAll.setText(tvTotal.getText());
                    after = tongtien;
                }

            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPay();
            }
        });
    }

    private void addPay() {
        String date = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());
        String rdate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        String time = new SimpleDateFormat("hh:mm", Locale.getDefault()).format(new Date());
        total = tvTotal.getText().toString().replaceAll("'","");
        totalall = tvTotalAll.getText().toString().replaceAll("'","");
        if (edtDiscount.getText().toString().isEmpty()){
            discount = "0";
        }else {discount = edtDiscount.getText().toString();}
        databaseRevenue = new DatabaseRevenue(this);
        Revenue revenue = new Revenue();
        revenue.setDate(date);
        revenue.setRdate(rdate);
        revenue.setTime(time);
        revenue.setNumber(getNumber);
        revenue.setTotal(String.valueOf(tongtien));
        revenue.setDiscount(discount);
        revenue.setTotalat(String.valueOf(after));
        if (databaseRevenue.creat(revenue)){
            Toast.makeText(PayActivity.this, "Đã thanh toán bàn số "+ getNumber, Toast.LENGTH_LONG).show();
            updateOrdered();
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    public void setLvPay() {
        if (payAdapter == null) {
            tongtien=0;
            payAdapter = new PayAdapter(PayActivity.this, R.layout.item_pay, payList);
            lvOrdered.setAdapter(payAdapter);
            for (int a =0; a<payList.size();a++){
                tongtien += Integer.valueOf(payList.get(a).getTotal());
            }
            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            formatter.applyPattern("#,###,###,###");
            tvTotal.setText(formatter.format(tongtien));
            tvTotalAll.setText(formatter.format(tongtien));
        } else {
            tongtien=0;
            payList.clear();
            payList.addAll(databaseOrdered.getallOrdered(getNumber));
            payAdapter.notifyDataSetChanged();
            lvOrdered.setSelection(payAdapter.getCount() - 1);
            for (int a =0; a<payList.size();a++){
                tongtien += Integer.valueOf(payList.get(a).getTotal());
            }
            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            formatter.applyPattern("#,###,###,###");
            tvTotal.setText(formatter.format(tongtien));
            tvTotalAll.setText(formatter.format(tongtien));
        }
    }

    private void updateTable (String tb){
        databaseTable = new DatabaseTable(this);
        Number number = new Number();
        number.setStatus("free");
        databaseTable.updateTable(number, tb);
        startActivity(new Intent(PayActivity.this, MainActivity.class));
        finish();
    }

    private void updateOrdered (){
        databaseOrdered = new DatabaseOrdered(this);
        Ordered ordered = new Ordered();
        String date = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());
//        ordered.setNumber(getNumber);
//        ordered.setFoodname(foodname);
//        ordered.setAmount(newamout);
        ordered.setStatus("done");
        ordered.setDate(date);
//        ordered.setPrice(price);
//        ordered.setTotal(newtotal);
        int result = databaseOrdered.updateOrderedPaid(ordered, getNumber);
        if (result>0){
            updateTable(getNumber);
        }
    }
}
