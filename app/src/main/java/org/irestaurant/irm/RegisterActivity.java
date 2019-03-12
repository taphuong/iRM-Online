package org.irestaurant.irm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.irestaurant.irm.Database.DatabaseHelper;
import org.irestaurant.irm.Database.SessionManager;
import org.irestaurant.irm.Database.User;

public class RegisterActivity extends Activity {

    EditText edtName, edtPhone, edtResName, edtResPhone, edtResAddress, edtPassword, edtCPassword;
    Button btnConfirm, btnAddPhone;

    DatabaseHelper db;

    SessionManager sessionManager;


    private void AnhXa (){
        edtName     = findViewById(R.id.edt_name);
        edtPhone    = findViewById(R.id.edt_phone);
        edtPassword = findViewById(R.id.edt_password);
        edtCPassword= findViewById(R.id.edt_cpassword);
        edtResName  = findViewById(R.id.edt_resname);
        edtResPhone = findViewById(R.id.edt_resphone);
        edtResAddress = findViewById(R.id.edt_resaddress);
        btnConfirm  = findViewById(R.id.btn_regist);
        btnAddPhone = findViewById(R.id.btn_addphone);
        sessionManager = new SessionManager(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = new DatabaseHelper(this);
        AnhXa();
        sessionManager = new SessionManager(this);

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
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Confirm();
            }
        });
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
    }
    //    Đăng ký
    private void Confirm (){
        String name = edtName.getText().toString();
        String phone = edtPhone.getText().toString();
        String password = edtPassword.getText().toString();
        String cpassword = edtCPassword.getText().toString();
        String resname = edtResName.getText().toString();
        String resphone = edtResPhone.getText().toString();
        String resaddress = edtResAddress.getText().toString();
        if (name.isEmpty()){
            edtName.setError("Thiếu thông tin");
            edtName.requestFocus();
        }else if (phone.isEmpty()){
            edtPhone.setError("Thiếu thông tin");
            edtPhone.requestFocus();
        }else if (password.isEmpty()){
            edtPassword.setError("Thiếu thông tin");
            edtPassword.requestFocus();
        }else if (cpassword.isEmpty()){
            edtCPassword.setError("Thiếu thông tin");
            edtCPassword.requestFocus();
        }else if (resname.isEmpty()){
            edtResName.setError("Thiếu thông tin");
            edtResName.requestFocus();
        }else if (resphone.isEmpty()){
            edtResPhone.setError("Thiếu thông tin");
            edtResPhone.requestFocus();
        }else if (resaddress.isEmpty()){
            edtResAddress.setError("Thiếu thông tin");
            edtResAddress.requestFocus();
        }else if (!password.equals(cpassword)){
            edtCPassword.setError("Mật khẩu xác nhận không đúng");
            edtCPassword.requestFocus();
        }else {
//            User user = createUser(name,resname,resphone,resaddress);
////            if (user !=null){
//                dbManager.addUser(user);
//                registed();
////            }

            Boolean chkphone = db.chkphone(phone);
            if (chkphone == true) {
                Regist();
            }else {
                edtPhone.setError("Số điện thoại đã tồn tại");
                edtPhone.requestFocus();
            }
        }
    }

    private void Regist (){
        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        User user = new User();
        user.setName(edtName.getText().toString());
        user.setPassword(edtPassword.getText().toString());
        user.setPhone(edtPhone.getText().toString());
        user.setResname(edtResName.getText().toString());
        user.setResphone(edtResPhone.getText().toString());
        user.setResaddress(edtResAddress.getText().toString());
        if (db.creat(user)){
            Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,LoginActivity.class));
            finish();
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
}
