package org.irestaurant.irm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import org.irestaurant.irm.Database.Config;
import org.irestaurant.irm.Database.SessionManager;
import java.util.HashMap;
import java.util.Map;

public class NewresActivity extends Activity {
    SessionManager sessionManager;
    String getName, getImage, getToken, getEmail, getID, getPassword;
    EditText edtResname, edtResPhone, edtResAddress;
    Button btnAddPhone, btnConfirm, btnSaveV;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    private void Anhxa(){
        edtResname      = findViewById(R.id.edt_resname);
        edtResPhone     = findViewById(R.id.edt_resphone);
        edtResAddress   = findViewById(R.id.edt_resaddress);
        btnAddPhone     = findViewById(R.id.btn_addphone);
        btnConfirm      = findViewById(R.id.btn_confirm);
        btnSaveV        = findViewById(R.id.btn_saveV);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newres);
        Anhxa();
        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        getName     = user.get(sessionManager.NAME);
        getEmail    = user.get(sessionManager.EMAIL);
        getImage    = user.get(sessionManager.IMAGE);
        getToken    = FirebaseInstanceId.getInstance().getToken();
        getID       = user.get(sessionManager.ID);
        getPassword = user.get(sessionManager.PASSWORD);

        btnSaveV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRes();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRes();
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

    private void saveRes() {
        final String resName = edtResname.getText().toString().trim();
        final String resPhone = edtResPhone.getText().toString().trim();
        final String resAddress = edtResAddress.getText().toString().trim();
        if (resName.isEmpty()){
            edtResname.setError("Thiếu thông tin");
            edtResname.requestFocus();
        }else if (resPhone.isEmpty()){
            edtResPhone.setError("Thiếu thông tin");
            edtResPhone.requestFocus();
        }else if (resAddress.isEmpty()){
            edtResAddress.setError("Thiếu thông tin");
            edtResAddress.requestFocus();
        }else {
            final ProgressDialog progressDialog = ProgressDialog.show(NewresActivity.this,
                    "Đang đăng ký cửa hàng", "Đang cập nhật thông tin", true, false);
            mFirestore.collection(Config.RESTAURANTS).document().collection(Config.PEOPLE).document(getEmail).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Map<String, Object> resMap = new HashMap<>();
                    resMap.put(Config.RESNAME, resName);
                    resMap.put(Config.RESPHONE, resPhone);
                    resMap.put(Config.RESADDRESS, resAddress);
                    mFirestore.collection(Config.RESTAURANTS).document(getEmail).set(resMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Map<String, Object> resMap = new HashMap<>();
                            resMap.put(Config.NAME, getName);
                            resMap.put(Config.EMAIL, getEmail);
                            resMap.put(Config.STATUS, "admin");
                            resMap.put(Config.TOKENID, getToken);
                            resMap.put(Config.IMAGE, getImage);
                            mFirestore.collection(Config.RESTAURANTS).document(getEmail).collection("People").document(getEmail).set(resMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Map<String, Object> posMap = new HashMap<>();
                                    posMap.put(Config.RESNAME, resName);
                                    posMap.put(Config.RESPHONE, resPhone);
                                    posMap.put(Config.RESADDRESS, resAddress);
                                    posMap.put(Config.RESEMAIL, getEmail);
                                    posMap.put(Config.POSITION, "admin");
                                    mFirestore.collection("Users").document(getEmail).update(posMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(NewresActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                            sessionManager.createSession(getID, getName,getEmail, getEmail, getPassword, resName, resPhone, resAddress, "admin", getImage);
                                            progressDialog.dismiss();
                                            startActivity(new Intent(NewresActivity.this, MainActivity.class));
                                            finish();
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
        }
    }
}
