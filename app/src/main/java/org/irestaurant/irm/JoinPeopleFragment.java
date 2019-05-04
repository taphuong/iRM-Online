package org.irestaurant.irm;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

public class JoinPeopleFragment extends Fragment {
    RecyclerView lvJoinPeople;
    public List<People> joinList;
    public PeopleAdapter peopleAdapter;
    public FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    SessionManager sessionManager;
    String getResEmail;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_join_people, container, false);
        sessionManager = new SessionManager(getActivity());
        HashMap<String, String> user = sessionManager.getUserDetail();
        getResEmail = user.get(sessionManager.RESEMAIL);
        lvJoinPeople = view.findViewById(R.id.lv_joinpeople);
        joinList = new ArrayList<>();
        peopleAdapter = new PeopleAdapter(getActivity(), joinList);
        lvJoinPeople.setHasFixedSize(true);
        lvJoinPeople.setLayoutManager(new LinearLayoutManager(getActivity()));
        lvJoinPeople.setAdapter(peopleAdapter);
        return view;
    }

    public void refreshCurrent (List<People> peopleList){
        this.joinList.clear();
        this.joinList.addAll(peopleList);
        peopleAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        joinList.clear();
        mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.PEOPLE).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot documentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null){
//                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }else {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()){
                        String numberId = doc.getDocument().getId();
                        People people = doc.getDocument().toObject(People.class).withId(numberId);
                        String status = doc.getDocument().getString("status");
                        if (status.equals("join")) {
                            switch (doc.getType()) {
                                case ADDED:
                                    joinList.add(people);
                                    peopleAdapter.notifyDataSetChanged();
                                    break;
                                case REMOVED:
                                    mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.PEOPLE).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()){
                                                joinList.clear();
                                                for (QueryDocumentSnapshot doctask : task.getResult()){
                                                    if (doctask.exists()){
                                                        People people1 = doctask.toObject(People.class);
                                                        joinList.add(people1);
                                                        peopleAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                        }
                                    });
                                    break;
                                case MODIFIED:
                                    mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.PEOPLE).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()){
                                                joinList.clear();
                                                for (QueryDocumentSnapshot doctask : task.getResult()){
                                                    if (doctask.exists()){
                                                        People people1 = doctask.toObject(People.class);
                                                        joinList.add(people1);
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
