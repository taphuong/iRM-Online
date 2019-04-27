package org.irestaurant.irm;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.irestaurant.irm.Database.Ordered;
import org.irestaurant.irm.Database.OredredAdapter;
import org.irestaurant.irm.Database.RecentAdapter;
import org.irestaurant.irm.Database.SessionManager;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RecentActivity extends Activity {
    String getResName, getResPhone, getResAddress, number, date, time;
    TextView tvResName, tvResPhone, tvResAddress, tvNumber, tvDate, tvTotal, tvTotalAll, tvDiscount, tvTC, tvCK;
    ListView lvRecent;
    Button btnBack;
    SessionManager sessionManager;
    RecentAdapter recentAdapter;
    List<Ordered> orderedList;

    private void Anhxa(){
        tvResName = findViewById(R.id.tv_resname);
        tvResPhone = findViewById(R.id.tv_resphone);
        tvResAddress = findViewById(R.id.tv_resaddress);
        tvNumber = findViewById(R.id.tv_number);
        tvDate = findViewById(R.id.tv_date);
        tvTotal = findViewById(R.id.tv_total);
        tvDiscount = findViewById(R.id.tv_discount);
        tvTC        = findViewById(R.id.tv_tc);
        tvCK        = findViewById(R.id.tv_ck);
        tvTotalAll = findViewById(R.id.tv_totalall);
        lvRecent = findViewById(R.id.lv_recent);
        btnBack = findViewById(R.id.btn_back);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);
        Anhxa();
        sessionManager = new SessionManager(this);
        sessionManager.checkLoggin();
        HashMap<String, String> user = sessionManager.getUserDetail();
        getResName = user.get(sessionManager.RESNAME);
        getResPhone = user.get(sessionManager.RESPHONE);
        getResAddress = user.get(sessionManager.RESADDRESS);

        tvResName.setText(getResName);
        tvResPhone.setText(getResPhone);
        tvResAddress.setText(getResAddress);

        Intent intent = getIntent();
        date = intent.getExtras().getString("date");
        time = intent.getExtras().getString("time");
        number = intent.getExtras().getString("number");
        String total = intent.getExtras().getString("total");
        String discount = intent.getExtras().getString("discount");
        String totalall = intent.getExtras().getString("totalall");
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###,###,###");

        tvNumber.setText("Bàn số: "+number);
        tvDate.setText(date+"  "+time);
        tvTotal.setText(formatter.format(Integer.valueOf(total)));

        if (discount.equals("0")){
            tvTC.setVisibility(View.GONE);
            tvCK.setVisibility(View.GONE);
            tvDiscount.setVisibility(View.GONE);
            tvTotal.setVisibility(View.GONE);
        }else {
            tvDiscount.setText(discount+"%");
        }

        tvTotalAll.setText(formatter.format(Integer.valueOf(totalall)));

        setLvRecent();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setLvRecent() {
        if (recentAdapter == null) {
            recentAdapter = new RecentAdapter(RecentActivity.this, R.layout.item_recent, orderedList);
            lvRecent.setAdapter(recentAdapter);
        } else {
            orderedList.clear();
//            orderedList.addAll(databaseOrdered.getallRecent(number,date, time));
            recentAdapter.notifyDataSetChanged();
            lvRecent.setSelection(recentAdapter.getCount() - 1);
        }
    }
}
