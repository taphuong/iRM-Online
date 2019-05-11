package org.irestaurant.irm.Database;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kekstudio.dachshundtablayout.DachshundTabLayout;

import org.irestaurant.irm.MainActivity;
import org.irestaurant.irm.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InviteAdapter extends RecyclerView.Adapter<InviteAdapter.ViewHolder> {
    private Context context;
    private List<Invite> inviteList;
    SessionManager sessionManager;
    String getID, getName, getEmail, getPassword, getImage, getToken, resPhone, resAddress;
    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    List<Invite> listInvite;

    public InviteAdapter(Context context, List<Invite> inviteList) {
        this.context = context;
        this.inviteList = inviteList;
    }

    @NonNull
    @Override
    public InviteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_invite, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int Mpostition) {
        sessionManager = new SessionManager(context);
        Map<String, String> user = sessionManager.getUserDetail();
        getID    = user.get(sessionManager.ID);
        getName  = user.get(sessionManager.NAME);
        getEmail = user.get(sessionManager.EMAIL);
        getPassword = user.get(sessionManager.PASSWORD);
        getImage = user.get(sessionManager.IMAGE);
        getToken = FirebaseInstanceId.getInstance().getToken();
        final String resEmail = inviteList.get(Mpostition).inviteId;
        final String resName  = inviteList.get(Mpostition).getResname();
        final String Date     = inviteList.get(Mpostition).getDate();
        viewHolder.tvResEmail.setText(resEmail);
        viewHolder.tvResName.setText(resName);
        viewHolder.tvDate.setText(Date);
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Lời mời từ "+resName);
                builder.setPositiveButton("Từ chối", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        mFirestore.collection(Config.RESTAURANTS).document(resEmail).collection(Config.PEOPLE).document(getEmail).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mFirestore.collection(Config.USERS).document(getEmail).collection(Config.INVITE).document(resEmail).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "Đã từ chối lời mời của của "+resName, Toast.LENGTH_SHORT).show();
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
                builder.setNegativeButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, final int p) {
                        Map<String, Object> acceptMap = new HashMap<>();
                        acceptMap.put(Config.POSITION, "employe");
                        acceptMap.put(Config.TOKENID, getToken);
                        Toast.makeText(context, resEmail, Toast.LENGTH_SHORT).show();
                        mFirestore.collection(Config.RESTAURANTS).document(resEmail).collection(Config.PEOPLE).document(getEmail).update(acceptMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                mFirestore.collection(Config.RESTAURANTS).document(resEmail).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        resPhone = documentSnapshot.getString(Config.RESPHONE);
                                        resAddress = documentSnapshot.getString(Config.RESADDRESS);
                                        Map<String, Object> updateMap = new HashMap<>();
                                        updateMap.put(Config.POSITION, "employe");
                                        updateMap.put(Config.RESNAME, resName);
                                        updateMap.put(Config.RESPHONE, resPhone);
                                        updateMap.put(Config.RESADDRESS, resAddress);
                                        updateMap.put(Config.RESEMAIL, resEmail);
                                        updateMap.put(Config.TOKENID, getToken);

                                        mFirestore.collection(Config.USERS).document(getEmail).update(updateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                getInvite(resEmail);

                                                sessionManager.createSession(getID,getName,getEmail,resEmail,getPassword,resName,resPhone,resAddress,"employe",getImage);
                                                Toast.makeText(context, "Đã đồng ý lời mời từ "+resName, Toast.LENGTH_SHORT).show();
                                                dialogInterface.dismiss();
                                                Intent intent = new Intent(context, MainActivity.class);
                                                context.startActivity(intent);
                                            }
                                        });
                                    }
                                });
                            }
                        });

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return inviteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView tvResName, tvResEmail, tvDate;

        public ViewHolder( View itemView) {
            super(itemView);
            mView = itemView;
            tvResName = mView.findViewById(R.id.tv_resname);
            tvResEmail = mView.findViewById(R.id.tv_resemail);
            tvDate = mView.findViewById(R.id.tv_date);
        }
    }
    private void getInvite(final String resEmail){
        mFirestore.collection(Config.USERS).document(getEmail).collection(Config.INVITE).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                listInvite = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    String ID = documentSnapshot.getId();
                    Toast.makeText(context, ID, Toast.LENGTH_SHORT).show();
                    Invite invite = documentSnapshot.toObject(Invite.class).withId(ID);
                    listInvite.add(invite);
                }
                for (int a=0; a < listInvite.size(); a++){
                    String ID = listInvite.get(a).inviteId;
                    mFirestore.collection(Config.USERS).document(getEmail).collection(Config.INVITE).document(ID).delete();
                }
                for (int a=0; a < listInvite.size(); a++){
                    String ID = listInvite.get(a).inviteId;
                    String eMail = listInvite.get(a).getResemail();
                    if (!resEmail.equals(eMail)) {
                        mFirestore.collection(Config.RESTAURANTS).document(eMail).collection(Config.PEOPLE).document(getEmail).delete();
                    }
                }
            }
        });
    }
}
