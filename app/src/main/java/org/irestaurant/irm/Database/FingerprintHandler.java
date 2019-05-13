package org.irestaurant.irm.Database;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.irestaurant.irm.FingerActivity;
import org.irestaurant.irm.LoginActivity;
import org.irestaurant.irm.MainActivity;
import org.irestaurant.irm.R;
import org.irestaurant.irm.SettingActivity;

import java.util.HashMap;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {
    SessionManager sessionManager;
    private Context context;
    SessionFinger sessionFinger;
    String getEmail, getPassword, getFingerEmail, getFingerPassword;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ProgressDialog progressDialog;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    public FingerprintHandler(Context context) {
        this.context = context;
    }

    public void startAuthentication(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED)
            return;
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        sessionManager = new SessionManager(context);
//        sessionManager.checkLoggin();
        HashMap<String, String> user = sessionManager.getUserDetail();
        getEmail = user.get(sessionManager.EMAIL);
        getPassword = user.get(sessionManager.PASSWORD);

        sessionFinger = new SessionFinger(context);
//        sessionFinger.checkFinger();
        HashMap<String, String> finger = sessionFinger.getFinger();
        getFingerEmail = finger.get(sessionFinger.EMAIL);
        getFingerPassword = finger.get(sessionFinger.PASSWORD);


        if (sessionFinger.isFinger() && Config.CHECKACTIVITY.equals("LoginActivity")){
            loginFirebase(getFingerEmail, getFingerPassword);
        }else if (Config.CHECKACTIVITY.equals("SettingActivity")){
            ((Activity)context).finish();
            sessionFinger.creatFinger(getEmail, getPassword);
            Toast.makeText(context, "Đăng ký vân tay thành công", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        Toast.makeText(context, "Xác nhận vân tay lỗi, Vui lòng thử lại", Toast.LENGTH_SHORT).show();
    }
    private void loginFirebase(final String Email, final String Password){
        progressDialog = ProgressDialog.show(context,
                "Đang đăng nhập", "Vui lòng đợi ...", true, false);
//        final String Email = edtPhone.getText().toString().trim();
//        final String Password = edtPassword.getText().toString().trim();
        mAuth.signInWithEmailAndPassword(getFingerEmail, getFingerPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            ((Activity)context).finish();
                            final String uID = mAuth.getCurrentUser().getUid();
                            mFirestore.collection("Users").document(getFingerEmail).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                                    sessionManager.createSession(uID,mName,getFingerEmail,ResEmail, getFingerPassword, mResname,mResphone,mResaddress,mPosition,mImage);
                                    Toast.makeText(context, "Xin chào "+mName, Toast.LENGTH_SHORT).show();
                                    context.startActivity(new Intent(context, MainActivity.class));

                                }
                            });
                        }else {
                            Toast.makeText(context, R.string.dacoloi, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                    }
                });
    }
    private void login (){
        mAuth.signInWithEmailAndPassword(getFingerEmail, getFingerPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(context, "Đã đăng nhập", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
