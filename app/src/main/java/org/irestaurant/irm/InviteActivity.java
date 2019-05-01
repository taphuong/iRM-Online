package org.irestaurant.irm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Environment;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.zxing.WriterException;

import org.irestaurant.irm.Database.SessionManager;

import java.util.HashMap;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

public class InviteActivity extends Activity {
    EditText edtEmail;
    Button btnConfirm,btnInput, btnQr, btnClose;
    RelativeLayout layoutInput, layoutQr;
    ImageView ivQr;
    String getResEmail;
    SessionManager sessionManager;

    String savePath = Environment.getExternalStorageDirectory().getPath() + "/Pictures/QrCode/";
    Bitmap bitmap;
    QRGEncoder qrgEncoder;

    private void Anhxa(){
        btnConfirm  = findViewById(R.id.btn_confirm);
        btnInput    = findViewById(R.id.btn_input);
        btnQr       = findViewById(R.id.btn_qr);
        btnClose    = findViewById(R.id.btn_close);
        edtEmail    = findViewById(R.id.edt_email);
        layoutInput = findViewById(R.id.layout_input);
        layoutQr    = findViewById(R.id.layout_qr);
        ivQr        = findViewById(R.id.iv_qr);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        Anhxa();
        sessionManager =  new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        getResEmail = user.get(sessionManager.RESEMAIL);
        loadQr();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutInput.setVisibility(View.GONE);
                layoutQr.setVisibility(View.VISIBLE);
                btnQr.setEnabled(false);
                btnInput.setEnabled(true);
                btnInput.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
                btnQr.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
            }
        });
        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutInput.setVisibility(View.VISIBLE);
                layoutQr.setVisibility(View.GONE);
                btnQr.setEnabled(true);
                btnInput.setEnabled(false);
                btnInput.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
                btnQr.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);

            }
        });

    }
    private void loadQr(){
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3 / 4;

        qrgEncoder = new QRGEncoder(
                getResEmail, null,
                QRGContents.Type.TEXT,
                smallerDimension);
        try {
            bitmap = qrgEncoder.encodeAsBitmap();
            ivQr.setImageBitmap(bitmap);
        } catch (WriterException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Lỗi tạo mã QR, bạn có muốn thử lại ?");
            builder.setCancelable(false);
            builder.setPositiveButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("Thử lại", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    loadQr();
                    dialogInterface.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
}
