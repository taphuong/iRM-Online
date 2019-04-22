package org.irestaurant.irm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.irestaurant.irm.Database.DatabaseHelper;
import org.irestaurant.irm.Database.SessionManager;
import org.irestaurant.irm.Database.User;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends Activity {

    EditText edtName, edtPhone, edtResName, edtResPhone, edtResAddress, edtPassword, edtCPassword;
    Button btnConfirm, btnAddPhone;
    DatabaseHelper db;
    SessionManager sessionManager;
    CircleImageView ivPicture;
    ProgressDialog progressDialog;
//    Firebase
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private FirebaseFirestore mFirestore;
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
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = new DatabaseHelper(this);
        AnhXa();
        sessionManager = new SessionManager(this);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference().child("images");
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
        }else if (password.length()<6){
            edtPassword.setError("Mật khẩu ít hơn 6 ký tự");
            edtPassword.requestFocus();
        }else if (resname.isEmpty()){
            edtResName.setError("Thiếu thông tin");
            edtResName.requestFocus();
        }else if (resphone.isEmpty()){
            edtResPhone.setError("Thiếu thông tin");
            edtResPhone.requestFocus();
        }else if (resaddress.isEmpty()){
            edtResAddress.setError("Thiếu thông tin");
            edtResAddress.requestFocus();
        }else if (!Patterns.EMAIL_ADDRESS.matcher(phone).matches()){
            edtPhone.requestFocus();
            edtPhone.setError("Email không chính xác");
        }else if (!password.equals(cpassword)){
            edtCPassword.setError("Mật khẩu xác nhận không đúng");
            edtCPassword.requestFocus();
        }else {
            createFirebase();

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
    private void createFirebase(){
        final String mPhone = edtPhone.getText().toString();
        String mPass = edtPassword.getText().toString().trim();
        if (imageUri != null){
            progressDialog = ProgressDialog.show(RegisterActivity.this,
                    "Đang đăng ký", "Vui lòng đợi ...", true, false);
            mAuth.createUserWithEmailAndPassword(mPhone,mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        final String user_id = mAuth.getCurrentUser().getUid();
                        final StorageReference user_profile = mStorage.child(user_id+".jpg");
                        final String mName = edtName.getText().toString().trim();
                        String mResname = edtResName.getText().toString().trim();
                        String mResphone = edtResPhone.getText().toString().trim();
                        String mResaddress = edtResAddress.getText().toString().trim();

                        String token_id = FirebaseInstanceId.getInstance().getToken();


                        Map<String,Object> nameMap = new HashMap<>();
                        nameMap.put("email",mPhone);
                        nameMap.put("name",mName);
                        nameMap.put("resname",mResname);
                        nameMap.put("resphone",mResphone);
                        nameMap.put("resaddress",mResaddress);
                        nameMap.put("token_id",token_id);

                        mFirestore.collection("Users").document(user_id).set(nameMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                user_profile.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()){
                                            progressDialog.dismiss();
                                            progressDialog = ProgressDialog.show(RegisterActivity.this,
                                                    "Đang đăng ký", "Đang tải ảnh lên ...", true, false);
                                            String download_url = task.getResult().getMetadata().getReference().getDownloadUrl().toString();
                                            Map<String ,Object> userMap = new HashMap<>();
                                            userMap.put("image", download_url);
                                            mFirestore.collection("Users").document(user_id).update(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                    finish();
                                                    progressDialog.dismiss();
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
        if (requestCode == 1){
            imageUri = data.getData();
            if (imageUri != null) {
                ivPicture.setImageURI(imageUri);
            }
        }
    }
}
