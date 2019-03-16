package org.irestaurant.irm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.irestaurant.irm.Database.SessionManager;

import java.util.HashMap;

public class MenuActivity extends AppCompatActivity {
    SessionManager sessionManager;
    String getResName;
    Button btnHome, btnAddFood;

    private void Anhxa(){
        btnHome     = findViewById(R.id.btn_home);
        btnAddFood  = findViewById(R.id.btn_addfood);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Anhxa();
        sessionManager = new SessionManager(this);
        sessionManager.checkLoggin();
        HashMap<String, String> user = sessionManager.getUserDetail();
        getResName = user.get(sessionManager.RESNAME);
        setTitle(getResName);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
