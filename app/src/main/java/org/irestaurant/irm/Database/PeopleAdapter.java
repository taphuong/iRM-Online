package org.irestaurant.irm.Database;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.irestaurant.irm.CurrentPeopleFragment;
import org.irestaurant.irm.JoinPeopleFragment;
import org.irestaurant.irm.PeopleActivity;
import org.irestaurant.irm.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder> {
    private Context context;
    private List<People> peopleList;
    SessionManager sessionManager;
    String getPosition, getResEmail, getResName, getResPhone, getResAddress, getEmail, getPassword;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    public PeopleAdapter(Context context, List<People> peopleList) {
        this.context = context;
        this.peopleList = peopleList;
    }

    @NonNull
    @Override
    public PeopleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_people, viewGroup, false);
        return new PeopleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PeopleAdapter.ViewHolder viewHolder, final int i) {
        sessionManager = new SessionManager(context);
        HashMap<String, String> user = sessionManager.getUserDetail();
        getPosition = user.get(sessionManager.POSITION);
        getResEmail = user.get(sessionManager.RESEMAIL);
        getResName  = user.get(sessionManager.RESNAME);
        getResPhone = user.get(sessionManager.RESPHONE);
        getResAddress = user.get(sessionManager.RESADDRESS);
        getEmail    = user.get(sessionManager.EMAIL);
        getPassword = user.get(sessionManager.PASSWORD);
        final String email = peopleList.get(i).peopleId;
        final String name = peopleList.get(i).getName();
        final String image = peopleList.get(i).getImage();
        final String position = peopleList.get(i).getPosition();
        viewHolder.tvName.setText(name);
        viewHolder.tvEmail.setText(email);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile);
        Glide.with(context).setDefaultRequestOptions(requestOptions).load(image).into(viewHolder.ivPeople);
        if (position.equals("admin")){
            viewHolder.ivAdmin.setVisibility(View.VISIBLE);
            viewHolder.tvPosition.setText("Admin");
        } else if (position.equals("cashier")){
            viewHolder.ivCashier.setVisibility(View.VISIBLE);
            viewHolder.tvPosition.setText("Thu ngân");
        }else if (position.equals("join")){
            viewHolder.ivCashier.setVisibility(View.GONE);
            viewHolder.ivAdmin.setVisibility(View.GONE);
            viewHolder.tvPosition.setText("Yêu cầu");
        }else if (position.equals("invite")){
            viewHolder.ivCashier.setVisibility(View.GONE);
            viewHolder.ivAdmin.setVisibility(View.GONE);
            viewHolder.tvPosition.setText("Đã mời");
        }else {
            viewHolder.ivCashier.setVisibility(View.GONE);
            viewHolder.ivAdmin.setVisibility(View.GONE);
        }
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.equals(getEmail)){

                }else if (position.equals("join")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Duyệt đơn của "+name);
                    builder.setPositiveButton("Từ chối", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, int i) {
                            mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.PEOPLE).document(email).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(context, "Đã từ chối đơn của "+name, Toast.LENGTH_SHORT).show();
                                    dialogInterface.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, R.string.dacoloi, Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });
                    builder.setNegativeButton("Đồng ý", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, final int p) {
                            Map<String, Object> acceptMap = new HashMap<>();
                            acceptMap.put(Config.POSITION, "employe");
                            mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.PEOPLE).document(email).update(acceptMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Map<String, Object> updateMap = new HashMap<>();
                                    updateMap.put(Config.POSITION, "employe");
                                    updateMap.put(Config.RESNAME, getResName);
                                    updateMap.put(Config.RESPHONE, getResPhone);
                                    updateMap.put(Config.RESADDRESS, getResAddress);
                                    updateMap.put(Config.RESEMAIL, getResEmail);
                                    mFirestore.collection(Config.USERS).document(email).update(updateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            JoinPeopleFragment.p = String.valueOf(i) ;
                                            Toast.makeText(context, "Đã duyệt đơn của "+name, Toast.LENGTH_SHORT).show();
                                            dialogInterface.dismiss();
                                        }
                                    });
                                }
                            });

                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }else if (position.equals("invite")){
                    showMenuInvite(v, name, email);
                }
            }
        });
        viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (getPosition.equals("admin")){
                    showMenuAdmin(v,name,email,image);
                }
                return false;
            }
        });
    }

    private void showMenuInvite(View v, final String name, final String email) {
        final android.support.v7.widget.PopupMenu popupMenu = new android.support.v7.widget.PopupMenu(context,v);
        popupMenu.getMenuInflater().inflate(R.menu.invite_menu,popupMenu.getMenu());
        popupMenu.setGravity(Gravity.RIGHT);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.popup_cancel:
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Hủy lời mời đến "+name);
                        builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.setNegativeButton("Đồng ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, final int p) {

                                mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.PEOPLE).document(email).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mFirestore.collection(Config.USERS).document(email).collection(Config.INVITE).document(getResEmail).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(context, "Đã hủy lời mời đến "+name, Toast.LENGTH_SHORT).show();
                                                dialogInterface.dismiss();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, R.string.dacoloi, Toast.LENGTH_SHORT).show();
                                    }
                                });

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
    }

    private void showMenuAdmin(View v, final String name, final String email, final String image) {
        final android.support.v7.widget.PopupMenu popupMenu = new android.support.v7.widget.PopupMenu(context,v);
        popupMenu.getMenuInflater().inflate(R.menu.person_popup,popupMenu.getMenu());
        popupMenu.setGravity(Gravity.RIGHT);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.popup_edit:
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dialog_people);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        CircleImageView ivPeople    = dialog.findViewById(R.id.iv_people);
                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.placeholder(R.drawable.profile);
                        Glide.with(context).setDefaultRequestOptions(requestOptions).load(image).into(ivPeople);

                        TextView tvName      = dialog.findViewById(R.id.tv_name);
                        tvName.setText(name);
                        TextView tvEmail     = dialog.findViewById(R.id.tv_email);
                        tvEmail.setText(email);
                        final Spinner spnPosition = dialog.findViewById(R.id.spn_position);

                        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);
                        spnPosition.setDropDownVerticalOffset(100);
                        List<String> list = new ArrayList<>();
                        list.add("Nhân viên");
                        list.add("Thu ngân");
                        list.add("Admin");
                        ArrayAdapter<String> adaptersl = new ArrayAdapter<String>(context,R.layout.spinner_item,list);
                        spnPosition.setAdapter(adaptersl);

                        btnConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String pos = "none";
                                final String possl = spnPosition.getSelectedItem().toString().trim();
                                switch (possl){
                                    case "Admin":
                                        pos = "admin";
                                        break;
                                    case "Thu ngân":
                                        pos = "cashier";
                                        break;
                                    case "Nhân viên":
                                        pos = "employe";
                                        break;
                                }
                                final String finalPos = pos;
                                mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.PEOPLE).document(email).update(Config.POSITION, finalPos).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mFirestore.collection(Config.USERS).document(email).update(Config.POSITION, finalPos).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                dialog.dismiss();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, R.string.dacoloi, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

                        dialog.show();
                        break;
                    case R.id.popup_delete:
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(Html.fromHtml("Bạn có muốn đuổi việc "+"<font color='red'>"+name+"</font>"+" không?"));
                        builder.setCancelable(false);
                        builder.setPositiveButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.setNegativeButton("Đuổi việc", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final Dialog dialog = new Dialog(context);
                                dialog.setContentView(R.layout.dialog_password);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                final EditText edtPass    = dialog.findViewById(R.id.edt_password);
                                edtPass.requestFocus();
                                Button btnConfirm   = dialog.findViewById(R.id.btn_confirm);
                                Button btnClose     = dialog.findViewById(R.id.btn_close);
                                btnConfirm.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (!edtPass.getText().toString().equals(getPassword)){
                                            edtPass.setError("Sai mật khẩu");
                                        }else {
                                            mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.PEOPLE).document(email).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    String none = "none";
                                                    Map<String, Object> deleteMap = new HashMap<>();
                                                    deleteMap.put(Config.POSITION, none);
                                                    deleteMap.put(Config.RESEMAIL, none);
                                                    deleteMap.put(Config.RESNAME, none);
                                                    deleteMap.put(Config.RESPHONE, none);
                                                    deleteMap.put(Config.RESADDRESS, none);
                                                    mFirestore.collection(Config.USERS).document(email).update(deleteMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(context, "Đã đuổi việc "+name, Toast.LENGTH_SHORT).show();
                                                            dialog.dismiss();
                                                        }
                                                    });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(context, R.string.dacoloi, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                });
                                btnClose.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });

                                dialogInterface.dismiss();
                                dialog.show();
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
    }


    @Override
    public int getItemCount() {
        return peopleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView tvName, tvEmail, tvPosition;
        private CircleImageView ivPeople;
        private ImageView ivAdmin, ivCashier;

        public ViewHolder( View itemView) {
            super(itemView);
            mView = itemView;
            tvName = mView.findViewById(R.id.tv_name);
            tvEmail = mView.findViewById(R.id.tv_email);
            tvPosition = mView.findViewById(R.id.tv_position);
            ivPeople = mView.findViewById(R.id.iv_people);
            ivAdmin = mView.findViewById(R.id.iv_admin);
            ivCashier = mView.findViewById(R.id.iv_cashier);
        }
    }
}
