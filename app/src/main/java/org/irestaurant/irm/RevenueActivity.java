package org.irestaurant.irm;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.irestaurant.irm.Database.Config;
import org.irestaurant.irm.Database.Revenue;
import org.irestaurant.irm.Database.RevenueAdapter;
import org.irestaurant.irm.Database.SessionManager;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

public class RevenueActivity extends Activity {

    Button btnHome;
    EditText edtStart,edtEnd;
    TextView tvTotalAll;
    RecyclerView lvRevenue;
    String dateStart, dateEnd, rdateS, rdateE, getResEmail;
    long tongtien;

    List<Revenue> revenueList;
    RevenueAdapter revenueAdapter;
    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    SessionManager sessionManager;

    private void Anhxa(){
        btnHome = findViewById(R.id.btn_home);
        edtStart= findViewById(R.id.edt_start);
        edtEnd  = findViewById(R.id.edt_end);
        tvTotalAll = findViewById(R.id.tv_totalall);
        lvRevenue = findViewById(R.id.lv_revenue);
        dateStart = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        dateEnd = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        rdateS = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        rdateE = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        edtStart.setText(dateStart);
        edtEnd.setText(dateEnd);
        edtStart.setFocusable(false);
        edtEnd.setFocusable(false);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue);
        sessionManager = new SessionManager(this);
        Map<String, String> user = sessionManager.getUserDetail();
        getResEmail = user.get(sessionManager.RESEMAIL);
        Anhxa();

        revenueList = new ArrayList<>();
        revenueAdapter = new RevenueAdapter(this, revenueList);
        lvRevenue.setHasFixedSize(true);
        lvRevenue.setLayoutManager(new LinearLayoutManager(this));
        lvRevenue.setAdapter(revenueAdapter);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        edtStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateStart();
            }
        });
        edtEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateEnd();
            }
        });
    }

    private void dateStart() {
        final Calendar c = Calendar.getInstance();
        String date = edtStart.getText().toString().replaceAll("/","");
        int dd = Integer.valueOf(date.substring(0,2));
        int MM = Integer.valueOf(date.substring(2,4));
        int yyyy = Integer.valueOf(date.substring(date.length()-4));
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                c.set(year, month,dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                rdateS = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(c.getTime());
                if (Integer.valueOf(rdateS)>Integer.valueOf(rdateE)){
                    Toast.makeText(RevenueActivity.this, "Ngày bắt đầu lớn hơn ngày kết thúc", Toast.LENGTH_SHORT).show();


                }
                else {setLvRevenue(rdateS, rdateE);
                    edtStart.setText(simpleDateFormat.format(c.getTime()));}
//                rDateStart.setText(rdatestart);


            }
        }, yyyy, MM-1, dd);
        datePickerDialog.show();
    }
    private void dateEnd() {
        final Calendar c = Calendar.getInstance();
        String date = edtStart.getText().toString().replaceAll("/","");
        int dd = Integer.valueOf(date.substring(0,2));
        int MM = Integer.valueOf(date.substring(2,4));
        int yyyy = Integer.valueOf(date.substring(date.length()-4));
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                c.set(year, month,dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                rdateE = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(c.getTime());
//                rDateEnd.setText(rdateend);

                if (Integer.valueOf(rdateS)>Integer.valueOf(rdateE)){

                    Toast.makeText(RevenueActivity.this, "Ngày bắt đầu lớn hơn ngày kết thúc", Toast.LENGTH_SHORT).show();
                }
                else {setLvRevenue(rdateS, rdateE);
                    edtEnd.setText(simpleDateFormat.format(c.getTime()));}
            }
        }, yyyy, MM-1, dd);
        datePickerDialog.show();
    }

    public void setLvRevenue(final String dateS, final String dateE) {
        mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.HISTORY).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                tongtien=0;
                revenueList.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    String total = documentSnapshot.getString(Config.TOTAL);
                    String ID = documentSnapshot.getId();
                    if (Integer.valueOf(ID)>=Integer.valueOf(dateS) && Integer.valueOf(ID)<=Integer.valueOf(dateE)) {
                        Revenue revenue = documentSnapshot.toObject(Revenue.class).withId(ID);
                        revenueList.add(revenue);
                        revenueAdapter.notifyDataSetChanged();
                        tongtien += Integer.valueOf(total);
                        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                        formatter.applyPattern("#,###,###,###");
                        tvTotalAll.setText(formatter.format(tongtien));
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        revenueList.clear();
        mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.HISTORY).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e !=null){}
                else {
                    tongtien = 0;
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                        String ID = doc.getDocument().getId();
                        String total = doc.getDocument().getString("total");
                        switch (doc.getType()){
                            case ADDED:
                                Revenue revenue = doc.getDocument().toObject(Revenue.class).withId(ID);
                                if (Integer.valueOf(ID)>= Integer.valueOf(rdateS) && Integer.valueOf(ID)<= Integer.valueOf(rdateE)){

                                    revenueList.add(revenue);
                                    revenueAdapter.notifyDataSetChanged();
                                    tongtien += Integer.valueOf(total);
                                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                                    formatter.applyPattern("#,###,###,###");
                                    tvTotalAll.setText(formatter.format(tongtien));
                                }
                                break;
                        }
                    }
                }
            }
        });
    }
}
