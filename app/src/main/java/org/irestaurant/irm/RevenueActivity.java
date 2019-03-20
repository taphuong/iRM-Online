package org.irestaurant.irm;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.irestaurant.irm.Database.DatabaseRevenue;
import org.irestaurant.irm.Database.Revenue;
import org.irestaurant.irm.Database.RevenueAdapter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RevenueActivity extends Activity {

    Button btnHome;
    EditText edtStart,edtEnd;
    TextView tvTotalAll;
    ListView lvRevenue;
    String dateStart, dateEnd, rdateS, rdateE;
    long tongtien;

    DatabaseRevenue databaseRevenue;
    List<Revenue> revenueList;
    RevenueAdapter revenueAdapter;

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
        Anhxa();
        databaseRevenue = new DatabaseRevenue(this);
        revenueList = databaseRevenue.getallRevenue(rdateS,rdateE);
        setLvRevenue(rdateS,rdateE);

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

    public void setLvRevenue(String dateS, String dateE) {
        if (revenueAdapter == null) {
            tongtien=0;
            revenueAdapter = new RevenueAdapter(RevenueActivity.this, R.layout.item_revenue, revenueList);
            lvRevenue.setAdapter(revenueAdapter);
            for (int a =0; a<revenueList.size();a++){
                tongtien += Integer.valueOf(revenueList.get(a).getTotalat());
            }
            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            formatter.applyPattern("#,###,###,###");
            tvTotalAll.setText(formatter.format(tongtien));
        } else {
            tongtien=0;
            revenueList.clear();
            revenueList.addAll(databaseRevenue.getallRevenue(dateS, dateE));
            revenueAdapter.notifyDataSetChanged();
            lvRevenue.setSelection(revenueAdapter.getCount() - 1);
            for (int a =0; a<revenueList.size();a++){
                tongtien += Integer.valueOf(revenueList.get(a).getTotalat());
            }
            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            formatter.applyPattern("#,###,###,###");
            tvTotalAll.setText(formatter.format(tongtien));
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
                edtStart.setText(simpleDateFormat.format(c.getTime()));
                rdateS = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(c.getTime());
                if (Integer.valueOf(rdateS)>Integer.valueOf(rdateE)){
                    Toast.makeText(RevenueActivity.this, "Ngày bắt đầu lớn hơn này kết thúc", Toast.LENGTH_SHORT).show();
                }
                else {setLvRevenue(rdateS, rdateE);}
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
                edtEnd.setText(simpleDateFormat.format(c.getTime()));
                rdateE = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(c.getTime());
//                rDateEnd.setText(rdateend);

                if (Integer.valueOf(rdateS)>Integer.valueOf(rdateE)){
                    Toast.makeText(RevenueActivity.this, "Ngày bắt đầu lớn hơn này kết thúc", Toast.LENGTH_SHORT).show();
                }
                else {setLvRevenue(rdateS, rdateE);}
            }
        }, yyyy, MM-1, dd);
        datePickerDialog.show();
    }
}
