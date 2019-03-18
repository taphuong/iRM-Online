package org.irestaurant.irm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.irestaurant.irm.Database.DatabaseFood;
import org.irestaurant.irm.Database.Food;
import org.irestaurant.irm.Database.FoodAdapter;

import org.irestaurant.irm.Database.SessionManager;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MenuActivity extends Activity {
    SessionManager sessionManager;
    String getResName;
    TextView tvResname;
    Button btnHome, btnAddFood;
    EditText edtPrice;
    ListView lvFood;
    List<Food> foodList;
    FoodAdapter foodAdapter;
    DatabaseFood databaseFood;

    private void Anhxa(){
        btnHome     = findViewById(R.id.btn_home);
        btnAddFood  = findViewById(R.id.btn_addfood);
        lvFood      = findViewById(R.id.lv_food);
        tvResname   = findViewById(R.id.tv_resname);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Anhxa();
        sessionManager = new SessionManager(this);
        sessionManager.checkLoggin();
        HashMap<String, String> user = sessionManager.getUserDetail();
        getResName = user.get(sessionManager.RESNAME);
        tvResname.setText(getResName);
        databaseFood = new DatabaseFood(this);
        foodList = databaseFood.getallFood();
        setLvFood();


        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFood();
            }
        });

    }
    public void setLvFood() {
        if (foodAdapter == null) {
            foodAdapter = new FoodAdapter(MenuActivity.this, R.layout.item_table, foodList, this);
            lvFood.setAdapter(foodAdapter);
        } else {
            foodList.clear();
            foodList.addAll(databaseFood.getallFood());
            foodAdapter.notifyDataSetChanged();
            lvFood.setSelection(foodAdapter.getCount() - 1);
        }
    }

    private void addFood(){
        final Dialog dialog = new Dialog(MenuActivity.this);
        dialog.setContentView(R.layout.dialog_addfood);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        Button btnClose     = (Button) dialog.findViewById(R.id.btn_close);
        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);
        final EditText edtFoodname = (EditText) dialog.findViewById(R.id.edt_foodname);
        edtPrice = (EditText) dialog.findViewById(R.id.edt_foodprice);
        dialog.show();
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        edtPrice.addTextChangedListener(onTextChangedListener());
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtFoodname.getText().toString();
                String price = edtPrice.getText().toString().replaceAll(",","");
                if (name.isEmpty()){
                    edtFoodname.setError("Nhập tên món ăn (uống)");
                    edtFoodname.requestFocus();
                }else if (price.isEmpty()){
                    edtPrice.setError("Nhập đơn giá");
                    edtPrice.requestFocus();
                }else {
                    registFood(name, price,dialog);
                }
            }
        });
    }
    //    FormatPrice
    private TextWatcher onTextChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                edtPrice.removeTextChangedListener(this);
                try {
                    String originalString = s.toString();
                    Long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Long.parseLong(originalString);
                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    formatter.applyPattern("#,###,###,###");
                    String formattedString = formatter.format(longval);

                    edtPrice.setText(formattedString);
                    edtPrice.setSelection(edtPrice.getText().length());
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
                edtPrice.addTextChangedListener(this);
            }
        };
    }
    private void registFood (String foodname, String foodprice, Dialog dialog){
        databaseFood = new DatabaseFood(this);
        Food food = new Food();
        food.setFoondname(foodname);
        food.setFoodprice(foodprice);
        if (databaseFood.creat(food)){
            foodList.clear();
            foodList.addAll(databaseFood.getallFood());
            setLvFood();
            Toast.makeText(this, "Đã thêm món "+foodname, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
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


}
