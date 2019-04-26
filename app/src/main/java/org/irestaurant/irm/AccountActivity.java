package org.irestaurant.irm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.irestaurant.irm.Database.Config;
import org.irestaurant.irm.Database.SessionManager;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends Activity {

    SessionManager sessionManager;
    EditText edtName,edtResName, edtResPhone, edtResAddress, edtPassword, edtNew, edtNew2;
    Button btnSave, btnHome, btnAddPhone, btnSaveV;
    LinearLayout layoutNewPass;
    RelativeLayout layoutChangeRes;
    CheckBox cbChange, cbChangeRes;
    String getID, getName, getEmail, getResEmail, getPassword, getResName, getResPhone, getResAddress, getImage, getPosition;
    CircleImageView ivPicture;
    ProgressDialog progressDialog;
    private Uri imageUri;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("images");


    private void Anhxa(){
        edtName     = findViewById(R.id.edt_name);
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
        ivPicture   = findViewById(R.id.iv_picture);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Anhxa();
        imageUri = null;
        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        getID = user.get(sessionManager.ID);
        getName = user.get(sessionManager.NAME);
        getEmail = user.get(sessionManager.EMAIL);
        getPassword = user.get(sessionManager.PASSWORD);
        getResName = user.get(sessionManager.RESNAME);
        getResPhone = user.get(sessionManager.RESPHONE);
        getResAddress = user.get(sessionManager.RESADDRESS);
        getImage = user.get(sessionManager.IMAGE);
        getPosition = user.get(sessionManager.POSITION);
        getResEmail = user.get(sessionManager.RESEMAIL);
        edtName.setText(getName);
        edtResName.setText(getResName);
        edtResPhone.setText(getResPhone);
        edtResAddress.setText(getResAddress);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile);
        Glide.with(getApplicationContext()).setDefaultRequestOptions(requestOptions).load(getImage).into(ivPicture);

        if (!getPosition.equals("admin")){
            cbChangeRes.setVisibility(View.INVISIBLE);
        }

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
                startActivity(new Intent(AccountActivity.this, MainActivity.class));
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
        ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Chọn ảnh đại diện"),1 );
            }
        });
    }
    private void Save (){
        final String mName = edtName.getText().toString().trim();
        final String mResName = edtResName.getText().toString().trim();
        final String mResPhone = edtResPhone.getText().toString().trim();
        final String mResAddress = edtResAddress.getText().toString().trim();
        String mPassword = edtPassword.getText().toString();
        if (mName.isEmpty()){
            edtName.setError("Thiếu thông tin");
            edtName.requestFocus();
        }else if (mResName.isEmpty()){
            edtResName.setError("Thiếu thông tin");
            edtResName.requestFocus();
        }else if (mResPhone.isEmpty()){
            edtResPhone.setError("Thiếu thông tin");
            edtResPhone.requestFocus();
        }else if (mResAddress.isEmpty()){
            edtResAddress.setError("Thiếu thông tin");
            edtResAddress.requestFocus();
        }else if (mPassword.isEmpty()){
            edtPassword.setError("Thiếu thông tin");
            edtPassword.requestFocus();
        }else {
            if (cbChange.isChecked()){
                String mNew = edtNew.getText().toString();
                String mNew2 = edtNew2.getText().toString();
                if (!mPassword.equals(getPassword)){
                    edtPassword.setError("Sai mật khẩu");
                    edtPassword.requestFocus();
                }else if (!mNew.equals(mNew2)){
                    edtNew2.setError("Xác nhận mật khẩu không khớp");
                    edtNew2.requestFocus();
                }else {
                    FirebaseUser user = mAuth.getCurrentUser();
                    final String newPassword = edtNew.getText().toString().trim();

                    user.updatePassword(newPassword)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Map<String, Object> nameMap = new HashMap<>();
                                        if (cbChangeRes.isChecked()) {
                                            nameMap.put(Config.NAME, mName);
                                            nameMap.put(Config.RESNAME, mResName);
                                            nameMap.put(Config.RESPHONE, mResPhone);
                                            nameMap.put(Config.RESADDRESS, mResAddress);
                                        } else {
                                            nameMap.put(Config.NAME, mName);
                                        }
                                        mFirestore.collection("Users").document(getEmail).update(nameMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                sessionManager.createSession(getID,mName,getEmail,getResEmail,newPassword,mResName,mResPhone,mResAddress,getPosition,getImage);
                                                Toast.makeText(AccountActivity.this, "Đã cập nhật thông tin", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(AccountActivity.this, MainActivity.class));
                                                finish();
                                            }
                                        });
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AccountActivity.this, R.string.dacoloi, Toast.LENGTH_SHORT).show();
                            mAuth.signInWithEmailAndPassword(getEmail, getPassword);
                        }
                    });
                }
            }else {
                if (!mPassword.equals(getPassword)){
                    edtPassword.setError("Sai mật khẩu");
                    edtPassword.requestFocus();
                }else {
                    Map<String, Object> nameMap = new HashMap<>();
                    if (cbChangeRes.isChecked()) {
                        nameMap.put(Config.NAME, mName);
                        nameMap.put(Config.RESNAME, mResName);
                        nameMap.put(Config.RESPHONE, mResPhone);
                        nameMap.put(Config.RESADDRESS, mResAddress);
                    } else {
                        nameMap.put(Config.NAME, mName);
                    }
                    mFirestore.collection("Users").document(getEmail).update(nameMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            sessionManager.createSession(getID,mName,getEmail,getResEmail,getPassword,mResName,mResPhone,mResAddress,getPosition,getImage);
                            Toast.makeText(AccountActivity.this, "Đã cập nhật thông tin", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AccountActivity.this, MainActivity.class));
                            finish();
                        }
                    });
                }
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            imageUri = data.getData();
            if (imageUri != null) {
                ivPicture.setImageURI(imageUri);
                final StorageReference user_profile = mStorage.child(getEmail+".jpg");
                progressDialog = ProgressDialog.show(AccountActivity.this,
                        "Cập nhật ảnh", "Đang tải ảnh lên ...", true, false);
                user_profile.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            user_profile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String download_url = uri.toString();
                                    Map<String ,Object> userMap = new HashMap<>();
                                    userMap.put("image", download_url);
                                    mFirestore.collection("Users").document(getEmail).update(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressDialog.dismiss();
                                            Toast.makeText(AccountActivity.this, "Cập nhật ảnh thành công", Toast.LENGTH_SHORT).show();
                                            sessionManager.createSession(getID, getName, getEmail, getResEmail, getPassword, getResName, getResPhone, getResAddress, getPosition, download_url);
                                        }
                                    });

                                }
                            });

                        }else {
                            Toast.makeText(AccountActivity.this, R.string.dacoloi, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

}
