package org.irestaurant.irm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.irestaurant.irm.Database.Config;
import org.irestaurant.irm.Database.SessionManager;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends Activity {

    EditText edtName, edtPhone, edtResName, edtResPhone, edtResAddress, edtPassword, edtCPassword;
    Button btnConfirm, btnAddPhone;
    SessionManager sessionManager;
    CircleImageView ivPicture;
    ProgressDialog progressDialog;
    Switch swNewRes;
    RelativeLayout layoutRes;
//    Firebase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("images");
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private CollectionReference mCollectUser = mFirestore.collection("Users");
    private String mVerificationId, firebaseID;
    private Uri imageUri;

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
        ivPicture   = findViewById(R.id.iv_picture);
        swNewRes    = findViewById(R.id.sw_newres);
        layoutRes   = findViewById(R.id.res);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        AnhXa();
        sessionManager = new SessionManager(this);
        FirebaseApp.initializeApp(this);
        imageUri = null;

        ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Chọn ảnh đại diện"),1 );
            }
        });

        swNewRes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    layoutRes.setVisibility(View.VISIBLE);
                }else {
                    layoutRes.setVisibility(View.GONE);
                }
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
        }else if (password.length()<6) {
            edtPassword.setError("Mật khẩu ít hơn 6 ký tự");
            edtPassword.requestFocus();
        }else if (swNewRes.isChecked()) {

            if (resname.isEmpty()) {
                edtResName.setError("Thiếu thông tin");
                edtResName.requestFocus();
            } else if (resphone.isEmpty()) {
                edtResPhone.setError("Thiếu thông tin");
                edtResPhone.requestFocus();
            } else if (resaddress.isEmpty()) {
                edtResAddress.setError("Thiếu thông tin");
                edtResAddress.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(phone).matches()) {
                edtPhone.requestFocus();
                edtPhone.setError("Email không chính xác");
            } else if (!password.equals(cpassword)) {
                edtCPassword.setError("Mật khẩu xác nhận không đúng");
                edtCPassword.requestFocus();
            } else {
                createFirebase();

            }
        } else {
            createFirebase();
        }
    }

    private void createFirebase(){
        final String mEmail = edtPhone.getText().toString();
        String mPass = edtPassword.getText().toString().trim();
        if (imageUri != null){
            progressDialog = ProgressDialog.show(RegisterActivity.this,
                    "Đang đăng ký", "Vui lòng đợi ...", true, false);
            mAuth.createUserWithEmailAndPassword(mEmail,mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        final String user_id = mAuth.getCurrentUser().getUid();
                        final StorageReference user_profile = mStorage.child(mEmail+".jpg");
                        final String mName = edtName.getText().toString().trim();
                        final String mResname = edtResName.getText().toString().trim();
                        final String mResphone = edtResPhone.getText().toString().trim();
                        final String mResaddress = edtResAddress.getText().toString().trim();
                        final String token_id = FirebaseInstanceId.getInstance().getToken();

                        final Map<String, Object> nameMap = new HashMap<>();
                        if (swNewRes.isChecked()) {
                            nameMap.put(Config.RESEMAIL, mEmail);
                            nameMap.put(Config.NAME, mName);
                            nameMap.put(Config.RESNAME, mResname);
                            nameMap.put(Config.RESPHONE, mResphone);
                            nameMap.put(Config.RESADDRESS, mResaddress);
                            nameMap.put(Config.POSITION, "admin");
                            nameMap.put(Config.TOKENID, token_id);
                        } else {
                            nameMap.put(Config.RESEMAIL, "none");
                            nameMap.put(Config.NAME, mName);
                            nameMap.put(Config.RESNAME, "none");
                            nameMap.put(Config.RESPHONE, "none");
                            nameMap.put(Config.RESADDRESS, "none");
                            nameMap.put(Config.POSITION, "none");
                            nameMap.put(Config.TOKENID, token_id);
                        }

                        mFirestore.collection("Users").document(mEmail).set(nameMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                progressDialog = ProgressDialog.show(RegisterActivity.this,
                                        "Đang đăng ký", "Đang tải ảnh lên ...", true, false);
                                user_profile.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()){
                                            user_profile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    final String download_url = uri.toString();
                                                    Map<String ,Object> userMap = new HashMap<>();
                                                    userMap.put(Config.IMAGE, download_url);
                                                    mFirestore.collection("Users").document(mEmail).update(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            if (swNewRes.isChecked()) {
                                                                progressDialog.dismiss();
                                                                progressDialog = ProgressDialog.show(RegisterActivity.this,
                                                                        "Đang đăng ký cửa hàng", "Đang cập nhật thông tin", true, false);
                                                                Map<String, Object> resMap = new HashMap<>();
                                                                resMap.put(Config.RESNAME, mResname);
                                                                resMap.put(Config.RESPHONE, mResphone);
                                                                resMap.put(Config.RESADDRESS, mResaddress);
                                                                mFirestore.collection(Config.RESTAURANTS).document(mEmail).set(resMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Map<String, Object> resMap = new HashMap<>();
                                                                        resMap.put(Config.NAME, mName);
                                                                        resMap.put(Config.EMAIL, mEmail);
                                                                        resMap.put(Config.STATUS, "admin");
                                                                        resMap.put(Config.TOKENID, token_id);
                                                                        resMap.put(Config.IMAGE, download_url);
                                                                        mFirestore.collection(Config.RESTAURANTS).document(mEmail).collection("People").document(mEmail).set(resMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                                                                progressDialog.dismiss();
                                                                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                                                finish();
                                                                            }
                                                                        });
                                                                    }
                                                                });

                                                            }else {
                                                                Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                                finish();
                                                                progressDialog.dismiss();
                                                            }
                                                        }
                                                    });
                                                }
                                            });

                                        }else {
                                            progressDialog.dismiss();
                                            Toast.makeText(RegisterActivity.this, "Đăng ký lỗi: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        });
                    }else {
                        Toast.makeText(RegisterActivity.this, "Đăng ký lỗi: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Chọn ảnh đại diện"),1 );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            imageUri = data.getData();
            if (imageUri != null) {
                ivPicture.setImageURI(imageUri);
            }
        }
    }
}
