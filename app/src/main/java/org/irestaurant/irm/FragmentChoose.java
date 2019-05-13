package org.irestaurant.irm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.irestaurant.irm.Database.Config;
import org.irestaurant.irm.Database.Food;
import org.irestaurant.irm.Database.FoodAdapter;
import org.irestaurant.irm.Database.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nullable;

public class FragmentChoose extends Fragment {
    RecyclerView lvFood;
    String getResEmail, getIdNunber,getNumber;
    SessionManager sessionManager;
    public static ArrayList<Food> foodList;
    public static FoodAdapter foodAdapter;
    public static FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
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
        foodAdapter = new FoodAdapter(getActivity(), foodList);
        lvFood.setHasFixedSize(true);
        lvFood.setLayoutManager(new LinearLayoutManager(getActivity()));
        lvFood.setAdapter(foodAdapter);
        // Inflate the layout for this fragment
        return view;
    }

    public void getGroup (String eMail){
        mFirestore.collection(Config.RESTAURANTS).document(eMail).collection(Config.MENU).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                foodList.clear();
                foodAdapter.notifyDataSetChanged();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    String ID = documentSnapshot.getId();
                    String firstID = ID.substring(0,1);
                    if (firstID.equals("0")){
                        Food food = documentSnapshot.toObject(Food.class).withId(ID);
                        foodList.add(food);
                        foodAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    public void getData(final String group1, String eMail) {
        mFirestore.collection(Config.RESTAURANTS).document(eMail).collection(Config.MENU).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                foodList.clear();
                foodAdapter.notifyDataSetChanged();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    String ID = documentSnapshot.getId();
                    String firstID = ID.substring(0,1);
                    String group = documentSnapshot.getString("group");
                    if (firstID.equals("0") || group1.equals(group)){
                        Food food = documentSnapshot.toObject(Food.class).withId(ID);
//                        foodList = Config.sortList(foodList);
                        foodList.add(food);
                        foodList = Config.sortList(foodList);
                        foodAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Config.CHECKACTIVITY = "FragmentChoose";
        foodList.clear();
        mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.MENU).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null){
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                        String categoryID = doc.getDocument().getId();
                        switch (doc.getType()){
                            case ADDED:
                                String first = categoryID.substring(0,1);
                                if (first.equals("0")){
                                    String foodId = doc.getDocument().getId();
                                    Food food = doc.getDocument().toObject(Food.class).withId(foodId);
                                    foodList.add(food);
                                    foodAdapter.notifyDataSetChanged();
                                }
                                break;
                        }
                    }
                }
            }
        });
    }
}
