package org.irestaurant.irm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
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
import org.irestaurant.irm.Database.SessionFinger;
import org.irestaurant.irm.Database.SessionManager;

import java.util.Map;

public class LoginActivity extends Activity {
    String fingerEmail, fingerPassword;
    EditText edtPhone, edtPassword;
    TextView tvForgot, tvRegister, tvEmail;
    Button btnLogin, btnClear;
    SessionManager sessionManager;
    SessionFinger sessionFinger;
    ImageView ivFringer;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ProgressDialog progressDialog;
    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();


    private void Anhxa(){
        edtPhone    = findViewById(R.id.edt_phone);
        edtPassword = findViewById(R.id.edt_password);
        btnLogin    = findViewById(R.id.btn_login);
        ivFringer   = findViewById(R.id.iv_fringer);
        tvForgot    = findViewById(R.id.tv_forgot);
        tvRegister  = findViewById(R.id.tv_register);
        tvEmail     = findViewById(R.id.tv_email);
        btnClear    = findViewById(R.id.btn_clear);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sessionManager = new SessionManager(this);
        sessionFinger = new SessionFinger(this);
        Map<String, String> finger = sessionFinger.getFinger();
        fingerEmail = finger.get(sessionFinger.EMAIL);
        fingerPassword = finger.get(sessionFinger.PASSWORD);
        FirebaseApp.initializeApp(this);
        Config.CHECKACTIVITY = "LoginActivity";
        Anhxa();
        if (sessionFinger.isFinger()){
            tvEmail.setText(fingerEmail);
            tvEmail.setVisibility(View.VISIBLE);
            btnClear.setVisibility(View.VISIBLE);
            edtPhone.setVisibility(View.INVISIBLE);
        }else {
            tvEmail.setVisibility(View.INVISIBLE);
            btnClear.setVisibility(View.INVISIBLE);
            edtPhone.setVisibility(View.VISIBLE);
        }
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvEmail.setVisibility(View.INVISIBLE);
                btnClear.setVisibility(View.INVISIBLE);
                edtPhone.setVisibility(View.VISIBLE);
                sessionFinger.clearFinger();
            }
        });

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

                    loginFirebase(phone, password);
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
                if (sessionFinger.isFinger()){
                    Intent intent = new Intent(LoginActivity.this, FingerActivity.class);
                    startActivityForResult(intent,1);
                }else {
                    Toast.makeText(LoginActivity.this, "Vui lòng vào cài đặt để đăng ký vân tay", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void tv_register(View view){
        startActivity(new Intent(this,RegisterActivity.class));
    }

    private void loginFirebase(final String Email, String Password){
        progressDialog = ProgressDialog.show(this,
                "Đang đăng nhập", "Vui lòng đợi ...", true, false);
//        final String Email = edtPhone.getText().toString().trim();
//        final String Password = edtPassword.getText().toString().trim();
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
                                    String ResEmail = documentSnapshot.getString(Config.RESEMAIL);
//                                    if (mPosition.equals("admin")){
                                        String mResname = documentSnapshot.getString(Config.RESNAME);
                                        String mResaddress = documentSnapshot.getString(Config.RESADDRESS);
                                        String mResphone = documentSnapshot.getString(Config.RESPHONE);
                                        sessionManager.createSession(uID,mName,mAuth.getCurrentUser().getEmail(),ResEmail, edtPassword.getText().toString(), mResname,mResphone,mResaddress,mPosition,mImage);
                                        Toast.makeText(LoginActivity.this, "Xin chào "+mName, Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();

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

                    }
                });
    }


}
