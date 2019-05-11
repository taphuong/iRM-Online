package org.irestaurant.irm;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.irestaurant.irm.Database.Config;
import org.irestaurant.irm.Database.FoodOrderedAdapter;
import org.irestaurant.irm.Database.Ordered;
import org.irestaurant.irm.Database.OredredAdapter;
import org.irestaurant.irm.Database.SessionManager;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;


public class FragmentOrdered extends Fragment {
    RecyclerView lvOrdered;
    String getResEmail, getIdNunber,getNumber;
    SessionManager sessionManager;
    List<Ordered> orderedList;
    OredredAdapter oredredAdapter;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    CollectionReference numberRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ordered, container, false);
        lvOrdered = view.findViewById(R.id.lv_ordered);
        sessionManager = new SessionManager(getActivity());
        sessionManager.checkLoggin();
        HashMap<String, String> user = sessionManager.getUserDetail();
        getResEmail = user.get(sessionManager.RESEMAIL);
        numberRef = mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.NUMBER);
        OrderedActivity orderedActivity = new OrderedActivity();
        getIdNunber = orderedActivity.getIdNumber;
        getNumber   = orderedActivity.getNumber;
//        getIdNunber = getArguments().getString("idnumber");
//        getNumber   = getArguments().getString("number");

        orderedList = new ArrayList<>();
        oredredAdapter = new OredredAdapter(getActivity(), orderedList);
        lvOrdered.setHasFixedSize(true);
        lvOrdered.setLayoutManager(new LinearLayoutManager(getActivity()));
        lvOrdered.setAdapter(oredredAdapter);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        orderedList.clear();
        mFirestore.collection(Config.RESTAURANTS+"/"+getResEmail+"/"+Config.NUMBER+"/"+getIdNunber+"/unpaid").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }else {
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                        String orderId = doc.getDocument().getId();
                        switch (doc.getType()){
                            case ADDED:
                                Ordered ordered = doc.getDocument().toObject(Ordered.class).withId(orderId);
                                orderedList.add(ordered);
                                oredredAdapter.notifyDataSetChanged();

                                break;
                            case REMOVED:
                                mFirestore
                                break;
                            case MODIFIED:
//                                loadTotal();
                                break;
                        }
                    }
                }
            }
        });
    }
}
