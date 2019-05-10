package org.irestaurant.irm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
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
import org.irestaurant.irm.Interface.LinearLayoutManagerWithSmoothScrooler;
import org.irestaurant.irm.Interface.WrapContentLinearLayoutManager;

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
    public ArrayList<Food> foodList;
    public FoodAdapter foodAdapter;
    RelativeLayout layoutMenu;
    LinearLayoutManager layoutManager;

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
        foodAdapter = new FoodAdapter(this, foodList);
        layoutManager = new LinearLayoutManagerWithSmoothScrooler(this);
//        layoutManager = new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        lvFood.setLayoutManager(layoutManager);
        lvFood.setHasFixedSize(true);
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
                final android.support.v7.widget.PopupMenu popupMenu = new android.support.v7.widget.PopupMenu(MenuActivity.this,v);
                popupMenu.getMenuInflater().inflate(R.menu.addfood_popup,popupMenu.getMenu());
                popupMenu.setGravity(Gravity.RIGHT);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.popup_category:
                                addCategory();
                                break;
                            case R.id.popup_food:
                                addFood();

                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
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

    private void addCategory(){
        final Dialog dialog = new Dialog(MenuActivity.this);
        dialog.setContentView(R.layout.dialog_addcategory);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        final RelativeLayout layoutAdd = dialog.findViewById(R.id.layout_add);
        final TextView tvAdd = dialog.findViewById(R.id.tv_category);
        final EditText edtCategory = dialog.findViewById(R.id.edt_category);
        final Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);
        Button btnClose = dialog.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtCategory.getText().toString().isEmpty()){
                    edtCategory.setError("Thiếu thông tin");
                    edtCategory.requestFocus();
                }else {
                    String name = edtCategory.getText().toString();
                    tvAdd.setText("Đang thêm danh mục ...");
                    layoutAdd.setVisibility(View.GONE);
                    registCategory(name, dialog, layoutAdd);

                }
            }
        });
        dialog.show();
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
        final Spinner spnCategory = dialog.findViewById(R.id.spn_category);
        final List<String> listCategory = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_item,listCategory);
        spnCategory.setAdapter(adapter);

        mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.MENU).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null){
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                        String categoryID = doc.getDocument().getId();
                        switch (doc.getType()){
                            case ADDED:
                                String first = categoryID.substring(0,1);
                                if (first.equals("0")){
                                    String categoryName = doc.getDocument().getString(Config.GROUP);
                                    listCategory.add(categoryName);
                                    adapter.notifyDataSetChanged();

                                    if (listCategory.isEmpty()){
                                        dialog.dismiss();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
                                        builder.setMessage("Chưa có danh mục thực phẩm. Bạn có muốn thêm danh mục không");
                                        builder.setCancelable(false);
                                        builder.setPositiveButton("Không", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                                        builder.setNegativeButton("Thêm", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                addCategory();
                                            }
                                        });
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    }
                                }

                                break;
                        }
                    }
                }
            }
        });


        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

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
                    String group = spnCategory.getSelectedItem().toString();
                    registFood(name, price,dialog, group);
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

    private void registCategory (String name, final Dialog dialog, final RelativeLayout layoutAdd){
        String categoryID = "0"+Config.VNCharacterUtils.removeAccent(name).trim();
        Map<String, Object> categoryMap = new HashMap<>();
        categoryMap.put(Config.FOODNAME, name);
        categoryMap.put(Config.FOODPRICE, "0");
        categoryMap.put(Config.GROUP, name);
        categoryMap.put(Config.VIEWTYPE, Config.VIEWTYPEGROUP);
        mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.MENU).document(categoryID).set(categoryMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MenuActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MenuActivity.this, R.string.dacoloi, Toast.LENGTH_SHORT).show();
                layoutAdd.setVisibility(View.VISIBLE);
            }
        });
    }

    private void registFood (String foodname, String foodprice, final Dialog dialog, String group){
        String foodID = Config.VNCharacterUtils.removeAccent(foodname).trim();

        Map<String, Object> nameMap = new HashMap<>();
        nameMap.put(Config.FOODNAME, foodname);
        nameMap.put(Config.FOODPRICE, foodprice);
        nameMap.put(Config.GROUP, group);
        nameMap.put(Config.VIEWTYPE, Config.VIEWTYPEITEM);
        mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.MENU).document(foodID).set(nameMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                foodAdapter.notifyDataSetChanged();
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

//Sticky


    public ArrayList<String> getData() {
        final ArrayList<String> list = new ArrayList<String>();
        mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.MENU).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null){
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                        String categoryID = doc.getDocument().getId();
                        switch (doc.getType()){
                            case ADDED:
                                String first = categoryID.substring(0,1);
                                if (first.equals("0")){
                                    String categoryName = doc.getDocument().getString(Config.GROUP);
                                    list.add(categoryName);

                                }

                                break;
                        }
                    }
                }
            }
        });

        return list;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Config.CHECKACTIVITY = "MenuActivity";
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
                                foodList = Config.sortList(foodList);
                                foodList.add(food);
//                                foodList = Config.foodGroupArrayList(foodList);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode,@Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK){

            String groupClick = data.getStringExtra("result");
            if (groupClick.equals("lastposition")){
                lvFood.smoothScrollToPosition(foodList.size());
            }else {
                for (int i =0; i<foodList.size(); i++){
                    if (foodList.get(i).getGroup().equals(groupClick)) {
                        break;
                    }
                    lvFood.smoothScrollToPosition(i);
                }
            }
        }
    }
}
