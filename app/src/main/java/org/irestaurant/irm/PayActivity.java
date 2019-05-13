package org.irestaurant.irm;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.irestaurant.irm.Database.BluetoothHandler;
import org.irestaurant.irm.Database.BluetoothService;
import org.irestaurant.irm.Database.Config;
import org.irestaurant.irm.Database.Number;
import org.irestaurant.irm.Database.Ordered;
import org.irestaurant.irm.Database.OredredAdapter;
import org.irestaurant.irm.Database.PrinterCommands;
import org.irestaurant.irm.Database.SessionManager;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class PayActivity extends Activity implements EasyPermissions.PermissionCallbacks, BluetoothHandler.HandlerInterface {
    SessionManager sessionManager;
    TextView tvTotal, tvTotalAll, tvNumber;
    EditText edtDiscount;
    RecyclerView lvOrdered;
    Button btnPay, btnCancel, btnPrinter;
    String getIdNumber, getNumber, total, totalall, discount, getResName, getResPhone, getResAddress, getResEmail;
    public String name = "Chưa kết nối", address = "Null";
    public Switch swPrint;
    long tongtien, after;

    List<Ordered> orderedList;
    OredredAdapter oredredAdapter;
    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    public static BluetoothService mService = null;
    private boolean isPrinterReady = false;

    /******************************************************************************************************/

    public static final int RC_BLUETOOTH = 0;
    public static final int RC_CONNECT_DEVICE = 1;
    public static final int RC_ENABLE_BLUETOOTH = 2;

    private void Anhxa (){
        tvTotal     = findViewById(R.id.tv_tong);
        tvTotalAll  = findViewById(R.id.tv_totalall);
        tvNumber    = findViewById(R.id.tv_table);
        edtDiscount = findViewById(R.id.edt_discount);
        lvOrdered   = findViewById(R.id.lv_ordered);
        btnCancel   = findViewById(R.id.btn_cancel);
        btnPay      = findViewById(R.id.btn_pay);
        btnPrinter  = findViewById(R.id.btn_printer);
        swPrint     = findViewById(R.id.sw_print);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        sessionManager = new SessionManager(this);
        sessionManager.checkLoggin();
        HashMap<String, String> user = sessionManager.getUserDetail();
        getResAddress = user.get(sessionManager.RESADDRESS);
        getResName = user.get(sessionManager.RESNAME);
        getResPhone = user.get(sessionManager.RESPHONE);
        getResEmail = user.get(sessionManager.RESEMAIL);

        Anhxa();
        Intent intent = getIntent();
        getIdNumber = intent.getExtras().getString("idnumber");
        getNumber = intent.getExtras().getString("number");
        tvNumber.setText("Bàn số: "+getNumber);
        swPrint.setText("In hóa đơn ("+name+")");
        Config.TABLE = getNumber;
        Config.TABLEID = getIdNumber;

        orderedList = new ArrayList<>();
        oredredAdapter = new OredredAdapter(this, orderedList);
        lvOrdered.setHasFixedSize(true);
        lvOrdered.setLayoutManager(new LinearLayoutManager(this));
        lvOrdered.setAdapter(oredredAdapter);


//        setLvPay();
        setupBluetooth();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        edtDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String Sdiscount = edtDiscount.getText().toString().trim();

                if (!Sdiscount.equals("")){
                    long discount =Integer.valueOf(Sdiscount);
                    if (discount>100){
                        edtDiscount.setText("100");
                        tvTotalAll.setText("0");
                        after = 0;
                    }else {
                        after = tongtien-(tongtien*discount/100);
                        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                        formatter.applyPattern("#,###,###,###");
                        tvTotalAll.setText(formatter.format(after));
                    }
                } else {
                    tvTotalAll.setText(tvTotal.getText());
                    after = tongtien;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String Sdiscount = edtDiscount.getText().toString().trim();
                if (Sdiscount.isEmpty()){
                    tvTotalAll.setText(tvTotal.getText());
                    after = tongtien;
                }

            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swPrint.isChecked() && name.equals("Chưa kết nối") ){
                    connectPrinter();
                } else {
                    if (swPrint.isChecked()){
                        printText();
                    }else {
                        addPay();
                    }
                }


            }
        });

        swPrint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    btnPrinter.setEnabled(true);
                    swPrint.setText("In hóa đơn (Chưa kết nối)");
                    connectPrinter();
                }else {
                    swPrint.setText("Không in hóa đơn");
                    btnPrinter.setEnabled(false);
                }
            }
        });
        btnPrinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectPrinter();
            }
        });
    }
    private void checkConnection(){
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size()>0){
            if (mService!=null) {
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                this.registerReceiver(mReceiver, filter);

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(PayActivity.this);
                builder.setMessage("Kết nối với máy in lỗi!\nBạn có muốn kết nối lại không?");
                builder.setCancelable(false);
                builder.setPositiveButton("Không in", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        swPrint.setChecked(false);
                        dialogInterface.dismiss();
                        btnPrinter.setEnabled(false);
                    }
                });
                builder.setNegativeButton("Kết nối", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        connectPrinter();
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

        }
        else {
            connectPrinter();
        }
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                swPrint.setText("Đã kết nối "+device.getName());
                Print();
                //Device is now connected
            }
        }
    };

    private void addPay() {
        String date = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());
        String rdate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        String time = new SimpleDateFormat("kk:mm", Locale.getDefault()).format(new Date());
        total = tvTotal.getText().toString().replaceAll(",","");
        totalall = tvTotalAll.getText().toString().replaceAll(",","");
        if (edtDiscount.getText().toString().isEmpty()){
            discount = "0";
        }else {discount = edtDiscount.getText().toString();}

        Paid();

        if (swPrint.isChecked() && name.equals("Chưa kết nối") ){
            connectPrinter();
        }
    }

    private void Paid (){
        final String date = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());
        String time = new SimpleDateFormat("kk:mm", Locale.getDefault()).format(new Date());
        final String dt = new SimpleDateFormat("yy/MM/dd-kk:mm", Locale.getDefault()).format(new Date());
        mFirestore.collection(Config.RESTAURANTS).document(getResAddress).collection(Config.NUMBER).document(getIdNumber).collection("unpaid").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    final String id = documentSnapshot.getId();
                    String total = documentSnapshot.getString(Config.TOTAL);
                    String foodname = documentSnapshot.getString(Config.FOODNAME);
                    String foodprice = documentSnapshot.getString("price");
                    String amount = documentSnapshot.getString(Config.AMOUNT);
                    final Map<String, Object> paidMap = new HashMap<>();
                    paidMap.put(Config.TOTAL, total);
                    paidMap.put(Config.FOODNAME, foodname);
                    paidMap.put(Config.FOODPRICE, foodprice);
                    paidMap.put(Config.AMOUNT, amount);

                    Map<String, Object> dateMap = new HashMap<>();
                    dateMap.put("date", date);
                    mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.PAID).document(dt).set(dateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            String tongtien = tvTotalAll.getText().toString().replaceAll(",","");
                            Map<String, Object> tongtienMap = new HashMap<>();
                            tongtienMap.put(Config.TOTAL, tongtien);
                            mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.HISTORY).document(dt).collection(Config.PAID).document(getIdNumber).set(tongtienMap);
                            mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.HISTORY).document(dt).collection(Config.PAID).document(getIdNumber).collection(Config.PAID).document(id).set(paidMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Map<String, Object>tableMap = new HashMap<>();
                                    tableMap.put(Config.TOTAL, "");
                                    tableMap.put(Config.STATUS, "free");
                                    mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.NUMBER).document(getIdNumber).update(tableMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(PayActivity.this, "Đã thanh toán bàn số "+ getNumber, Toast.LENGTH_LONG).show();
                                            finish();
                                        }
                                    });
                                }
                            });
                        }
                    });

                }
                mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.NUMBER).document(getIdNumber).update(Config.TOTAL, String.valueOf(tongtien));
            }
        });
    }

    private void Print (){

    }

    public void setLvPay() {
//        if (payAdapter == null) {
//            tongtien=0;
//            payAdapter = new PayAdapter(PayActivity.this, R.layout.item_pay, payList);
//            lvOrdered.setAdapter(payAdapter);
//            for (int a =0; a<payList.size();a++){
//                tongtien += Integer.valueOf(payList.get(a).getTotal());
//            }
//            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
//            formatter.applyPattern("#,###,###,###");
//            tvTotal.setText(formatter.format(tongtien));
//            tvTotalAll.setText(formatter.format(tongtien));
//        } else {
//            tongtien=0;
//            payList.clear();
////            payList.addAll(databaseOrdered.getallOrdered(getNumber));
//            payAdapter.notifyDataSetChanged();
//            lvOrdered.setSelection(payAdapter.getCount() - 1);
//            for (int a =0; a<payList.size();a++){
//                tongtien += Integer.valueOf(payList.get(a).getTotal());
//            }
//            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
//            formatter.applyPattern("#,###,###,###");
//            tvTotal.setText(formatter.format(tongtien));
//            tvTotalAll.setText(formatter.format(tongtien));
//        }
    }

    private void updateTable (String tb){
//        databaseTable = new DatabaseTable(this);
        Number number = new Number();
        number.setStatus("free");
//        databaseTable.updateTable(number, tb);
        startActivity(new Intent(PayActivity.this, MainActivity.class));
        finish();
    }

    private void updateOrdered (){
    }



    //    Print
    public void connectPrinter(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(PayActivity.this, "Chưa kết nối máy in", Toast.LENGTH_SHORT).show();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent,
                        REQUEST_ENABLE_BT);
            } else {
                ListPairedDevices();
                Intent connectIntent = new Intent(PayActivity.this,
                        DeviceListActivity.class);
                startActivityForResult(connectIntent,
                        REQUEST_CONNECT_DEVICE);
            }
        }
    }

    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

