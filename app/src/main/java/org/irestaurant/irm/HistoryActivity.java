package org.irestaurant.irm;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import org.irestaurant.irm.Database.DatabaseRevenue;
import org.irestaurant.irm.Database.HistoryAdapter;
import org.irestaurant.irm.Database.Revenue;
import org.irestaurant.irm.Database.RevenueAdapter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends Activity {
    Button btnHome;
    EditText edtStart,edtEnd;
    GridView gvHistory;
    String dateStart, dateEnd, rdateS, rdateE;

    DatabaseRevenue databaseRevenue;
    List<Revenue> hitoryList;
    HistoryAdapter historyAdapter;

    private void Anhxa(){
        btnHome = findViewById(R.id.btn_home);
        edtStart= findViewById(R.id.edt_start);
        edtEnd  = findViewById(R.id.edt_end);
        gvHistory = findViewById(R.id.gv_history);
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
        setContentView(R.layout.activity_history);
        Anhxa();

        databaseRevenue = new DatabaseRevenue(this);
        hitoryList = databaseRevenue.getallRevenue(rdateS,rdateE);
        setGvHistory(rdateS,rdateE);

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

        gvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String date = hitoryList.get(position).getDate();
                String time = hitoryList.get(position).getTime();
                String number = hitoryList.get(position).getNumber();
                String total = hitoryList.get(position).getTotal();
                String discount = hitoryList.get(position).getDiscount();
                String totalall = hitoryList.get(position).getTotalat();
                Intent i = new Intent(HistoryActivity.this, RecentActivity.class);
                i.putExtra("date", date);
                i.putExtra("time", time);
                i.putExtra("number", number);
                i.putExtra("total", total);
                i.putExtra("discount", discount);
                i.putExtra("totalall", totalall);
                startActivity(i);
            }
        });
    }

    private void setGvHistory(String rdateS, String rdateE) {
        if (historyAdapter == null) {
            historyAdapter = new HistoryAdapter(HistoryActivity.this, R.layout.item_revenue, hitoryList);
            gvHistory.setAdapter(historyAdapter);
        } else {
            hitoryList.clear();
            hitoryList.addAll(databaseRevenue.getallRevenue(rdateS, rdateE));
            historyAdapter.notifyDataSetChanged();
            gvHistory.setSelection(historyAdapter.getCount() - 1);
        }
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
                    Toast.makeText(HistoryActivity.this, "Ngày bắt đầu lớn hơn này kết thúc", Toast.LENGTH_SHORT).show();


                }
                else {setGvHistory(rdateS, rdateE);
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
//        int mYear = c.get(Calendar.YEAR);
//        int mMonth = c.get(Calendar.MONTH);
//        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                c.set(year, month,dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                rdateE = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(c.getTime());
//                rDateEnd.setText(rdateend);

                if (Integer.valueOf(rdateS)>Integer.valueOf(rdateE)){

                    Toast.makeText(HistoryActivity.this, "Ngày bắt đầu lớn hơn này kết thúc", Toast.LENGTH_SHORT).show();
                }
                else {setGvHistory(rdateS, rdateE);
                    edtEnd.setText(simpleDateFormat.format(c.getTime()));}
            }
        }, yyyy, MM-1, dd);
        datePickerDialog.show();
    }
}
