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

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
    public void refreshCurrent (){
        PeopleActivity peopleActivity = new PeopleActivity();
        peopleActivity.refreshPeople();
    }
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
                        People people = doc.getDocument().toObject(People.class).withId(numberId);
                        String status = doc.getDocument().getString("status");
                        if (!status.equals("join")) {
                            switch (doc.getType()) {
                                case ADDED:
                                    if (status.equals("admin")){
                                        currentList.add(people);
                                        peopleAdapter.notifyDataSetChanged();
                                    }else {
                                        currentList.add(people);
                                        peopleAdapter.notifyDataSetChanged();
                                    }
                                    break;
                                case REMOVED:
                                    currentList.remove(numberId);
                                    peopleAdapter.notifyDataSetChanged();
                                    break;
                                case MODIFIED:
//                                    currentList.remove(people);
//                                    peopleAdapter.notifyDataSetChanged();
//                                    currentList.add(people);
//                                    peopleAdapter.notifyDataSetChanged();
                                    break;
                            }
                        }


                    }
                }
            }
        });
    }
}
