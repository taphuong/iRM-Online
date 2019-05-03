package org.irestaurant.irm;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import org.irestaurant.irm.Database.Config;
import org.irestaurant.irm.Database.SessionManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.androidhive.barcode.BarcodeReader;
import info.androidhive.barcode.ScannerOverlay;

public class JoinActivity extends Activity {
    TextView tvPreview;
    EditText edtEmail;
    Button btnConfirm, btnInput, btnQr, btnClose;
    RelativeLayout layoutInput, layoutQr;
    SurfaceView qrScanner;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    ScannerOverlay scannerOverlay;
    String getEmail, getName, getImage, getToken;
    SessionManager sessionManager;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    private void Anhxa() {
        tvPreview = findViewById(R.id.tv_preview);
        btnConfirm = findViewById(R.id.btn_confirm);
        btnInput = findViewById(R.id.btn_input);
        btnQr = findViewById(R.id.btn_qr);
        btnClose = findViewById(R.id.btn_close);
        edtEmail = findViewById(R.id.edt_email);
        layoutInput = findViewById(R.id.layout_input);
        layoutQr = findViewById(R.id.layout_qr);
        qrScanner = findViewById(R.id.barcode_scanner);
        scannerOverlay = findViewById(R.id.sc_overlay);
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(640, 480).build();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        Anhxa();
        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        getEmail = user.get(sessionManager.EMAIL);
        getName = user.get(sessionManager.NAME);
        getImage = user.get(sessionManager.IMAGE);
        getToken = FirebaseInstanceId.getInstance().getToken();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmJoin();
            }
        });
        btnQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutInput.setVisibility(View.GONE);
                layoutQr.setVisibility(View.VISIBLE);
                btnQr.setEnabled(false);
                btnInput.setEnabled(true);
                btnInput.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
                btnQr.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
            }
        });
        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutInput.setVisibility(View.VISIBLE);
                layoutQr.setVisibility(View.GONE);
                btnQr.setEnabled(true);
                btnInput.setEnabled(false);
                btnInput.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
                btnQr.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);

            }
        });
        qrScanner.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                } try {
                    cameraSource.start(holder);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();
                if (qrCodes.size()!=0){
                    tvPreview.post(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(500);
                            String resEmail = qrCodes.valueAt(0).displayValue;
                            tvPreview.setText(resEmail);
                            confirmJoinSC(resEmail);

                            qrScanner.setVisibility(View.GONE);
                            scannerOverlay.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void confirmJoin() {
        final String resEmail = edtEmail.getText().toString().trim();
        if (resEmail.isEmpty()){
            edtEmail.requestFocus();
            edtEmail.setError(String.valueOf(R.string.isempty));
        }else if (!Patterns.EMAIL_ADDRESS.matcher(resEmail).matches()) {
            edtEmail.requestFocus();
            edtEmail.setError("Email sai định dạng");
        }else {
            mFirestore.collection(Config.RESTAURANTS).document(resEmail).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()){
                        Map<String ,Object> joinMap = new HashMap<>();
                        joinMap.put(Config.NAME, getName);
                        joinMap.put(Config.EMAIL, getEmail);
                        joinMap.put(Config.IMAGE, getImage);
                        joinMap.put(Config.STATUS, "join");
                        joinMap.put(Config.TOKENID, getToken);
                        mFirestore.collection(Config.RESTAURANTS).document(resEmail).collection(Config.PEOPLE).document(getEmail).set(joinMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(JoinActivity.this, "Đã gửi yêu cầu tham gia", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }else {
                        edtEmail.setError("Sai địa chỉ Email");
                        edtEmail.requestFocus();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(JoinActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void confirmJoinSC(final String resemail) {
            mFirestore.collection(Config.RESTAURANTS).document(resemail).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()){
                        mFirestore.collection("Users").document(resemail).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                final String resName = documentSnapshot.getString(Config.RESNAME);
                                Map<String ,Object> joinMap = new HashMap<>();
                                joinMap.put(Config.NAME, getName);
                                joinMap.put(Config.EMAIL, getEmail);
                                joinMap.put(Config.IMAGE, getImage);
                                joinMap.put(Config.STATUS, "join");
                                joinMap.put(Config.TOKENID, getToken);
                                mFirestore.collection(Config.RESTAURANTS).document(resemail).collection(Config.PEOPLE).document(getEmail).set(joinMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "Đã gửi yêu cầu đến "+ resName, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            }
                        });
                    }else {
                        edtEmail.setError("Sai địa chỉ Email");
                        edtEmail.requestFocus();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

    }

}
