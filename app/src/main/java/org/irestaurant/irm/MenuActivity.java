package org.irestaurant.irm;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.irestaurant.irm.Database.SessionManager;

import java.text.DecimalFormat;
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
        btnAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MenuActivity.this);
                dialog.setContentView(R.layout.dialog_addfood);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCanceledOnTouchOutside(false);
                Button btnClose     = (Button) dialog.findViewById(R.id.btn_close);
                Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);
                final EditText edtFoodname = (EditText) dialog.findViewById(R.id.edt_foodname);
                final EditText edtFoodprice = (EditText) dialog.findViewById(R.id.edt_foodprice);
                dialog.show();
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                edtFoodprice.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String pricce = edtFoodprice.getText().toString();
                        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
                        edtFoodprice.setText(decimalFormat.format(Integer.valueOf(pricce)).toString());
                    }
                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = edtFoodname.getText().toString();
                        String price = edtFoodprice.getText().toString().replaceAll(",","");;
//                        if ()

                    }
                });
            }
        });
    }
}
