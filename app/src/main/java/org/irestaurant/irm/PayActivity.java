package org.irestaurant.irm;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.irestaurant.irm.Database.DatabaseOrdered;
import org.irestaurant.irm.Database.Ordered;
import org.irestaurant.irm.Database.OredredAdapter;
import org.irestaurant.irm.Database.PayAdapter;
import org.irestaurant.irm.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PayActivity extends Activity {
    TextView tvTotal, tvTotalAll, tvNumber;
    EditText edtDiscount;
    ListView lvOrdered;
    Button btnPay, btnCancel;
    String getIdNumber, getNumber;
    long tongtien, after;

    List<Ordered> payList;
    PayAdapter payAdapter;
    DatabaseOrdered databaseOrdered;

    private void Anhxa (){
        tvTotal     = findViewById(R.id.tv_tong);
        tvTotalAll  = findViewById(R.id.tv_totalall);
        tvNumber    = findViewById(R.id.tv_table);
        edtDiscount = findViewById(R.id.edt_discount);
        lvOrdered   = findViewById(R.id.lv_ordered);
        btnCancel   = findViewById(R.id.btn_cancel);
        btnPay      = findViewById(R.id.btn_pay);
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
                        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
                        tvTotalAll.setText(decimalFormat.format(after));
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
}
