package org.irestaurant.irm;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.irestaurant.irm.Database.Config;
import org.irestaurant.irm.Database.Food;
import org.irestaurant.irm.Database.FoodAdapter;
import org.irestaurant.irm.Database.SessionManager;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

public class MenuActivity extends Activity {
    SessionManager sessionManager;
    String getResName, getEmail;
    TextView tvResname;
    Button btnHome, btnAddFood;
    EditText edtPrice;
    RecyclerView lvFood;
    List<Food> foodList;
    FoodAdapter foodAdapter;

    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

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
        getEmail = user.get(sessionManager.EMAIL);
        tvResname.setText(getResName);

        foodList = new ArrayList<>();
        foodAdapter = new FoodAdapter(this, foodList);
        lvFood.setHasFixedSize(true);
        lvFood.setLayoutManager(new LinearLayoutManager(this));
        lvFood.setAdapter(foodAdapter);

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
    private void registFood (String foodname, String foodprice, final Dialog dialog){
        Map<String, Object> nameMap = new HashMap<>();
        nameMap.put(Config.FOODNAME, foodname);
        nameMap.put(Config.FOODPRICE, foodprice);
        mFirestore.collection("Restaurants").document(getEmail).collection("Menu").document().set(nameMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirestore.collection("Restaurant").document(getEmail).collection("Menu").addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Toast.makeText(MenuActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }else {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()){
                        if (doc.getType() == DocumentChange.Type.ADDED){
                            Food food = doc.getDocument().toObject(Food.class);
                            foodList.add(food);
                            foodAdapter.notifyDataSetChanged();
                        }
                    }
                }

            }
        });
    }
}
