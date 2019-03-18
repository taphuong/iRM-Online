package org.irestaurant.irm;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.irestaurant.irm.Database.DatabaseFood;
import org.irestaurant.irm.Database.Food;
import org.irestaurant.irm.Database.FoodAdapter;
import org.irestaurant.irm.Database.FoodOrderedAdapter;

import java.util.List;

public class OrderedActivity extends Activity {
    ListView lvFood, lvOrdered;
    TextView tvNumber, tvIdNumber, tvTitle, tvTotal;
    String getIdNumber, getNumber;
    Button btnHome;
    List<Food> foodList;
    FoodOrderedAdapter foodOrderedAdapter;
    DatabaseFood databaseFood;

    private void Anhxa(){
        lvFood = findViewById(R.id.lv_food);
        lvOrdered = findViewById(R.id.lv_ordered);
        tvTitle = findViewById(R.id.tv_title);
        tvNumber = findViewById(R.id.tv_number);
        tvIdNumber = findViewById(R.id.tv_idnumber);
        tvTotal = findViewById(R.id.tv_total);
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
        tvTitle.setText("Bàn số: "+getNumber);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        databaseFood = new DatabaseFood(this);
        foodList = databaseFood.getallFood();
        setLvFood();
    }

    public void setLvFood() {
        if (foodOrderedAdapter == null) {
            foodOrderedAdapter = new FoodOrderedAdapter(OrderedActivity.this, R.layout.item_table, foodList);
            lvFood.setAdapter(foodOrderedAdapter);
        } else {
            foodList.clear();
            foodList.addAll(databaseFood.getallFood());
            foodOrderedAdapter.notifyDataSetChanged();
            lvFood.setSelection(foodOrderedAdapter.getCount() - 1);
        }
    }
}
