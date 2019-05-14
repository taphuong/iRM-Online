package org.irestaurant.irm;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
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
import org.irestaurant.irm.Database.Ordered;
import org.irestaurant.irm.Database.OredredAdapter;
import org.irestaurant.irm.Database.PrinterCommands;
import org.irestaurant.irm.Database.Recent;
import org.irestaurant.irm.Database.RecentAdapter;
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
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class RecentActivity extends Activity {
    String getResName, getResPhone, getResAddress, getResEmail, number, date, time, getIdRecent, rDate;
    TextView tvResName, tvResPhone, tvResAddress, tvNumber, tvDate, tvTotal, tvTotalAll, tvDiscount, tvTC, tvCK, tvTy;
    RecyclerView lvRecent;
    Button btnBack;
    SessionManager sessionManager;
    RecentAdapter recentAdapter;
    List<Recent> recentList;
    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    FloatingActionButton btnPrinter;

    /******************************************************************************************************/
    public String name = "Chưa kết nối", address = "Null";
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

    public static final int RC_BLUETOOTH = 0;
    public static final int RC_CONNECT_DEVICE = 1;
    public static final int RC_ENABLE_BLUETOOTH = 2;
    /******************************************************************************************************/

    private void Anhxa(){
        tvResName = findViewById(R.id.tv_resname);
        tvResPhone = findViewById(R.id.tv_resphone);
        tvResAddress = findViewById(R.id.tv_resaddress);
        tvNumber = findViewById(R.id.tv_number);
        tvDate = findViewById(R.id.tv_date);
        tvTotal = findViewById(R.id.tv_total);
        tvDiscount = findViewById(R.id.tv_discount);
        tvTC        = findViewById(R.id.tv_tc);
        tvCK        = findViewById(R.id.tv_ck);
        tvTotalAll = findViewById(R.id.tv_totalall);
        lvRecent = findViewById(R.id.lv_recent);
        btnBack = findViewById(R.id.btn_back);
        btnPrinter = findViewById(R.id.btn_printer);
        tvTy = findViewById(R.id.ty);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);
        Anhxa();
        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        getResName = user.get(sessionManager.RESNAME);
        getResPhone = user.get(sessionManager.RESPHONE);
        getResAddress = user.get(sessionManager.RESADDRESS);
        getResEmail = user.get(sessionManager.RESEMAIL);

        tvResName.setText(getResName);
        tvResPhone.setText(getResPhone);
        tvResAddress.setText(getResAddress);

        recentList = new ArrayList<>();
        recentAdapter = new RecentAdapter(this, recentList);
        lvRecent.setHasFixedSize(true);
        lvRecent.setLayoutManager(new LinearLayoutManager(this));
        lvRecent.setAdapter(recentAdapter);

        Intent intent = getIntent();
        date = intent.getExtras().getString("date");
        time = intent.getExtras().getString("time");
        number = intent.getExtras().getString("number");
        String total = intent.getExtras().getString("total");
        String discount = intent.getExtras().getString("discount");
        getIdRecent = intent.getExtras().getString("id");
        String before = intent.getExtras().getString("before");
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###,###,###");
        tvNumber.setText("Bàn số: "+number);
        tvDate.setText(date+"  "+time);
        tvTotal.setText(formatter.format(Integer.valueOf(before)));

        String dd = date.substring(0,2);
        String MM = date.substring(3,5);
        String yyyy = date.substring(6,10);
        rDate = yyyy+MM+dd;

        if (discount.equals("0")){
            tvTC.setVisibility(View.GONE);
            tvCK.setVisibility(View.GONE);
            tvDiscount.setVisibility(View.GONE);
            tvTotal.setVisibility(View.GONE);
        }else {
            tvDiscount.setText(discount+"%");
        }
        tvTotalAll.setText(formatter.format(Integer.valueOf(total)));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnPrinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        recentList.clear();
        mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.HISTORY).document(rDate).collection(Config.PAID).document(getIdRecent).collection(Config.FOOD).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                        switch (doc.getType()){
                            case ADDED:
                                Recent recent = doc.getDocument().toObject(Recent.class);
                                recentList.add(recent);
                                recentAdapter.notifyDataSetChanged();
                                break;
                        }
                    }
            }
        });
    }
//    Print
//    Print
    public void connectPrinter(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(RecentActivity.this, "Chưa kết nối máy in", Toast.LENGTH_SHORT).show();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent,
                        REQUEST_ENABLE_BT);
            } else {
                ListPairedDevices();
                Intent connectIntent = new Intent(RecentActivity.this,
                        DeviceListActivity.class);
                startActivityForResult(connectIntent,
                        REQUEST_CONNECT_DEVICE);
            }
        }
    }

    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);
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
            Toast.makeText(RecentActivity.this, "DeviceConnected", Toast.LENGTH_SHORT).show();
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
        mService = new BluetoothService(this, new BluetoothHandler(RecentActivity.this));
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
//        Toast.makeText(this, "Mất kết nối", Toast.LENGTH_SHORT).show();
//        swPrint.setText("Đang kết nối...");

    }


    @Override
    public void onDeviceConnectionLost() {
        isPrinterReady = false;
        Toast.makeText(this, "Mất kết nối", Toast.LENGTH_SHORT).show();
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
                DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                formatter.applyPattern("#,###,###,###");
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    String id = documentSnapshot.getId();
                    String foodname = documentSnapshot.getString(Config.FOODNAME);
                    String amount = documentSnapshot.getString(Config.AMOUNT);
                    String total = documentSnapshot.getString(Config.TOTAL);
                    mService.write(PrinterCommands.ESC_ALIGN_LEFT);
                    mService.sendMessage(amount+" "+ Config.VNCharacterUtils.removeAccent(foodname), "UTF-8");
                    mService.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    mService.sendMessage(formatter.format(Integer.valueOf(total)), "");
                    mService.write(PrinterCommands.PRINTE_TEST);
                }
                PrintTotal();
            }
        });


    }
    private void PrintTotal (){
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###,###,###");
        String total = tvTotalAll.getText().toString();
        String discount = tvDiscount.getText().toString();
        mService.write(PrinterCommands.ESC_ALIGN_CENTER);
        mService.sendMessage("--------------------------------", "");
        mService.write(PrinterCommands.PRINTE_TEST);
        mService.write(PrinterCommands.ESC_ALIGN_RIGHT);
        if (!discount.equals("0")){
            mService.sendMessage("Chiet khau: "+discount+" %", "UTF-8");
        }

        mService.sendMessage("Tong tien: "+total, "UTF-8");
        mService.write(PrinterCommands.ESC_ALIGN_CENTER);
        mService.sendMessage("Hen gap lai quy khach", "UTF-8");
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

}
