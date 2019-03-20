package org.irestaurant.irm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.button.MaterialButton;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.irestaurant.irm.Database.DatabaseTable;
import org.irestaurant.irm.Database.Number;
import org.irestaurant.irm.Database.NumberAdapter;
import org.irestaurant.irm.Database.SessionManager;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SessionManager sessionManager;
    String getName, getResName;
    TextView tvResName, tvName;
    GridView gvNumber;
    Button btnAddTable, btnRemoveTable;
    private List<Number> numberList;
    private DatabaseTable databaseTable;
    private NumberAdapter numberAdapter;

    private void AnhXa(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);
        tvName  = hView.findViewById(R.id.tv_name);
        tvResName = hView.findViewById(R.id.tv_resname);
        gvNumber    = findViewById(R.id.gv_number);
        btnAddTable      = findViewById(R.id.btn_addtable);
        btnRemoveTable  = findViewById(R.id.btn_removetable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseTable = new DatabaseTable(this);
        numberList = databaseTable.getallTable();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AnhXa();
        setGvNumber();


        sessionManager = new SessionManager(this);
        sessionManager.checkLoggin();
        HashMap<String, String> user = sessionManager.getUserDetail();
        getName = user.get(sessionManager.NAME);
        getResName = user.get(sessionManager.RESNAME);
        tvName.setText(getName);
        tvResName.setText(getResName);
        setTitle(getResName);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
                    String idnumber = String.valueOf(numberList.get(position).getId());
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
                            String idnumber = String.valueOf(numberList.get(position).getId());
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
                            String idnumber = String.valueOf(numberList.get(position).getId());
                            String number = numberList.get(position).getNumber();
                            Intent i = new Intent(MainActivity.this, PayActivity.class);
                            i.putExtra("idnumber", idnumber);
                            i.putExtra("number", number);
                            startActivity(i);
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

    }

    private void setGvNumber() {
        if (numberAdapter == null) {
            numberAdapter = new NumberAdapter(MainActivity.this, R.layout.item_table, numberList);
            gvNumber.setAdapter(numberAdapter);
        } else {
            numberList.clear();
            numberList.addAll(databaseTable.getallTable());
            numberAdapter.notifyDataSetChanged();
            gvNumber.setSelection(numberAdapter.getCount() - 1);
        }
        if (numberList.size()<1){
            btnRemoveTable.setVisibility(View.INVISIBLE);
        }else {
            btnRemoveTable.setVisibility(View.VISIBLE);
        }
    }



    private void RegistNumber (long s){
        DatabaseTable db = new DatabaseTable(getApplicationContext());
        Number number = new Number();
        number.setNumber(String.valueOf(s));
        number.setStatus("free");
        if (db.creat(number)){
            numberList.clear();
            numberList.addAll(databaseTable.getallTable());
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
                            progressDialog.setMessage("Đang thêm bàn");
                            progressDialog.show();
                            int nbsize = numberList.size();
                            int amount = Integer.valueOf(edtAmount.getText().toString());
                            int a;
                            for (a = 0; a < amount; a++) {
                                databaseTable.deleteTable(nbsize);
                                nbsize--;
                            }
                            setGvNumber();
                            if (amount==nbsize){
                                btnRemoveTable.setVisibility(View.INVISIBLE);
                            }
                            Toast.makeText(MainActivity.this, "Đã xóa " + String.valueOf(Integer.valueOf(edtAmount.getText().toString())) + " bàn", Toast.LENGTH_LONG).show();
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
                    setGvNumber();
                    Toast.makeText(MainActivity.this, "Đã thêm " + String.valueOf(Integer.valueOf(edtAmount.getText().toString())) + " bàn", Toast.LENGTH_LONG).show();
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
            super.onBackPressed();
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
            startActivity(new Intent(this,AccountActivity.class));
        } else if (id == R.id.nav_revenue) {
            startActivity(new Intent(this,RevenueActivity.class));
        } else if (id == R.id.nav_menu) {
            startActivity(new Intent(this,MenuActivity.class));
        } else if (id == R.id.nav_recent) {

        } else if (id == R.id.nav_settings) {
//            startActivity(new Intent(this,SettingsActivity.class));
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
}
