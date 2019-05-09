package org.irestaurant.irm;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.irestaurant.irm.Database.Config;
import org.irestaurant.irm.Database.People;
import org.irestaurant.irm.Database.PeopleAdapter;
import org.irestaurant.irm.Database.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PeopleSingleActivity extends Activity {
    Button btnHome;
    TextView tvResName;
    RecyclerView lvPeople;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    SessionManager sessionManager;
    String getResName, getResEmail, getEmail;
    PeopleAdapter peopleAdapter;
    List<People> peopleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_single);
        btnHome  = findViewById(R.id.btn_home);
        tvResName = findViewById(R.id.tv_resname);
        lvPeople = findViewById(R.id.lv_people);
        sessionManager = new SessionManager(this);
        Map<String, String> user = sessionManager.getUserDetail();
        getEmail = user.get(sessionManager.EMAIL);
        getResEmail = user.get(sessionManager.RESEMAIL);
        getResName = user.get(sessionManager.RESNAME);
        tvResName.setText(getResName);
        peopleList = new ArrayList<>();
        peopleAdapter = new PeopleAdapter(this, peopleList);
        lvPeople.setHasFixedSize(true);
        lvPeople.setLayoutManager(new LinearLayoutManager(this));
        lvPeople.setAdapter(peopleAdapter);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        peopleList.clear();
        mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.PEOPLE).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot documentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null){
//                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }else {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()){
                        String numberId = doc.getDocument().getId();
                        final People people = doc.getDocument().toObject(People.class).withId(numberId);
                        String status = doc.getDocument().getString(Config.POSITION);
                        if (status != null && !status.equals("join")) {
                            switch (doc.getType()) {
                                case ADDED:
                                    if (status.equals("admin")){
                                        peopleList.add(people);
                                    }else {
                                        peopleList.add(people);
                                    }
                                    peopleAdapter.notifyDataSetChanged();
                                    break;
                                case REMOVED:
                                    peopleList.clear();
                                    mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.PEOPLE).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()){
                                                peopleList.clear();
                                                for (QueryDocumentSnapshot doctask : task.getResult()){
                                                    if (doctask.exists()){
                                                        People people1 = doctask.toObject(People.class);
                                                        peopleList.add(people1);
                                                        peopleAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                        }
                                    });
//                                    currentList.remove(numberId);
//                                    peopleAdapter.notifyDataSetChanged();
                                    break;
                                case MODIFIED:
                                    peopleList.clear();
                                    mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.PEOPLE).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()){
                                                peopleList.clear();
                                                for (QueryDocumentSnapshot doctask : task.getResult()){
                                                    if (doctask.exists()){
                                                        People people1 = doctask.toObject(People.class);
                                                        peopleList.add(people1);
                                                        peopleAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                        }
                                    });
                                    break;
                            }
                        }


                    }
                }
            }
        });
    }
}
