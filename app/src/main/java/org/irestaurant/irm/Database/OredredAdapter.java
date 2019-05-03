package org.irestaurant.irm.Database;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.irestaurant.irm.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class OredredAdapter extends RecyclerView.Adapter<OredredAdapter.ViewHolder> {
    private Context context;
    private List<Ordered> orderedList;
    long tongtien;
    private long price;
    String getResEmail;
    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    CollectionReference numberRef;
    SessionManager sessionManager;

    public OredredAdapter(Context context,List<Ordered> orderedList) {
        this.context = context;
        this.orderedList = orderedList;
    }

    @Override
    public OredredAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ordered, parent, false);
        return new OredredAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return orderedList.size();
    }

    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        sessionManager = new SessionManager(context);
        HashMap<String, String> user = sessionManager.getUserDetail();
        getResEmail = user.get(sessionManager.RESEMAIL);

        String orderId = orderedList.get(i).orderedId;
        String foodname = orderedList.get(i).getFoodname();
        String amount = orderedList.get(i).getAmount();
        viewHolder.tvFoodname.setText(foodname);
        viewHolder.tvAmount.setText(amount);
        viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                android.support.v7.widget.PopupMenu popupMenu = new android.support.v7.widget.PopupMenu(context,v);
                popupMenu.getMenuInflater().inflate(R.menu.ordered_popup,popupMenu.getMenu());
                popupMenu.setGravity(Gravity.RIGHT);
                popupMenu.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.popup_edit:
                                final Dialog dialog = new Dialog(context);
                                dialog.setContentView(R.layout.dialog_addnumber);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.setCanceledOnTouchOutside(false);
                                final Button btnMinus = (Button) dialog.findViewById(R.id.btn_minus);
                                final Button btnAdd = (Button) dialog.findViewById(R.id.btn_add);
                                Button btnClose     = (Button) dialog.findViewById(R.id.btn_close);
                                Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);
                                btnConfirm.setText("Lưu");
                                TextView tvFood = dialog.findViewById(R.id.themban);
                                tvFood.setText(orderedList.get(i).getFoodname());
                                final EditText edtAmount = (EditText) dialog.findViewById(R.id.edt_amount);
                                edtAmount.setText(orderedList.get(i).getAmount());
                                if (Integer.valueOf(orderedList.get(i).getAmount())>1){
                                    btnMinus.setVisibility(View.VISIBLE);
                                }else {btnMinus.setVisibility(View.INVISIBLE);}

                                if (Integer.valueOf(orderedList.get(i).getAmount())>999){
                                    btnAdd.setVisibility(View.INVISIBLE);
                                }else {btnAdd.setVisibility(View.VISIBLE);}
                                price = Integer.valueOf(orderedList.get(i).getPrice());
                                dialog.show();
                                btnClose.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                                btnAdd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        btnMinus.setVisibility(View.VISIBLE);
                                        if (edtAmount.getText().toString().equals("")){
                                            edtAmount.setText("1");
                                        } else if (Integer.valueOf(edtAmount.getText().toString())==999){
                                            edtAmount.setText("1000");
                                            btnAdd.setVisibility(View.INVISIBLE);
                                        } else {
                                            edtAmount.setText(String.valueOf(Integer.valueOf(edtAmount.getText().toString())+1));
                                        }
                                    }
                                });
                                btnMinus.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        btnAdd.setVisibility(View.VISIBLE);
                                        String stramount = edtAmount.getText().toString();
                                        if (stramount.isEmpty() || stramount.equals("2")){
                                            edtAmount.setText("1");
                                            btnMinus.setVisibility(View.INVISIBLE);
                                        }  else {
                                            long amount = Integer.valueOf(edtAmount.getText().toString());
                                            edtAmount.setText(String.valueOf(amount-1));
                                        }

                                    }
                                });
                                edtAmount.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        String amout = edtAmount.getText().toString();
                                        if (amout.isEmpty() || amout.equals("1") || amout.equals("0")) {
                                            btnMinus.setVisibility(View.INVISIBLE);
                                            btnAdd.setVisibility(View.VISIBLE);
                                        } else if (Integer.valueOf(amout) > 1000) {
                                            edtAmount.setText("1000");
                                            edtAmount.setSelection(edtAmount.getText().length());
                                            btnAdd.setVisibility(View.INVISIBLE);
                                            btnMinus.setVisibility(View.VISIBLE);
                                        } else {
                                            btnMinus.setVisibility(View.VISIBLE);
                                            btnAdd.setVisibility(View.VISIBLE);
                                        }
                                    }
                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                });
                                btnConfirm.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (edtAmount.getText().toString().equals("") || edtAmount.getText().toString().equals("0")){
                                            edtAmount.setError("Nhập số phần");
                                            edtAmount.requestFocus();
                                        }else {
                                            String id = String.valueOf(orderedList.get(i).orderedId);
                                            String foodname = orderedList.get(i).getFoodname();
                                            String newamount = edtAmount.getText().toString();
                                            String date = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());
                                            String total = String.valueOf(price*Integer.valueOf(newamount));
//                                            updateOrdered(id,foodname,newamount, date, String.valueOf(price), total, dialog);

                                        }
                                    }
                                });
                                break;
                            case R.id.popup_delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("Bạn muốn xóa "+orderedList.get(i).getAmount()+" phần "+orderedList.get(i).getFoodname()+" ?");
                                builder.setCancelable(false);
                                builder.setPositiveButton("Không", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                builder.setNegativeButton("Xóa", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });

                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();

                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
                return false;
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView tvFoodname, tvAmount;


        public ViewHolder( View itemView) {
            super(itemView);
            mView = itemView;
            tvFoodname = mView.findViewById(R.id.tv_foodname);
            tvAmount = mView.findViewById(R.id.tv_amount);
        }
    }

}
