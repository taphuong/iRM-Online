package org.irestaurant.irm;

import android.content.Intent;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
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

public class CurrentPeopleFragment extends Fragment {
    RecyclerView lvCurrentPeople;
    static public List<People> currentList;
    static public PeopleAdapter peopleAdapter;
    public FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    SessionManager sessionManager;
    String getResEmail;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_people, container, false);
        sessionManager = new SessionManager(getActivity());
        HashMap<String, String> user = sessionManager.getUserDetail();
        getResEmail = user.get(sessionManager.RESEMAIL);
        lvCurrentPeople = view.findViewById(R.id.lv_currentpeople);
        currentList = new ArrayList<>();
        peopleAdapter = new PeopleAdapter(getActivity(), currentList);
        lvCurrentPeople.setHasFixedSize(true);
        lvCurrentPeople.setLayoutManager(new LinearLayoutManager(getActivity()));
        lvCurrentPeople.setAdapter(peopleAdapter);
        return view;
    }
    //    gotoChat
    @Override
    public void onStart() {
        super.onStart();
        currentList.clear();
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
                        if (status != null) {
                            switch (doc.getType()) {
                                case ADDED:
                                    if (!status.equals("join") && !status.equals("invite")) {
                                        if (status.equals("admin")) {
                                            currentList.add(people);
                                            peopleAdapter.notifyDataSetChanged();
                                        } else {
                                            currentList.add(people);
                                            peopleAdapter.notifyDataSetChanged();
                                        }
                                    }
                                    break;
                                case REMOVED:
                                    currentList.clear();
                                    mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.PEOPLE).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()){
                                                currentList.clear();
                                                for (QueryDocumentSnapshot doctask : task.getResult()){
                                                    if (doctask.exists()){
                                                        People people1 = doctask.toObject(People.class);
                                                        currentList.add(people1);
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
                                    loadPeople();
                                    break;
                            }
                        }


                    }
                }
            }
        });
    }
    private void loadPeople(){
        mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.PEOPLE).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                currentList.clear();
                peopleAdapter.notifyDataSetChanged();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    String ID = documentSnapshot.getId();
                    String position = documentSnapshot.getString(Config.POSITION);
                    if (!position.equals("join") && !position.equals("invite")){
                        People people = documentSnapshot.toObject(People.class).withId(ID);
                        currentList.add(people);
                        peopleAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
}
