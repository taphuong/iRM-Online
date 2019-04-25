package org.irestaurant.irm;

import android.app.Activity;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.irestaurant.irm.Database.Config;
import org.irestaurant.irm.Database.DatabaseHelper;
import org.irestaurant.irm.Database.FingerprintHandler;
import org.irestaurant.irm.Database.SessionManager;
import org.irestaurant.irm.Database.User;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class LoginActivity extends Activity {

    EditText edtPhone, edtPassword;
    TextView tvForgot, tvRegister;
    Button btnLogin;
    DatabaseHelper db;
    SessionManager sessionManager;
    ImageView ivFringer;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ProgressDialog progressDialog;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();


    private void Anhxa(){
        edtPhone    = findViewById(R.id.edt_phone);
        edtPassword = findViewById(R.id.edt_password);
        btnLogin    = findViewById(R.id.btn_login);
        ivFringer   = findViewById(R.id.iv_fringer);
        tvForgot    = findViewById(R.id.tv_forgot);
        tvRegister  = findViewById(R.id.tv_register);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        FirebaseApp.initializeApp(this);

        Anhxa();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = edtPhone.getText().toString();
                String password = edtPassword.getText().toString();
                if (phone.isEmpty()){
                    edtPhone.setError("Thiếu thông tin");
                    edtPhone.requestFocus();
                }else if (password.isEmpty()){
                    edtPassword.setError("Thiếu thông tin");
                    edtPassword.requestFocus();
                }else if (!Patterns.EMAIL_ADDRESS.matcher(phone).matches()){
                    edtPhone.requestFocus();
                    edtPhone.setError("Email không chính xác");
                }else {
                    tvForgot.setEnabled(false);
                    tvRegister.setEnabled(false);
                    edtPhone.setEnabled(false);
                    edtPassword.setEnabled(false);
                    btnLogin.setEnabled(false);
                    ivFringer.setEnabled(false);

                    loginFirebase();
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ivFringer.setVisibility(View.VISIBLE);
        }else {
            ivFringer.setVisibility(View.GONE);
        }
        ivFringer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Tính năng đang được phát triển", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(LoginActivity.this, FingerActivity.class);
//                startActivityForResult(intent,1);
            }
        });

    }

    public void tv_register(View view){
        startActivity(new Intent(this,RegisterActivity.class));
    }

    private void loginFirebase(){
        progressDialog = ProgressDialog.show(LoginActivity.this,
                "Đang đăng nhập", "Vui lòng đợi ...", true, false);
        final String Email = edtPhone.getText().toString().trim();
        final String Password = edtPassword.getText().toString().trim();
        mAuth.signInWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            final String uID = mAuth.getCurrentUser().getUid();
                            mFirestore.collection("Users").document(Email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String mName = documentSnapshot.getString("name");
                                    String mImage = documentSnapshot.getString(Config.IMAGE);
                                    String mPosition = documentSnapshot.getString(Config.POSITION);
                                    if (mPosition.equals("admin")){
                                        String mResname = documentSnapshot.getString(Config.RESNAME);
                                        String mResaddress = documentSnapshot.getString(Config.RESADDRESS);
                                        String mResphone = documentSnapshot.getString(Config.RESPHONE);
                                        sessionManager.createSession(uID,mName,mAuth.getCurrentUser().getEmail(), edtPassword.getText().toString(), mResname,mResphone,mResaddress,mPosition,mImage);
                                        Toast.makeText(LoginActivity.this, "Xin chào "+mName, Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    }else {
                                        String mResname = "Chưa có cửa hàng";
                                        String mResphone = "Chưa có cửa hàng";
                                        String mResaddress = "Chưa có cửa hàng";
                                        sessionManager.createSession(uID,mName,mAuth.getCurrentUser().getEmail(), edtPassword.getText().toString(), mResname,mResphone,mResaddress,mPosition,mImage);
                                        Toast.makeText(LoginActivity.this, "Xin chào "+mName, Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    }

                                }
                            });

                        } else {
                            Toast.makeText(LoginActivity.this, "Thông tin đăng nhập sai", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            tvForgot.setEnabled(true);
                            tvRegister.setEnabled(true);
                            edtPhone.setEnabled(true);
                            edtPassword.setEnabled(true);
                            btnLogin.setEnabled(true);
                            ivFringer.setEnabled(true);
                            progressDialog.dismiss();
                            edtPhone.requestFocus();
                            edtPassword.setText("");
                        }

                        // ...
                    }
                });
    }


}
