package org.irestaurant.irm;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.irestaurant.irm.Database.Config;
import org.irestaurant.irm.Database.Invite;
import org.irestaurant.irm.Database.InviteAdapter;
import org.irestaurant.irm.Database.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class InviteListActivity extends Activity {
    RecyclerView lvInvite;
    List<Invite> inviteList;
    InviteAdapter inviteAdapter;
    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    SessionManager sessionManager;
    String getEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_list);
        sessionManager = new SessionManager(this);
        Map<String, String> user = sessionManager.getUserDetail();
        getEmail = user.get(sessionManager.EMAIL);
        lvInvite = findViewById(R.id.lv_invite);
        inviteList = new ArrayList<>();
        inviteAdapter = new InviteAdapter(this, inviteList);
        lvInvite.setHasFixedSize(true);
        lvInvite.setLayoutManager(new LinearLayoutManager(this));
        lvInvite.setAdapter(inviteAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        inviteList.clear();
        mFirestore.collection(Config.USERS).document(getEmail).collection(Config.INVITE).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null){
//                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }else {
                    for (final DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                        String inviteId = doc.getDocument().getId();
                        final Invite invite = doc.getDocument().toObject(Invite.class).withId(inviteId);
                            switch (doc.getType()) {
                                case ADDED:
                                    inviteList.add(invite);
                                    inviteAdapter.notifyDataSetChanged();
                                    break;
                                case REMOVED:

                                    break;
                                case MODIFIED:

                                    break;

                        }
                    }
                }
            }
        });
    }
}
