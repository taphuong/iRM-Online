package org.irestaurant.irm;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.kekstudio.dachshundtablayout.DachshundTabLayout;

import org.irestaurant.irm.Database.Config;
import org.irestaurant.irm.Database.Food;
import org.irestaurant.irm.Database.FoodOrderedAdapter;
import org.irestaurant.irm.Database.Ordered;
import org.irestaurant.irm.Database.OredredAdapter;
import org.irestaurant.irm.Database.PagerAdapter;
import org.irestaurant.irm.Database.PagerOrderAdapter;
import org.irestaurant.irm.Database.SessionManager;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

public class OrderedActivity extends AppCompatActivity {
    RecyclerView lvFood, lvOrdered;
    TextView tvNumber, tvIdNumber, tvTotal;
    public static String getIdNumber, getNumber;
    long tongtien;
    private long price;
    private ViewPager pager;
    private DachshundTabLayout tabLayout;
    String getResEmail;
    SessionManager sessionManager;
    Button btnHome;

    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    CollectionReference numberRef;

    private void Anhxa(){
        lvFood = findViewById(R.id.lv_food);
        lvOrdered = findViewById(R.id.lv_ordered);
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
        Config.TABLE = getNumber;
        Config.TABLEID = getIdNumber;
        tvIdNumber.setText(getIdNumber);
        tvNumber.setText("Bàn số: "+getNumber);
//        String date = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());
//        tvDate.setText(date);
        sessionManager = new SessionManager(this);
        sessionManager.checkLoggin();
        HashMap<String, String> user = sessionManager.getUserDetail();
        getResEmail = user.get(sessionManager.RESEMAIL);
        numberRef = mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.NUMBER);
        loadTotal();
        addControl();

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(OrderedActivity.this, MainActivity.class));
                finish();
            }
        });


