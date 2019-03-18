package org.irestaurant.irm;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.irestaurant.irm.Database.DatabaseFood;
import org.irestaurant.irm.Database.DatabaseOrdered;
import org.irestaurant.irm.Database.Food;
import org.irestaurant.irm.Database.FoodAdapter;
import org.irestaurant.irm.Database.FoodOrderedAdapter;
import org.irestaurant.irm.Database.Ordered;
import org.irestaurant.irm.Database.OredredAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderedActivity extends Activity {
    ListView lvFood, lvOrdered;
    TextView tvNumber, tvIdNumber, tvTotal, tvDate;
    public static String getIdNumber, getNumber;
    Button btnHome;
    List<Food> foodList;
    List<Ordered>orderedList;
    FoodOrderedAdapter foodOrderedAdapter;
    OredredAdapter oredredAdapter;

    DatabaseFood databaseFood;
    DatabaseOrdered databaseOrdered;

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
        tvNumber.setText(getNumber);
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
            oredredAdapter = new OredredAdapter(OrderedActivity.this, R.layout.item_ordered, orderedList);
            lvOrdered.setAdapter(oredredAdapter);
        } else {
            orderedList.clear();
            orderedList.addAll(databaseOrdered.getallOrdered(getNumber));
            oredredAdapter.notifyDataSetChanged();
            lvOrdered.setSelection(oredredAdapter.getCount() - 1);
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
