package org.irestaurant.irm;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.irestaurant.irm.Database.DatabaseHelper;
import org.irestaurant.irm.Database.SessionManager;
import org.irestaurant.irm.Database.User;

import java.util.HashMap;

public class AccountActivity extends Activity {

    SessionManager sessionManager;
    EditText edtName, edtEmail,edtResName, edtResPhone, edtResAddress, edtPassword, edtNew, edtNew2;
    Button btnSave, btnHome, btnAddPhone, btnSaveV;
    LinearLayout layoutNewPass;
    RelativeLayout layoutChangeRes;
    CheckBox cbChange, cbChangeRes;
    String getName, getEmail, getPassword, getResName, getResPhone, getResAddress;
    DatabaseHelper db;
    User user;


    private void Anhxa(){
        edtName     = findViewById(R.id.edt_name);
        edtEmail    = findViewById(R.id.edt_email);
        edtResName  = findViewById(R.id.edt_resname);
        edtResPhone = findViewById(R.id.edt_resphone);
        edtResAddress = findViewById(R.id.edt_resaddress);
        edtPassword = findViewById(R.id.edt_oldpass);
        edtNew      = findViewById(R.id.edt_newpass);
        edtNew2     = findViewById(R.id.edt_newpass2);
        btnSave     = findViewById(R.id.btn_save);
        btnSaveV    = findViewById(R.id.btn_saveV);
        btnAddPhone = findViewById(R.id.btn_addphone);
        btnHome     = findViewById(R.id.btn_home);
        cbChange    = findViewById(R.id.cb_pass);
        cbChangeRes = findViewById(R.id.cb_changeres);
        layoutNewPass = findViewById(R.id.newpass);
        layoutChangeRes = findViewById(R.id.resinfo);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Anhxa();
        db = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        getName = user.get(sessionManager.NAME);
        getEmail = user.get(sessionManager.EMAIL);
        getPassword = user.get(sessionManager.PASSWORD);
        getResName = user.get(sessionManager.RESNAME);
        getResPhone = user.get(sessionManager.RESPHONE);
        getResAddress = user.get(sessionManager.RESADDRESS);
        edtName.setText(getName);
        edtEmail.setText(getEmail);
        edtResName.setText(getResName);
        edtResPhone.setText(getResPhone);
        edtResAddress.setText(getResAddress);

        edtResPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = edtResPhone.getText().toString();

                if (phone.isEmpty()){
                    btnAddPhone.setVisibility(View.INVISIBLE);
                }else if (phone.length()>9){
                    btnAddPhone.setVisibility(View.VISIBLE);
                }else if (phone.length()<10){
                    btnAddPhone.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnAddPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = edtResPhone.getText().toString().trim();
                String lastChar = phone.substring(phone.length() - 1);
                String twoChar = phone.substring(phone.length() - 2);
                if (lastChar.equals(",")){
                    edtResPhone.setText(phone+" ");
                    edtResPhone.setSelection(edtResPhone.getText().length());
                    btnAddPhone.setVisibility(View.INVISIBLE);
                }else if (twoChar.equals(", ")){
                    edtResPhone.setSelection(edtResPhone.getText().length());
                    btnAddPhone.setVisibility(View.INVISIBLE);
                }else {
                    edtResPhone.setText(phone+", ");
                    edtResPhone.setSelection(edtResPhone.getText().length());
                    btnAddPhone.setVisibility(View.INVISIBLE);
                }

            }
        });

        cbChange.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    layoutNewPass.setVisibility(View.VISIBLE);
                }else {
                    layoutNewPass.setVisibility(View.GONE);
                }
            }
        });
        cbChangeRes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    layoutChangeRes.setVisibility(View.VISIBLE);
                }else {
                    layoutChangeRes.setVisibility(View.GONE);
                }
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Save();
            }
        });
        btnSaveV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Save();
            }
        });
    }
    private void Save (){
        String name = edtName.getText().toString();
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        String newpass = edtNew.getText().toString();
        String newpass2 = edtNew2.getText().toString();
        String resname = edtResName.getText().toString();
        String resphone = edtResPhone.getText().toString();
        String resaddress = edtResAddress.getText().toString();
        if (name.isEmpty()){
            edtName.setError("Thiếu thông tin");
            edtName.requestFocus();
        }else if (email.isEmpty()){
            edtEmail.setError("Thiếu thông tin");
            edtEmail.requestFocus();
        }else if (password.isEmpty()){
            edtPassword.setError("Nhập mật khẩu");
            edtPassword.requestFocus();
//        }else {
//            if (cbChange.isChecked()){
//                if (newpass.isEmpty()){
//                    edtNew.setError("Nhập mật khẩu");
//                    edtNew.requestFocus();
//                }else if (!newpass.equals(newpass2)){
//                    edtNew2.setError("Mật khẩu xác nhận không khớp");
//                    edtNew2.requestFocus();
//                }else {
//                    User user = db.userLogin(email, password);
//                    if (user != null){
//                        if (!getEmail.equals(phone)) {
//                            Boolean chkphone = db.chkphone(phone);
//                            if (chkphone == true) {
//                                updateUser(name, phone, newpass, resname, resphone, resaddress);
//                            } else {
//                                edtPhone.setError("Số điện thoại đã tồn tại");
//                                edtPhone.requestFocus();
//                            }
//                        }else {
//                            updateUser(name, phone, newpass, resname, resphone, resaddress);
//                        }
//                    }else {
//                        edtPassword.setError("Sai mật khẩu");
//                        edtPassword.requestFocus();
//                    }
//                }
//            }else {
//
//                User user = db.userLogin(phone, password);
//                if (user != null){
//                    if (!getPhone.equals(phone)){
//                        Boolean chkphone = db.chkphone(phone);
//                        if (chkphone == true) {
//                            updateUser(name,phone,password, resname, resphone, resaddress);
//                        }else {
//                            edtPhone.setError("Số điện thoại đã tồn tại");
//                            edtPhone.requestFocus();
//                        }
//                    } else {
//                        updateUser(name,phone,password, resname, resphone, resaddress);
//                    }
//                }else {
//                    edtPassword.setError("Sai mật khẩu");
//                    edtPassword.requestFocus();
//                }
//            }
        }
    }

//    private void updateUser (String name, String phone, String password, String resname, String resphone, String resaddress){
////        user.setId();
//        user.setName(name);
//        user.setPhone(phone);
//        user.setPassword(password);
//        user.setResname(resname);
//        user.setResphone(resphone);
//        user.setResaddress(resaddress);
//        int result = db.updateUser(user);
//        if (result >0){
//            sessionManager.createSession(user.getId(), name,phone,password,resname,resphone,resaddress);
//            Toast.makeText(this, R.string.update_success, Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(this,MainActivity.class));
//            finish();
//        }else {
//            Toast.makeText(this, R.string.toast_error, Toast.LENGTH_SHORT).show();
//        }
//
//    }
}
