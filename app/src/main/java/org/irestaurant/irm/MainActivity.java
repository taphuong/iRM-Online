package org.irestaurant.irm;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.util.Patterns;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.snapshot.StringNode;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;

import org.irestaurant.irm.Database.Config;
import org.irestaurant.irm.Database.Food;
import org.irestaurant.irm.Database.Invite;
import org.irestaurant.irm.Database.Number;
import org.irestaurant.irm.Database.NumberAdapter;
import org.irestaurant.irm.Database.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SessionManager sessionManager;
    String getName, getResName, getEmail, getImage, getPosition, getResEmail, getToken, getId, getPassword, getResPhone, getResAddress;
    TextView tvResName, tvName, btvNotifi, tvInvite;
    GridView gvNumber;
    Button btnAddTable, btnRemoveTable, btnNewRes, btnJoinRes;
    RelativeLayout layoutNores;
    FloatingActionButton btnNoti, fab1, fab2, fab3;
    int fab = 0;

//    Firebase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    CircleImageView imgprofile;
    private List<Number> numberList;
    private NumberAdapter numberAdapter;
    private CollectionReference numberRef;
    boolean doubleBackToExitPressedOnce = false;



    private void AnhXa(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);
        tvName  = hView.findViewById(R.id.tv_name);
        tvResName = hView.findViewById(R.id.tv_resname);
        gvNumber    = findViewById(R.id.gv_number);
        btnAddTable      = findViewById(R.id.btn_addtable);
        btnRemoveTable  = findViewById(R.id.btn_removetable);
        btnNewRes   = findViewById(R.id.btn_newres);
        btnJoinRes  = findViewById(R.id.btn_joinres);
        imgprofile  = hView.findViewById(R.id.im_profile);
        layoutNores = findViewById(R.id.layout_nores);
        btvNotifi   = findViewById(R.id.tv_noti);
        tvInvite    = findViewById(R.id.tv_invite);
        btnNoti     = findViewById(R.id.btn_noti);
        fab1        = findViewById(R.id.fab1);
        fab2        = findViewById(R.id.fab2);
        fab3        = findViewById(R.id.fab3);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        AnhXa();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menuNav = navigationView.getMenu();
        MenuItem navNewRes = menuNav.findItem(R.id.nav_addres);
        MenuItem navJoinRes = menuNav.findItem(R.id.nav_joinress);
        MenuItem navMenu = menuNav.findItem(R.id.nav_menu);
        MenuItem navPeople = menuNav.findItem(R.id.nav_people);
        MenuItem navRevenue = menuNav.findItem(R.id.nav_revenue);
        MenuItem navRecent = menuNav.findItem(R.id.nav_recent);

        sessionManager = new SessionManager(this);
        sessionManager.checkLoggin();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser==null || !sessionManager.isLoggin()){
            sessionManager.logout();
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            finish();
        }else {
            HashMap<String, String> user = sessionManager.getUserDetail();
            getName = user.get(sessionManager.NAME);
            getResName = user.get(sessionManager.RESNAME);
            getEmail = user.get(sessionManager.EMAIL);
            getImage = user.get(sessionManager.IMAGE);
            getPosition = user.get(sessionManager.POSITION);
            getResEmail = user.get(sessionManager.RESEMAIL);
            getToken = FirebaseInstanceId.getInstance().getToken();
            getId = mAuth.getCurrentUser().getUid();
            getPassword = user.get(sessionManager.PASSWORD);
            getResAddress = user.get(sessionManager.RESADDRESS);
            getResPhone = user.get(sessionManager.RESPHONE);

            tvName.setText(getName);
            if (getPosition.equals("none")){
                btnRemoveTable.setVisibility(View.GONE);
                btnAddTable.setVisibility(View.GONE);
                gvNumber.setVisibility(View.GONE);
                layoutNores.setVisibility(View.VISIBLE);
                tvResName.setText("Chưa có cửa hàng");
                setTitle("Chưa có cửa hàng");
                navMenu.setVisible(false);
                navRevenue.setVisible(false);
                navPeople.setVisible(false);
                navRecent.setVisible(false);
            } else if (getPosition.equals("admin")){
                layoutNores.setVisibility(View.GONE);
                tvResName.setText(getResName);
                setTitle(getResName);
                navJoinRes.setVisible(false);
                navNewRes.setVisible(false);
            }else {
                btnAddTable.setVisibility(View.GONE);
                btnRemoveTable.setVisibility(View.GONE);
                layoutNores.setVisibility(View.GONE);
                tvResName.setText(getResName);
                setTitle(getResName);
                navJoinRes.setVisible(false);
                navMenu.setVisible(false);
                navNewRes.setVisible(false);
                navRevenue.setVisible(false);
            }

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.profile);
            Glide.with(getApplicationContext()).setDefaultRequestOptions(requestOptions).load(getImage).into(imgprofile);
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();



        FirebaseUser fUser = mAuth.getCurrentUser();
        if (fUser==null || getName == null || getName.isEmpty()){
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            finish();
            sessionManager.logout();
        }else {


//            Toast.makeText(this, getPosition, Toast.LENGTH_SHORT).show();

        }


        numberList = new ArrayList<>();
        numberAdapter = new NumberAdapter(this, R.layout.item_table, numberList);
        gvNumber.setAdapter(numberAdapter);

        numberRef = mFirestore.collection(Config.RESTAURANTS+"/"+getResEmail+"/"+Config.NUMBER);

        btnRemoveTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeTable();
            }
        });

        btnAddTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTable();
            }
        });


        gvNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                String status = numberList.get(position).getStatus();
                if (status.equals("free")){
                    String idnumber = String.valueOf(numberList.get(position).numberId);
                    String number = numberList.get(position).getNumber();
                    Intent i = new Intent(MainActivity.this, OrderedActivity.class);
                    i.putExtra("idnumber", idnumber);
                    i.putExtra("number", number);
                    startActivity(i);
                }else if (status.equals("busy")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Bàn số "+numberList.get(position).getNumber()+" đang có khách.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setNegativeButton("Thêm món", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String idnumber = String.valueOf(numberList.get(position).numberId);
                            String number = numberList.get(position).getNumber();
                            Intent e = new Intent(MainActivity.this, OrderedActivity.class);
                            e.putExtra("idnumber", idnumber);
                            e.putExtra("number", number);
                            startActivity(e);
                        }
                    });
                    builder.setNeutralButton("Tính tiền", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            String idnumber = String.valueOf(numberList.get(position).getId());
                            String number = numberList.get(position).getNumber();
                            Intent i = new Intent(MainActivity.this, PayActivity.class);
//                            i.putExtra("idnumber", idnumber);
                            i.putExtra("number", number);
                            startActivity(i);
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });
        
        btnJoinRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinRes();
            }
        });
        btnNewRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newRes();
            }
        });
        btnNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fab == 0) {
                    fab =1;
                    showFab();
                }else {
                    fab = 0;
                    hideFab();
                }
            }
        });
    }

    private void showFab (){
        Animation show_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_show);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        layoutParams.rightMargin += (int) (fab1.getWidth() * 1.7);
        layoutParams.bottomMargin += (int) (fab1.getHeight() * 0.25);
        fab1.setLayoutParams(layoutParams);
        fab1.startAnimation(show_fab_1);
        fab1.setClickable(true);
    }
    private void hideFab (){
        Animation hide_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_hide);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        layoutParams.rightMargin -= (int) (fab1.getWidth() * 1.7);
        layoutParams.bottomMargin -= (int) (fab1.getHeight() * 0.25);
        fab1.setLayoutParams(layoutParams);
        fab1.startAnimation(hide_fab_1);
        fab1.setClickable(false);
    }

    private void newRes() {
        startActivity(new Intent(this, NewresActivity.class));
        finish();
    }

    private void joinRes() {
        startActivity(new Intent(this, JoinActivity.class));
//        final Dialog dialog = new Dialog(MainActivity.this);
//        dialog.setContentView(R.layout.dialog_joinres);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.setCanceledOnTouchOutside(false);
//        final EditText edtResEmail    = dialog.findViewById(R.id.edt_resemail);
//        Button btnClose         = dialog.findViewById(R.id.btn_close);
//        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);
//        btnClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        btnConfirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final String resEmail = edtResEmail.getText().toString().trim();
//                if (resEmail.isEmpty()){
//                    edtResEmail.requestFocus();
//                    edtResEmail.setError(String.valueOf(R.string.isempty));
//                }else if (!Patterns.EMAIL_ADDRESS.matcher(resEmail).matches()) {
//                    edtResEmail.requestFocus();
//                    edtResEmail.setError("Email sai định dạng");
//                }else {
//                    mFirestore.collection(Config.RESTAURANTS).document(resEmail).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                        @Override
//                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//                            if (documentSnapshot.exists()){
//                                Map<String ,Object> joinMap = new HashMap<>();
//                                joinMap.put(Config.NAME, getName);
//                                joinMap.put(Config.EMAIL, getEmail);
//                                joinMap.put(Config.IMAGE, getImage);
//                                joinMap.put(Config.STATUS, "join");
//                                joinMap.put(Config.TOKENID, getToken);
//                                mFirestore.collection(Config.RESTAURANTS).document(resEmail).collection(Config.PEOPLE).document(getEmail).set(joinMap).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//                                        Toast.makeText(MainActivity.this, "Đã gửi yêu cầu tham gia", Toast.LENGTH_SHORT).show();
//                                        dialog.dismiss();
//                                    }
//                                });
//
//                            }else {
//                                edtResEmail.setError("Sai địa chỉ Email");
//                                edtResEmail.requestFocus();
//                            }
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            }
//        });
//
//        dialog.show();
    }

    private void deleteNumber(int s) {
        String id = null;
        if (s<10){
            id = "00"+s;
        }else if (s<100){
            id = "0"+s;
        }else {
            id = String.valueOf(s);
        }
        numberRef.document(id).delete();
    }
    private void RegistNumber (long s){
        String id = null;
        String number = String.valueOf(s);
        if (s<10){
            id = "00"+s;
        }else if (s<100){
            id = "0"+s;
        }else {
            id = String.valueOf(s);
        }
        Map<String, Object> nameMap = new HashMap<>();
        nameMap.put("number", number);
        nameMap.put("status", "free");
        nameMap.put("total", "");
        numberRef.document(id).set(nameMap).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, R.string.dacoloi, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void removeTable(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_addnumber);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        final Button btnMinus = (Button) dialog.findViewById(R.id.btn_minus);
        final Button btnAdd = (Button) dialog.findViewById(R.id.btn_add);
        TextView tvXoa = dialog.findViewById(R.id.themban);
        tvXoa.setText("Xóa bàn");
        Button btnClose     = (Button) dialog.findViewById(R.id.btn_close);
        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);
        btnConfirm.setText("Xóa");
        final EditText edtAmount = (EditText) dialog.findViewById(R.id.edt_amount);
        dialog.show();
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = numberList.size();
                btnMinus.setVisibility(View.VISIBLE);
                if (edtAmount.getText().toString().equals("")){
                    edtAmount.setText("1");
                } else if (Integer.valueOf(edtAmount.getText().toString())>size){
                    edtAmount.setText(String.valueOf(size));
                    btnAdd.setVisibility(View.INVISIBLE);
                } else {
                    edtAmount.setText(String.valueOf(Integer.valueOf(edtAmount.getText().toString())+1));
                }
            }
        });
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAdd.setVisibility(View.VISIBLE);
                String stramount = edtAmount.getText().toString();
                if (stramount.isEmpty() || stramount.equals("2")){
                    edtAmount.setText("1");
                    btnMinus.setVisibility(View.INVISIBLE);
                }  else {
                    long amount = Integer.valueOf(edtAmount.getText().toString());
                    edtAmount.setText(String.valueOf(amount-1));
                }

            }
        });
        edtAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String amout = edtAmount.getText().toString();
                int size = numberList.size();
                if (amout.isEmpty() || amout.equals("1") || amout.equals("0")) {
                    btnMinus.setVisibility(View.INVISIBLE);
                    btnAdd.setVisibility(View.VISIBLE);
                } else if (Integer.valueOf(amout) > size) {
                    edtAmount.setText(String.valueOf(size));
                    btnAdd.setVisibility(View.INVISIBLE);
                    btnMinus.setVisibility(View.VISIBLE);
                } else {
                    btnMinus.setVisibility(View.VISIBLE);
                    btnAdd.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtAmount.getText().toString().equals("") || edtAmount.getText().toString().equals("0")){
                    edtAmount.setError("Nhập số lượng bàn cần xóa");
                    edtAmount.requestFocus();
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Bạn có muốn xóa "+ edtAmount.getText().toString()+" bàn không?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("Xóa", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                            progressDialog.setMessage("Đang xóa bàn");
                            progressDialog.show();
                            int nbsize = numberList.size();
                            int amount = Integer.valueOf(edtAmount.getText().toString());
                            int a;
                            for (a = 0; a < amount; a++) {
                                deleteNumber(nbsize);
                                nbsize--;
                            }
                            if (amount==nbsize){
                                btnRemoveTable.setVisibility(View.INVISIBLE);
                            }
                            Toast.makeText(MainActivity.this, "Đã xóa " + Integer.valueOf(edtAmount.getText().toString()) + " bàn", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            progressDialog.dismiss();
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });
    }
    private void addTable(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_addnumber);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        final Button btnMinus = (Button) dialog.findViewById(R.id.btn_minus);
        final Button btnAdd = (Button) dialog.findViewById(R.id.btn_add);
        Button btnClose     = (Button) dialog.findViewById(R.id.btn_close);
        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);
        final EditText edtAmount = (EditText) dialog.findViewById(R.id.edt_amount);
        dialog.show();
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMinus.setVisibility(View.VISIBLE);
                if (edtAmount.getText().toString().equals("")){
                    edtAmount.setText("1");
                } else if (Integer.valueOf(edtAmount.getText().toString())==49){
                    edtAmount.setText("50");
                    btnAdd.setVisibility(View.INVISIBLE);
                } else {
                    edtAmount.setText(String.valueOf(Integer.valueOf(edtAmount.getText().toString())+1));
                }
            }
        });
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAdd.setVisibility(View.VISIBLE);
                String stramount = edtAmount.getText().toString();
                if (stramount.isEmpty() || stramount.equals("2")){
                    edtAmount.setText("1");
                    btnMinus.setVisibility(View.INVISIBLE);
                }  else {
                    long amount = Integer.valueOf(edtAmount.getText().toString());
                    edtAmount.setText(String.valueOf(amount-1));
                }

            }
        });
        edtAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String amout = edtAmount.getText().toString();
                if (amout.isEmpty() || amout.equals("1") || amout.equals("0")) {
                    btnMinus.setVisibility(View.INVISIBLE);
                    btnAdd.setVisibility(View.VISIBLE);
                } else if (Integer.valueOf(amout) > 50) {
                    edtAmount.setText("50");
                    edtAmount.setSelection(edtAmount.getText().length());
                    btnAdd.setVisibility(View.INVISIBLE);
                    btnMinus.setVisibility(View.VISIBLE);
                } else {
                    btnMinus.setVisibility(View.VISIBLE);
                    btnAdd.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int size = numberList.size();
                if (edtAmount.getText().toString().equals("") || edtAmount.getText().toString().equals("0")){
                    edtAmount.setError("Nhập số lượng bàn cần thêm");
                    edtAmount.requestFocus();
                }else {
                    final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Đang thêm bàn");
                    progressDialog.show();
                    int amount = Integer.valueOf(edtAmount.getText().toString());
                    int i;
                    for (i = 0; i < amount; i++) {
                        RegistNumber(size + 1);
                        size++;
                    }
                    btnRemoveTable.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "Đã thêm " + Integer.valueOf(edtAmount.getText().toString()) + " bàn", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Ấn 1 lần nữa để thoát ứng dụng", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
//            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_account) {
            Intent intent = new Intent(MainActivity.this, AccountActivity.class);
//            Pair[] pairs = new Pair[1];
//            pairs[0] = new Pair<View, String>(imgprofile, "imageTransition");
//            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, pairs);
//            startActivity(intent, options.toBundle());
            startActivity(intent);
        }
        else if (id == R.id.nav_addres) {
            newRes();
//            startActivity(new Intent(this,RevenueActivity.class));
        } else if (id == R.id.nav_joinress) {
            joinRes();
        }else if (id == R.id.nav_revenue) {
            startActivity(new Intent(this,RevenueActivity.class));
        } else if (id == R.id.nav_menu) {
            startActivity(new Intent(this,MenuActivity.class));
        } else if (id == R.id.nav_people) {
            if (getPosition.equals("admin")) {
                startActivity(new Intent(this, PeopleActivity.class));
            }else {
                startActivity(new Intent(this, PeopleSingleActivity.class));
            }
        } else if (id == R.id.nav_recent) {
            startActivity(new Intent(this,HistoryActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this,SettingActivity.class));
        } else if (id == R.id.nav_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Bạn có muốn đăng xuất không?");
            builder.setCancelable(false);
            builder.setPositiveButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("Đăng xuất", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    sessionManager.logout();
                    dialogInterface.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void pushNoti(final String number){
        mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.NUMBER).document(number).collection("unpaid").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e!=null){
                    return;
                }else {
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                        String foodname = doc.getDocument().getString("foodname");
                        String amount = doc.getDocument().getString("amount");
                        switch (doc.getType()){
                            case ADDED:
                                btvNotifi.setVisibility(View.VISIBLE);
                                btvNotifi.setText("Bàn số "+number+" thêm "+amount+" phần\n"+foodname);
                                new CountDownTimer(3000, 3000) {

                                    public void onTick(long millisUntilFinished) {
                                    }

                                    public void onFinish() {
                                        btvNotifi.setVisibility(View.GONE);
                                    }
                                }.start();
                                break;
                            case MODIFIED:
                                btvNotifi.setVisibility(View.VISIBLE);
                                btvNotifi.setText("Bàn số "+number+" thay đổi "+amount+" phần\n"+foodname);
                                new CountDownTimer(3000, 3000) {

                                    public void onTick(long millisUntilFinished) {
                                    }

                                    public void onFinish() {
                                        btvNotifi.setVisibility(View.GONE);
                                    }
                                }.start();
                                break;
                            case REMOVED:
                                btvNotifi.setVisibility(View.VISIBLE);
                                btvNotifi.setText("Bàn số "+number+" bỏ "+amount+" phần\n"+foodname);
                                new CountDownTimer(3000, 3000) {

                                    public void onTick(long millisUntilFinished) {
                                    }

                                    public void onFinish() {
                                        btvNotifi.setVisibility(View.GONE);
                                    }
                                }.start();
                                break;
                        }
                    }
                }
            }
        });
    }

    public void tv_invite_click (View v){
        startActivity(new Intent(MainActivity.this, InviteListActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Config.TABLEID = "";
        Config.TABLE = "";
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser==null || !sessionManager.isLoggin()){
            sessionManager.logout();
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            finish();
        }

//      list number
        numberList.clear();
        numberRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null){
                    return;
                }
//                else {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()){
                        String numberId = doc.getDocument().getId();
                        String table = doc.getDocument().getString("number");
                        switch (doc.getType()){
                            case ADDED:
                                Number number = doc.getDocument().toObject(Number.class).withId(numberId);
                                numberList.add(number);
                                numberAdapter.notifyDataSetChanged();
//                                pushNoti(table);
                                break;
                            case REMOVED:
//                                pushNoti(table);
                                numberList.remove(Integer.valueOf(numberId)-1);
                                numberAdapter.notifyDataSetChanged();
                                break;
                            case MODIFIED:
//                                String status = doc.getDocument().getString("status");
//                                String nb = doc.getDocument().getString("number");
//                                pushNoti(table);
                                Number number1 = doc.getDocument().toObject(Number.class).withId(numberId);
                                numberList.set(Integer.valueOf(numberId)-1, number1);
                                numberAdapter.notifyDataSetChanged();
                                break;
                        }


                        }
                    }

