package org.irestaurant.irm;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.irestaurant.irm.Database.DatabaseHelper;
import org.irestaurant.irm.Database.SessionManager;
import org.irestaurant.irm.Database.User;

public class LoginActivity extends Activity {

    EditText edtPhone, edtPassword;
    Button btnLogin;
    DatabaseHelper db;
    SessionManager sessionManager;
    ImageView ivFringer;

    private void Anhxa(){
        edtPhone    = findViewById(R.id.edt_phone);
        edtPassword = findViewById(R.id.edt_password);
        btnLogin    = findViewById(R.id.btn_login);
        ivFringer   = findViewById(R.id.iv_fringer);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
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
                }else {
                    login();
                }
            }
        });
        ivFringer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Tính năng đang được phát triển", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void tv_register(View view){
        startActivity(new Intent(this,RegisterActivity.class));
    }

    private void login(){
        DatabaseHelper db = new DatabaseHelper(this);
        String phone = edtPhone.getText().toString();
        String password = edtPassword.getText().toString();

        Boolean chkphone = db.chkphone(phone);
        if (chkphone == true) {
            edtPhone.setError("Số điện thoại chưa đăng ký");
            edtPhone.requestFocus();
        }else {
            User user = db.userLogin(phone, password);
            if (user == null){
                edtPassword.setError("Sai mật khẩu");
                edtPassword.requestFocus();
            }else {
                sessionManager.createSession(user.getId(),user.getName(),user.getPhone(), user.getPassword(),user.getResname(),user.getResphone(),user.getResaddress());
                Toast.makeText(this, "Xin chào "+user.getName(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        }


    }
}
