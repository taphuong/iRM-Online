package org.irestaurant.irm;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
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
    String getResName, getEmail, getResEmail;
    TextView tvResname;
    Button btnHome, btnAddFood, btnSearch, btnClose;
    SearchView svSearch;
    EditText edtPrice;
    RecyclerView lvFood;
    public List<Food> foodList;
    public FoodAdapter foodAdapter;
    RelativeLayout layoutMenu;

    public FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    private void Anhxa(){
        btnHome     = findViewById(R.id.btn_home);
        btnAddFood  = findViewById(R.id.btn_addfood);
        lvFood      = findViewById(R.id.lv_food);
        tvResname   = findViewById(R.id.tv_resname);
        btnSearch   = findViewById(R.id.btn_search);
        btnClose    = findViewById(R.id.btn_close);
        svSearch    = findViewById(R.id.sv_search);
        layoutMenu  = findViewById(R.id.menu);
        int id = svSearch.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        TextView svText = (TextView) svSearch.findViewById(id);
        svText.setTextColor(Color.BLACK);
//        searchEditText.setHintTextColor(getResources().getColor(R.color.mau_xanhbien));
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
        getResEmail = user.get(sessionManager.RESEMAIL);
        tvResname.setText(getResName);

        foodList = new ArrayList<>();
        foodAdapter = new FoodAdapter(this, foodList, MenuActivity.this);
        lvFood.setHasFixedSize(true);
        lvFood.setLayoutManager(new LinearLayoutManager(this));
        lvFood.setAdapter(foodAdapter);

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
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSearch.setVisibility(View.GONE);
                btnClose.setVisibility(View.VISIBLE);
                TranslateAnimation animate = new TranslateAnimation(layoutMenu.getWidth(),0,0,0);
                animate.setDuration(300);
                animate.setFillAfter(true);
                svSearch.startAnimation(animate);
                svSearch.setVisibility(View.VISIBLE);
                svSearch.requestFocus();
                svSearch.onActionViewExpanded();

            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSearch.setVisibility(View.VISIBLE);
                btnClose.setVisibility(View.GONE);
                TranslateAnimation animate = new TranslateAnimation(0,layoutMenu.getWidth(),0,0);
                animate.setDuration(300);
                animate.setFillAfter(true);
                svSearch.startAnimation(animate);
                svSearch.setVisibility(View.GONE);
            }
        });
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                foodAdapter.getFilter().filter(newText);
                return true;
            }
        });

    }

    private void addFood(){
        final Dialog dialog = new Dialog(MenuActivity.this);
        dialog.setContentView(R.layout.dialog_addfood);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        Button btnClose     = (Button) dialog.findViewById(R.id.btn_close);
        final TextView tvThem = dialog.findViewById(R.id.themmon);
        final Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);
        final EditText edtFoodname = (EditText) dialog.findViewById(R.id.edt_foodname);
        final RelativeLayout layoutAdd = dialog.findViewById(R.id.layout_add);
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
                    tvThem.setText("Đang thêm ...");
                    layoutAdd.setVisibility(View.GONE);
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
        String foodID = Config.VNCharacterUtils.removeAccent(foodname).trim();

        Map<String, Object> nameMap = new HashMap<>();
        nameMap.put(Config.FOODNAME, foodname);
        nameMap.put(Config.FOODPRICE, foodprice);
        mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.MENU).document(foodID).set(nameMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MenuActivity.this, R.string.dacoloi+"\n"+e, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    public void reloadMenu (){
        finish();
        startActivity(getIntent());
    }


    @Override
    protected void onStart() {
        super.onStart();
        foodList.clear();
        mFirestore.collection(Config.RESTAURANTS+"/"+getResEmail+"/"+Config.MENU).addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null){
                    Toast.makeText(MenuActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }else {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()){
                        String foodId = doc.getDocument().getId();
                        switch (doc.getType()){
                            case ADDED:
                                Food food = doc.getDocument().toObject(Food.class).withId(foodId);
                                foodList.add(food);
                                foodAdapter.notifyDataSetChanged();
                                break;
                            case REMOVED:

                                break;
                        }
                    }
                }


            }
        });
    }
}
