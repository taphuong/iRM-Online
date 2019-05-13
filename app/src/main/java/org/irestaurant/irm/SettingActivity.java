package org.irestaurant.irm;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import org.irestaurant.irm.Database.Config;
import org.irestaurant.irm.Database.FingerprintHandler;
import org.irestaurant.irm.Database.SessionManager;

import java.util.HashMap;

public class SettingActivity extends Activity {
    ImageButton btnFinger;
    SessionManager sessionManager;
    String getEmail;
    ImageButton btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Config.CHECKACTIVITY = "SettingActivity";

        sessionManager = new SessionManager(this);
        sessionManager.checkLoggin();
        HashMap<String, String> user = sessionManager.getUserDetail();
        getEmail = user.get(sessionManager.EMAIL);

        if (findViewById(R.id.fragment_container)!=null){
            if (savedInstanceState!=null)
                return;
            getFragmentManager().beginTransaction().add(R.id.fragment_container, new FragmentSetting()).commit();

        }

        btnFinger = findViewById(R.id.btn_regfinger);
        btnHome = findViewById(R.id.btn_home);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish();}
        });
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
            Intent intent = new Intent(SettingActivity.this, FingerActivity.class);
            startActivityForResult(intent,1);
    }
}
