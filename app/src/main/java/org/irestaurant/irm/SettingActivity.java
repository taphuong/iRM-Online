package org.irestaurant.irm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.irestaurant.irm.Database.DatabaseFinger;
import org.irestaurant.irm.Database.FingerprintHandler;
import org.irestaurant.irm.Database.SessionManager;

import java.util.HashMap;

public class SettingActivity extends AppCompatActivity {
    ImageButton btnFinger;
    DatabaseFinger databaseFinger;
    SessionManager sessionManager;
    String getPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sessionManager = new SessionManager(this);
        sessionManager.checkLoggin();
        HashMap<String, String> user = sessionManager.getUserDetail();
        getPhone = user.get(sessionManager.PHONE);

        btnFinger = findViewById(R.id.btn_regfinger);
        databaseFinger = new DatabaseFinger(this);
        btnFinger.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                checkDB();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkDB(){
//        Boolean chkphone = databaseFinger.chkphone();
//        if (chkphone == true) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setMessage("Đã có dữ liệu vân tay. Bạn có muốn cài đặt lại");
//            builder.setCancelable(false);
//            builder.setPositiveButton("Không", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    dialogInterface.dismiss();
//                }
//            });
//            builder.setNegativeButton("Cài đặt", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    databaseFinger.deleteFinger();
//                    dialogInterface.dismiss();
//                    Intent intent = new Intent(SettingActivity.this, FingerActivity.class);
//                    startActivityForResult(intent,1);
//                }
//            });
//            AlertDialog alertDialog = builder.create();
//            alertDialog.show();
//        }else {
            Intent intent = new Intent(SettingActivity.this, FingerActivity.class);
            startActivityForResult(intent,1);
        FingerprintHandler.fgstt=0;
//        }
    }
}
