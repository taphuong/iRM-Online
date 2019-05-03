package org.irestaurant.irm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.irestaurant.irm.Database.Config;
import org.irestaurant.irm.Database.Food;
import org.irestaurant.irm.Database.FoodOrderedAdapter;
import org.irestaurant.irm.Database.Ordered;
import org.irestaurant.irm.Database.OredredAdapter;
import org.irestaurant.irm.Database.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FragmentChoose extends Fragment {
    RecyclerView lvFood;
    String getResEmail, getIdNunber,getNumber;
    SessionManager sessionManager;
    List<Food> foodList;
    FoodOrderedAdapter foodOrderedAdapter;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    CollectionReference numberRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose, container, false);
        OrderedActivity orderedActivity = new OrderedActivity();
        getIdNunber = orderedActivity.getIdNumber;
        getNumber   = orderedActivity.getNumber;
//        getIdNunber = getArguments().getString("idnumber");
//        getNumber   = getArguments().getString("number");
        lvFood = view.findViewById(R.id.lv_food);
        sessionManager = new SessionManager(getActivity());
        sessionManager.checkLoggin();
        HashMap<String, String> user = sessionManager.getUserDetail();
        getResEmail = user.get(sessionManager.RESEMAIL);
        numberRef = mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.NUMBER);


        foodList = new ArrayList<>();
        foodOrderedAdapter = new FoodOrderedAdapter(getActivity(), foodList);
        lvFood.setHasFixedSize(true);
        lvFood.setLayoutManager(new LinearLayoutManager(getActivity()));
        lvFood.setAdapter(foodOrderedAdapter);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        foodList.clear();
        mFirestore.collection(Config.RESTAURANTS+"/"+getResEmail+"/"+Config.MENU).addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null){
                    return;
                }else {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()){
                        String foodId = doc.getDocument().getId();
                        switch (doc.getType()){
                            case ADDED:
                                Food food = doc.getDocument().toObject(Food.class).withId(foodId);
                                foodList.add(food);
                                foodOrderedAdapter.notifyDataSetChanged();
                                break;
                        }
                    }
                }
            }
        });
    }
}