//        switch (mRequestCode) {
//            case REQUEST_CONNECT_DEVICE:
//                if (mResultCode == Activity.RESULT_OK) {
//                    Bundle mExtra = mDataIntent.getExtras();
//                    String mDeviceAddress = mExtra.getString("DeviceAddress");
//                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);
//                    mBluetoothDevice = mBluetoothAdapter
//                            .getRemoteDevice(mDeviceAddress);
////                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
////                            "Đang kết nối...", mBluetoothDevice.getName() + "\n"
////                                    + mBluetoothDevice.getAddress(), true, false);
//                    Thread mBlutoothConnectThread = new Thread();
//                    mBlutoothConnectThread.start();
//                    // pairToDevice(mBluetoothDevice); This method is replaced by
//                    // progress dialog with thread
//                }
//                break;
//
//            case REQUEST_ENABLE_BT:
//                if (mResultCode == Activity.RESULT_OK) {
//                    ListPairedDevices();
//                    Intent connectIntent = new Intent(PayActivity.this,
//                            DeviceListActivity.class);
//                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
//                } else {
//                    Toast.makeText(PayActivity.this, "Message", Toast.LENGTH_SHORT).show();
//                }
//                break;
//        }
        switch (mRequestCode) {
            case RC_ENABLE_BLUETOOTH:
                if (mResultCode == RESULT_OK) {
                    Log.i(TAG, "onActivityResult: bluetooth aktif");
                } else
                    Log.i(TAG, "onActivityResult: bluetooth harus aktif untuk menggunakan fitur ini");
                break;
            case RC_CONNECT_DEVICE:
                if (mResultCode == RESULT_OK) {
                    address = mDataIntent.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    name = mDataIntent.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_NAME);
                    BluetoothDevice mDevice = mService.getDevByMac(address);
                    mService.connect(mDevice);

//                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
//                            "Đang kết nối...", name + "\n"
//                                    + address, true, false);
                }
                break;
        }
    }
    public void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v(TAG, "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
            }
        }
    }
    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice
                    .createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandler.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
            return;
        }
    }

    public void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(PayActivity.this, "DeviceConnected", Toast.LENGTH_SHORT).show();
        }
    };

    @AfterPermissionGranted(RC_BLUETOOTH)
    private void setupBluetooth() {
        String[] params = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN};
        if (!EasyPermissions.hasPermissions(this, params)) {
            EasyPermissions.requestPermissions(this, "You need bluetooth permission",
                    RC_BLUETOOTH, params);
            return;
        }
        mService = new BluetoothService(this, new BluetoothHandler(this));
    }
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        // TODO: 10/11/17 do something
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        // TODO: 10/11/17 do something
    }

    @Override
    public void onDeviceConnected() {
        isPrinterReady = true;
//        mBluetoothConnectProgressDialog.dismiss();

        swPrint.setText("In hóa đơn ("+name+")");
        printText();

    }

    @Override
    public void onDeviceConnecting() {

        swPrint.setText("Đang kết nối...");

    }


    @Override
    public void onDeviceConnectionLost() {
        isPrinterReady = false;
        swPrint.setText("In hóa đơn (Mất kết nối)");
    }

    @Override
    public void onDeviceUnableToConnect() {
        swPrint.setText("Kết nối lỗi, Vui lòng thử lại");
    }

    public void printText() {
        if (!mService.isAvailable()) {
            Log.i(TAG, "printText: perangkat tidak support bluetooth");
            Toast.makeText(this, "Thiết bị không hỗ trợ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isPrinterReady) {
//            if (etText.getText().toString().isEmpty()) {
//                Toast.makeText(this, "Cant print null text", Toast.LENGTH_SHORT).show();
//                return;
//            }

            PrintRes();
            PrintTime();
            PrintData();
//            PrintTotal();

            addPay();
        } else {
            if (mService.isBTopen())
                startActivityForResult(new Intent(this, DeviceListActivity.class), RC_CONNECT_DEVICE);
            else
                requestBluetooth();
        }
    }

    private void PrintRes (){
        mService.write(PrinterCommands.ESC_ALIGN_CENTER);
        mService.sendMessage(Config.VNCharacterUtils.removeAccent(getResName), "UTF-8");
        mService.sendMessage(Config.VNCharacterUtils.removeAccent(getResPhone), "UTF-8");
        mService.sendMessage(Config.VNCharacterUtils.removeAccent(getResAddress), "UTF-8");
        mService.sendMessage("--------------------------------", "UTF-8");
        mService.write(PrinterCommands.PRINTE_TEST);
    }
    private void PrintTime (){
        String date = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());
        String time = new SimpleDateFormat("kk:mm", Locale.getDefault()).format(new Date());
        mService.write(PrinterCommands.ESC_ALIGN_LEFT);
        mService.sendMessage("Ban so: "+getNumber, "UTF-8");
        mService.write(PrinterCommands.ESC_ALIGN_RIGHT);
        mService.sendMessage(time+"  "+date, "UTF-8");
        mService.write(PrinterCommands.ESC_ALIGN_CENTER);
        mService.sendMessage("--------------------------------", "UTF-8");
        mService.write(PrinterCommands.PRINTE_TEST);
    }
    private void PrintData (){

        mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.NUMBER).document(getIdNumber).collection("unpaid").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                orderedList.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    String id = documentSnapshot.getId();
                    String foodname = documentSnapshot.getString(Config.FOODNAME);
                    String amount = documentSnapshot.getString(Config.AMOUNT);
                    String total = documentSnapshot.getString(Config.TOTAL);
                    mService.write(PrinterCommands.ESC_ALIGN_LEFT);
                    mService.sendMessage(amount+" "+ Config.convertUnicode(foodname), "");
                    mService.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    mService.sendMessage(total, "");
                    mService.write(PrinterCommands.PRINTE_TEST);
                }
                PrintTotal();
            }
        });

        mService.write(PrinterCommands.ESC_ALIGN_CENTER);
        mService.sendMessage("--------------------------------", "");
        mService.write(PrinterCommands.PRINTE_TEST);
    }
    private void PrintTotal (){
        String total = tvTotalAll.getText().toString();
        String discount = edtDiscount.getText().toString();
        mService.write(PrinterCommands.ESC_ALIGN_RIGHT);
        if (!discount.equals("0")){
            mService.sendMessage("Chiet khau: "+discount+" %", "");
        }
        mService.sendMessage("Tong tien: "+total, "");
        mService.write(PrinterCommands.ESC_ALIGN_CENTER);
        mService.sendMessage("Hen gap lai quy khach", "");
        mService.write(PrinterCommands.ESC_ENTER);
    }
    private void requestBluetooth() {
        if (mService != null) {
            if (!mService.isBTopen()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, RC_ENABLE_BLUETOOTH);
            }
        }
    }

    private void loadOrdered (){
        tongtien = 0;
        mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.NUMBER).document(getIdNumber).collection("unpaid").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                orderedList.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    String id = documentSnapshot.getId();
                    Ordered ordered1 = documentSnapshot.toObject(Ordered.class).withId(id);
                    orderedList.add(ordered1);
                    oredredAdapter.notifyDataSetChanged();
                    String total = documentSnapshot.getString(Config.TOTAL);
                    tongtien += Integer.valueOf(total);
                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    formatter.applyPattern("#,###,###,###");
                    tvTotal.setText(formatter.format(tongtien));
                    tvTotalAll.setText(formatter.format(tongtien));
                }
                mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.NUMBER).document(getIdNumber).update(Config.TOTAL, String.valueOf(tongtien));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        orderedList.clear();
        mFirestore.collection(Config.RESTAURANTS+"/"+getResEmail+"/"+Config.NUMBER+"/"+getIdNumber+"/unpaid").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null){
//                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }else {
                    tongtien = 0;
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                        final String orderId = doc.getDocument().getId();
                        switch (doc.getType()){
                            case ADDED:
                                Ordered ordered = doc.getDocument().toObject(Ordered.class).withId(orderId);
                                String total = doc.getDocument().getString(Config.TOTAL);
                                orderedList.add(ordered);
                                oredredAdapter.notifyDataSetChanged();
                                tongtien += Integer.valueOf(total);
                                DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                                formatter.applyPattern("#,###,###,###");
                                tvTotal.setText(formatter.format(tongtien));
                                tvTotalAll.setText(formatter.format(tongtien));
                                break;
                            case REMOVED:
                                loadOrdered();
                                break;
                            case MODIFIED:
                                loadOrdered();
                                break;
                        }
                    }
                    mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.NUMBER).document(getIdNumber).update(Config.TOTAL, String.valueOf(tongtien));
                }
            }
        });

    }
}
