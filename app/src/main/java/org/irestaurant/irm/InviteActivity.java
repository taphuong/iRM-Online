package org.irestaurant.irm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.WriterException;

import org.irestaurant.irm.Database.Config;
import org.irestaurant.irm.Database.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;
import de.hdodenhof.circleimageview.CircleImageView;

public class InviteActivity extends Activity {
    EditText edtEmail;
    Button btnConfirm,btnInput, btnQr, btnClose;
    RelativeLayout layoutInput, layoutQr;
    TextView tvName;
    CircleImageView ivPeople;
    ImageView ivQr;
    String getResEmail, getResName, peopleName, peopleEmail, peopleImage, peopleToken = "", peoplePosition;
    SessionManager sessionManager;
    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

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
        tvName      = findViewById(R.id.tv_name);
        ivPeople    = findViewById(R.id.iv_people);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        Anhxa();
        sessionManager =  new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        getResEmail = user.get(sessionManager.RESEMAIL);
        getResName = user.get(sessionManager.RESNAME);
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
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()) {
                    edtEmail.setError("Không phải địa chỉ Email");
                    edtEmail.requestFocus();
                } else if (tvName.getText().toString().isEmpty()){
                    edtEmail.setError("Sai địa chỉ Email");
                    edtEmail.requestFocus();
                }else {
                    Map<String, Object>inviteMap = new HashMap<>();
                    inviteMap.put(Config.POSITION, "invite");
                    inviteMap.put(Config.NAME, peopleName);
                    inviteMap.put(Config.EMAIL, peopleEmail);
                    inviteMap.put(Config.IMAGE, peopleImage);
                    inviteMap.put(Config.TOKENID, "");
                    mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.PEOPLE).document(peopleEmail).set(inviteMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            String date = new SimpleDateFormat("dd/MM/yyyyy", Locale.getDefault()).format(new Date());
                            Map<String, Object> inviteMap = new HashMap<>();
                            inviteMap.put(Config.RESEMAIL, getResEmail);
                            inviteMap.put(Config.RESNAME, getResName);
                            inviteMap.put(Config.DATE, date);
                            mFirestore.collection(Config.USERS).document(peopleEmail).collection(Config.INVITE).document(getResEmail).set(inviteMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(InviteActivity.this, "Đã gửi lời mời đến "+peopleName, Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(InviteActivity.this, R.string.dacoloi, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final String email = edtEmail.getText().toString();
                mFirestore.collection(Config.USERS).document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            peoplePosition = task.getResult().getString(Config.POSITION);
                            if (peoplePosition!=null && (peoplePosition.equals("none") || peoplePosition.equals("invite"))){
                                peopleName = task.getResult().getString(Config.NAME);
                                peopleImage = task.getResult().getString(Config.IMAGE);
                                peopleEmail = task.getResult().getId();
                                tvName.setText(peopleName);
                                RequestOptions requestOptions = new RequestOptions();
                                requestOptions.placeholder(R.drawable.profile);
                                Glide.with(getApplicationContext()).setDefaultRequestOptions(requestOptions).load(peopleImage).into(ivPeople);
                            }else if (peoplePosition!=null){
                                edtEmail.setError("Người này đang làm việc tại một cửa hàng");
                            }else {
                                peopleName = "";
                                peopleImage = "";
                                peopleEmail = "";
                                tvName.setText("");
                                ivPeople.setImageResource(R.drawable.profile);
                            }
                        }else {
                            peopleName = "";
                            peopleImage = "";
                            peopleEmail = "";
                            tvName.setText("");
                            ivPeople.setImageResource(R.drawable.profile);
                        }
                    }
                });
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
