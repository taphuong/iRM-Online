package org.irestaurant.irm.Database;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import org.irestaurant.irm.FingerActivity;
import org.irestaurant.irm.MainActivity;
import org.irestaurant.irm.R;
import org.irestaurant.irm.SettingActivity;

import java.util.HashMap;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {
    SessionManager sessionManager;
    private Context context;
    public final static int fgstt = 0;

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
        sessionManager.checkLoggin();
        HashMap<String, String> user = sessionManager.getUserDetail();
        String getPhone = user.get(sessionManager.PHONE);


        if (fgstt==1){
            DatabaseFinger databaseFinger = new DatabaseFinger(context);
            Toast.makeText(context, "Đang đăng nhập", Toast.LENGTH_SHORT).show();
            Finger finger = databaseFinger.getPhone();
            if (finger == null){

            }else {
                String Phone = finger.getPhone();
                login(Phone);
            }
        }else {
            DatabaseFinger db = new DatabaseFinger(context);
            Finger finger = new Finger();
            finger.setPhone(getPhone);
            if (db.creat(finger)){
                Toast.makeText(context, "Đăng ký thành công", Toast.LENGTH_SHORT).show();

            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.error);
                builder.setMessage(R.string.cannot_create);
                builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
            Toast.makeText(context, "Đăng ký vân tay", Toast.LENGTH_SHORT).show();
        }

//        DatabaseFinger databaseFinger = new DatabaseFinger(context);
//        Boolean chkphone = databaseFinger.chkphone();
//        if (chkphone == true) {
//            Toast.makeText(context, "Đang đăng nhập", Toast.LENGTH_SHORT).show();
//            Finger finger = databaseFinger.getPhone();
//            if (finger == null){
//
//            }else {
//                String Phone = finger.getPhone();
//                login(Phone);
//            }
//        }else {
//            DatabaseFinger db = new DatabaseFinger(context);
//            Finger finger = new Finger();
//            finger.setPhone(getPhone);
//            if (db.creat(finger)){
//                Toast.makeText(context, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
//
//            }else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setTitle(R.string.error);
//                builder.setMessage(R.string.cannot_create);
//                builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//            }
//            Toast.makeText(context, "Đăng ký vân tay", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        Toast.makeText(context, "Xác nhận vân tay lỗi, Vui lòng thử lại", Toast.LENGTH_SHORT).show();
    }
    private void login(String Phone){
        DatabaseHelper db = new DatabaseHelper(context);
        User user = db.fingerLogin(Phone);
        sessionManager.createSession(user.getId(),user.getName(),user.getPhone(), user.getPassword(),user.getResname(),user.getResphone(),user.getResaddress());
        Toast.makeText(context, "Xin chào "+user.getName(), Toast.LENGTH_SHORT).show();
        Intent myIntent = new Intent(context, MainActivity.class);
        context.startActivity(myIntent);
    }
}
