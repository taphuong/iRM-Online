package org.irestaurant.irm;

import android.app.Activity;
import android.content.Intent;
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
import org.irestaurant.irm.Database.Food;
import org.irestaurant.irm.Database.FoodGroupAdapter;
import org.irestaurant.irm.Database.SessionManager;
import org.irestaurant.irm.Interface.IOnFoodGroupClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class CategoryActivity extends Activity {
    RecyclerView lvGroup;
    List<String> foodGroupList;
    public FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    SessionManager sessionManager;
    String getResEmail;
    FoodGroupAdapter foodGroupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        sessionManager = new SessionManager(this);
        Map<String, String> user = sessionManager.getUserDetail();
        getResEmail = user.get(sessionManager.RESEMAIL);
        lvGroup = findViewById(R.id.lv_group);
        foodGroupList = new ArrayList<>();
        foodGroupAdapter = new FoodGroupAdapter(foodGroupList);
        foodGroupAdapter.setIOnFoodGroupClickListener(new IOnFoodGroupClickListener() {
            @Override
            public void onFoodGroupClickListener(String foodGroup, int postion) {
                if (postion == foodGroupList.size()-1){
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", "lastposition");
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }else {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", foodGroup);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
        lvGroup.setHasFixedSize(true);
        lvGroup.setLayoutManager(new LinearLayoutManager(this));
        lvGroup.setAdapter(foodGroupAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        foodGroupList.clear();
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
                                    String categoryName = doc.getDocument().getString(Config.GROUP);
                                    foodGroupList.add(categoryName);
                                    foodGroupAdapter.notifyDataSetChanged();
                                }

                                break;
                        }
                    }
                }
            }
        });
    }
}