//        lvOrdered.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//                android.support.v7.widget.PopupMenu popupMenu = new android.support.v7.widget.PopupMenu(OrderedActivity.this,view);
//                popupMenu.getMenuInflater().inflate(R.menu.ordered_popup,popupMenu.getMenu());
//                popupMenu.setGravity(Gravity.RIGHT);
//                popupMenu.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem menuItem) {
//                        switch (menuItem.getItemId()){
//                            case R.id.popup_edit:
//                                final Dialog dialog = new Dialog(OrderedActivity.this);
//                                dialog.setContentView(R.layout.dialog_addnumber);
//                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                                dialog.setCanceledOnTouchOutside(false);
//                                final Button btnMinus = (Button) dialog.findViewById(R.id.btn_minus);
//                                final Button btnAdd = (Button) dialog.findViewById(R.id.btn_add);
//                                Button btnClose     = (Button) dialog.findViewById(R.id.btn_close);
//                                Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);
//                                btnConfirm.setText("Lưu");
//                                TextView tvFood = dialog.findViewById(R.id.themban);
//                                tvFood.setText(orderedList.get(position).getFoodname());
//                                final EditText edtAmount = (EditText) dialog.findViewById(R.id.edt_amount);
//                                edtAmount.setText(orderedList.get(position).getAmount());
//                                if (Integer.valueOf(orderedList.get(position).getAmount())>1){
//                                    btnMinus.setVisibility(View.VISIBLE);
//                                }else {btnMinus.setVisibility(View.INVISIBLE);}
//
//                                if (Integer.valueOf(orderedList.get(position).getAmount())>999){
//                                    btnAdd.setVisibility(View.INVISIBLE);
//                                }else {btnAdd.setVisibility(View.VISIBLE);}
//                                price = Integer.valueOf(orderedList.get(position).getPrice());
//                                dialog.show();
//                                btnClose.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        dialog.dismiss();
//                                    }
//                                });
//                                btnAdd.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        btnMinus.setVisibility(View.VISIBLE);
//                                        if (edtAmount.getText().toString().equals("")){
//                                            edtAmount.setText("1");
//                                        } else if (Integer.valueOf(edtAmount.getText().toString())==999){
//                                            edtAmount.setText("1000");
//                                            btnAdd.setVisibility(View.INVISIBLE);
//                                        } else {
//                                            edtAmount.setText(String.valueOf(Integer.valueOf(edtAmount.getText().toString())+1));
//                                        }
//                                    }
//                                });
//                                btnMinus.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        btnAdd.setVisibility(View.VISIBLE);
//                                        String stramount = edtAmount.getText().toString();
//                                        if (stramount.isEmpty() || stramount.equals("2")){
//                                            edtAmount.setText("1");
//                                            btnMinus.setVisibility(View.INVISIBLE);
//                                        }  else {
//                                            long amount = Integer.valueOf(edtAmount.getText().toString());
//                                            edtAmount.setText(String.valueOf(amount-1));
//                                        }
//
//                                    }
//                                });
//                                edtAmount.addTextChangedListener(new TextWatcher() {
//                                    @Override
//                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                                    }
//
//                                    @Override
//                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                                        String amout = edtAmount.getText().toString();
//                                        if (amout.isEmpty() || amout.equals("1") || amout.equals("0")) {
//                                            btnMinus.setVisibility(View.INVISIBLE);
//                                            btnAdd.setVisibility(View.VISIBLE);
//                                        } else if (Integer.valueOf(amout) > 1000) {
//                                            edtAmount.setText("1000");
//                                            edtAmount.setSelection(edtAmount.getText().length());
//                                            btnAdd.setVisibility(View.INVISIBLE);
//                                            btnMinus.setVisibility(View.VISIBLE);
//                                        } else {
//                                            btnMinus.setVisibility(View.VISIBLE);
//                                            btnAdd.setVisibility(View.VISIBLE);
//                                        }
//                                    }
//                                    @Override
//                                    public void afterTextChanged(Editable s) {
//
//                                    }
//                                });
//                                btnConfirm.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        if (edtAmount.getText().toString().equals("") || edtAmount.getText().toString().equals("0")){
//                                            edtAmount.setError("Nhập số phần");
//                                            edtAmount.requestFocus();
//                                        }else {
//                                            String id = String.valueOf(orderedList.get(position).orderedId);
//                                            String foodname = orderedList.get(position).getFoodname();
//                                            String newamount = edtAmount.getText().toString();
//                                            String date = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());
//                                            String total = String.valueOf(price*Integer.valueOf(newamount));
////                                            updateOrdered(id,foodname,newamount, date, String.valueOf(price), total, dialog);
//
//                                        }
//                                    }
//                                });
//                                break;
//                            case R.id.popup_delete:
//                                AlertDialog.Builder builder = new AlertDialog.Builder(OrderedActivity.this);
//                                builder.setMessage("Bạn muốn xóa "+orderedList.get(position).getAmount()+" phần "+orderedList.get(position).getFoodname()+" ?");
//                                builder.setCancelable(false);
//                                builder.setPositiveButton("Không", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        dialogInterface.dismiss();
//                                    }
//                                });
//                                builder.setNegativeButton("Xóa", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                                    }
//                                });
//
//                                AlertDialog alertDialog = builder.create();
//                                alertDialog.show();
//
//                                break;
//                        }
//                        return false;
//                    }
//                });
//                popupMenu.show();
//                return false;
//            }
//        });
    }

    //  add Fragment
    public void addControl() {
        Bundle choose = new Bundle();
        choose.putString("idnumber", getIdNumber);
        choose.putString("number", getNumber);
        FragmentChoose fragchoose = new FragmentChoose();
        fragchoose.setArguments(choose);

        Bundle ordered = new Bundle();
        ordered.putString("idmunber", getIdNumber);
        ordered.putString("number", getNumber);
        FragmentOrdered frgordered = new FragmentOrdered();
        frgordered.setArguments(ordered);

        pager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (DachshundTabLayout) findViewById(R.id.tab_layout);

        FragmentManager manager = getSupportFragmentManager();
        PagerOrderAdapter adapter = new PagerOrderAdapter(manager, OrderedActivity.this);
        pager.setAdapter(adapter);

        tabLayout.setupWithViewPager(pager);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(adapter);//deprecated
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager));

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
                startActivity(new Intent(OrderedActivity.this, MainActivity.class));
                finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void loadTotal(){
        final String numberId;
        if (Integer.valueOf(getNumber)<10){
            numberId = "00"+getNumber;
        }else if (Integer.valueOf(getNumber)<100){
            numberId = "0"+getNumber;
        }else {
            numberId = getNumber;
        }
        numberRef.document(numberId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    String total = documentSnapshot.getString("total");
                    if (total.isEmpty()){
                        tvTotal.setText("0");
                    }else {
                        DecimalFormat formatPrice = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                        formatPrice.applyPattern("###,###,###");
                        tvTotal.setText(formatPrice.format(Integer.valueOf(total)));
                    }
                }
            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
//        foodList.clear();
//        orderedList.clear();
//        mFirestore.collection(Config.RESTAURANTS+"/"+getResEmail+"/"+Config.MENU).addSnapshotListener(this, new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//                if (e != null){
//                    Toast.makeText(OrderedActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                }else {
//                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()){
//                        String foodId = doc.getDocument().getId();
//                        switch (doc.getType()){
//                            case ADDED:
//                                Food food = doc.getDocument().toObject(Food.class).withId(foodId);
//                                foodList.add(food);
//                                foodOrderedAdapter.notifyDataSetChanged();
//                                break;
//                            case REMOVED:
//
//                                break;
//                        }
//                    }
//                }
//            }
//        });
//
//        String numberId;
//        if (Integer.valueOf(getNumber)<10){
//            numberId = "00"+getNumber;
//        }else if (Integer.valueOf(getNumber)<100){
//            numberId = "0"+getNumber;
//        }else {
//            numberId = getNumber;
//        }
//        mFirestore.collection(Config.RESTAURANTS+"/"+getResEmail+"/"+Config.NUMBER+"/"+numberId+"/unpaid").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                if (e != null){
//                    Toast.makeText(OrderedActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                }else {
//                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
//                        String orderId = doc.getDocument().getId();
//                        switch (doc.getType()){
//                            case ADDED:
//                                Ordered ordered = doc.getDocument().toObject(Ordered.class).withId(orderId);
//                                orderedList.add(ordered);
//                                oredredAdapter.notifyDataSetChanged();
//
//                                break;
//                            case REMOVED:
////                                loadTotal();
//                                break;
//                            case MODIFIED:
////                                loadTotal();
//                                break;
//                        }
//                    }
//                }
//            }
//        });
        numberRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null){
                    Toast.makeText(OrderedActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                for (DocumentChange doc : documentSnapshots.getDocumentChanges()){
                    switch (doc.getType()){
                        case MODIFIED:
                            String total = doc.getDocument().getString("total");
                            if (total.isEmpty()){
                                tvTotal.setText("0");
                            }else {
                                DecimalFormat formatPrice = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                                formatPrice.applyPattern("###,###,###");
                                tvTotal.setText(formatPrice.format(Integer.valueOf(total)));
                            }
                            break;
                    }
                }
            }
        });

    }

}
