package org.irestaurant.irm;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.irestaurant.irm.Database.Config;
import org.irestaurant.irm.Database.RevenueDetail;
import org.irestaurant.irm.Database.RevenueDetailAdapter;
import org.irestaurant.irm.Database.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class RevenueDetailActivity extends Activity {
    Button btnBack;
    TextView tvDate;
    SessionManager sessionManager;
    String getResEmail, getDate, getRDate;
    RecyclerView lvRevenueDetail;
    List<RevenueDetail> revenueDetailList;
    RevenueDetailAdapter revenueDetailAdapter;
    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue_detail);
        sessionManager = new SessionManager(this);
        Map<String, String>user = sessionManager.getUserDetail();
        getResEmail = user.get(sessionManager.RESEMAIL);
        btnBack = findViewById(R.id.btn_back);
        tvDate  = findViewById(R.id.tv_date);
        lvRevenueDetail = findViewById(R.id.lv_revenuedetail);
        Intent intent = getIntent();
        getDate = intent.getExtras().getString("date");
        getRDate = intent.getExtras().getString("rdate");
        tvDate.setText(getDate);

        revenueDetailList = new ArrayList<>();
        revenueDetailAdapter = new RevenueDetailAdapter(this, revenueDetailList);
        lvRevenueDetail.setHasFixedSize(true);
        lvRevenueDetail.setLayoutManager(new LinearLayoutManager(this));
        lvRevenueDetail.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        lvRevenueDetail.setAdapter(revenueDetailAdapter);

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
        revenueDetailList.clear();
        mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.HISTORY).document(getRDate).collection(Config.PAID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e!=null){
                    Toast.makeText(RevenueDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }else {
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                        switch (doc.getType()){
                            case ADDED:
                                RevenueDetail revenueDetail = doc.getDocument().toObject(RevenueDetail.class);
                                revenueDetailList.add(revenueDetail);
                                revenueDetailAdapter.notifyDataSetChanged();
                                break;
                        }
                    }
                }
            }
        });
    }
}