//                }

            });

//        check position
        mFirestore.collection(Config.USERS).document(getEmail).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (e!=null){
                    return;
                }
                String position = documentSnapshot.getString(Config.POSITION);
                if (documentSnapshot!=null && documentSnapshot.exists() && !position.equals(getPosition)){

                    sessionManager.createSession(getId, getName, getEmail, getResEmail, getPassword, getResName, getResPhone, getResAddress, position, getImage);
                    finish();
                    startActivity(getIntent());
                }
            }
        });

//        check res
        mFirestore.collection(Config.RESTAURANTS).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    return;
                }
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                    String resEmail = doc.getDocument().getId();
                    final String resname = doc.getDocument().getString(Config.RESNAME);
                    final String resphone = doc.getDocument().getString(Config.RESPHONE);
                    final String resaddress = doc.getDocument().getString(Config.RESADDRESS);
                    if (resEmail.equals(getResEmail) && doc.getType().equals(DocumentChange.Type.MODIFIED)){
                        Map<String, Object> updateMap = new HashMap<>();
                        updateMap.put(Config.RESNAME, resname);
                        updateMap.put(Config.RESPHONE, resphone);
                        updateMap.put(Config.RESADDRESS, resaddress);
                        mFirestore.collection(Config.USERS).document(getEmail).update(updateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                sessionManager.createSession(getId, getName, getEmail, getResEmail, getPassword, resname, resphone, resaddress, getPosition, getImage);
                                finish();
                                startActivity(getIntent());
                            }

                        });
                    }
                    return;
                }
            }
        });

//        inviteList
        mFirestore.collection(Config.USERS).document(getEmail).collection(Config.INVITE).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    return;
                }
                List inviteList = new ArrayList();
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                    String inviteId = doc.getDocument().getId();
                    switch (doc.getType()){
                        case ADDED:
                            Invite invite = doc.getDocument().toObject(Invite.class).withId(inviteId);
                            inviteList.add(invite);
                            break;
                    }
                }
                if (inviteList.size()>0){
                    tvInvite.setText("Bạn có "+inviteList.size()+" lời mời làm việc tại cửa hàng");
                    tvInvite.setVisibility(View.VISIBLE);
                }else {
                    tvInvite.setText("");
                    tvInvite.setVisibility(View.GONE);
                }

            }
        });
    }

}
