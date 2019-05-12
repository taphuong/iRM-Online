package org.irestaurant.irm;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.irestaurant.irm.Database.Config;
import org.irestaurant.irm.Database.Ordered;
import org.irestaurant.irm.Database.OredredAdapter;
import org.irestaurant.irm.Database.Recent;
import org.irestaurant.irm.Database.RecentAdapter;
import org.irestaurant.irm.Database.SessionManager;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

public class RecentActivity extends Activity {
    String getResName, getResPhone, getResAddress, getResEmail, number, date, time, getIdRecent, rDate;
    TextView tvResName, tvResPhone, tvResAddress, tvNumber, tvDate, tvTotal, tvTotalAll, tvDiscount, tvTC, tvCK;
    RecyclerView lvRecent;
    Button btnBack;
    SessionManager sessionManager;
    RecentAdapter recentAdapter;
    List<Recent> recentList;
    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
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
        HashMap<String, String> user = sessionManager.getUserDetail();
        getResName = user.get(sessionManager.RESNAME);
        getResPhone = user.get(sessionManager.RESPHONE);
        getResAddress = user.get(sessionManager.RESADDRESS);
        getResEmail = user.get(sessionManager.RESEMAIL);

        tvResName.setText(getResName);
        tvResPhone.setText(getResPhone);
        tvResAddress.setText(getResAddress);

        recentList = new ArrayList<>();
        recentAdapter = new RecentAdapter(this, recentList);
        lvRecent.setHasFixedSize(true);
        lvRecent.setLayoutManager(new LinearLayoutManager(this));
        lvRecent.setAdapter(recentAdapter);

        Intent intent = getIntent();
        date = intent.getExtras().getString("date");
        time = intent.getExtras().getString("time");
        number = intent.getExtras().getString("number");
        String total = intent.getExtras().getString("total");
        String discount = intent.getExtras().getString("discount");
        getIdRecent = intent.getExtras().getString("id");
        String before = intent.getExtras().getString("before");
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###,###,###");
        tvNumber.setText("Bàn số: "+number);
        tvDate.setText(date+"  "+time);
        tvTotal.setText(formatter.format(Integer.valueOf(before)));

        String dd = date.substring(0,2);
        String MM = date.substring(3,5);
        String yyyy = date.substring(6,10);
        rDate = yyyy+MM+dd;

        if (discount.equals("0")){
            tvTC.setVisibility(View.GONE);
            tvCK.setVisibility(View.GONE);
            tvDiscount.setVisibility(View.GONE);
            tvTotal.setVisibility(View.GONE);
        }else {
            tvDiscount.setText(discount+"%");
        }
        tvTotalAll.setText(formatter.format(Integer.valueOf(total)));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        recentList.clear();
        mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.HISTORY).document(rDate).collection(Config.PAID).document(getIdRecent).collection(Config.FOOD).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                        switch (doc.getType()){
                            case ADDED:
                                Recent recent = doc.getDocument().toObject(Recent.class);
                                recentList.add(recent);
                                recentAdapter.notifyDataSetChanged();
                                break;
                        }
                    }
            }
        });
    }
}
