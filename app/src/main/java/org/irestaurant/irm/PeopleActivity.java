package org.irestaurant.irm;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.kekstudio.dachshundtablayout.DachshundTabLayout;

import org.irestaurant.irm.Database.PagerAdapter;
import org.irestaurant.irm.Database.SessionManager;

import java.util.HashMap;

public class PeopleActivity extends AppCompatActivity  {
    SessionManager sessionManager;
    private DachshundTabLayout tabLayout;
    private ViewPager pager;
    Button btnHome, btnAddPeople;
    String getResEmail, getPosition;

    private void Anhxa(){
        btnHome = findViewById(R.id.btn_home);
        btnAddPeople = findViewById(R.id.btn_addaddpeople);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);
        addControl();
        Anhxa();
        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        getResEmail = user.get(sessionManager.RESEMAIL);
        getPosition = user.get(sessionManager.POSITION);
        if (!getPosition.equals("admin")){
            btnAddPeople.setVisibility(View.GONE);
        }
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnAddPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PeopleActivity.this, InviteActivity.class));
            }
        });
    }

    //  add Fragment
    public void addControl() {
        pager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (DachshundTabLayout) findViewById(R.id.tab_layout);

        FragmentManager manager = getSupportFragmentManager();
        PagerAdapter adapter = new PagerAdapter(manager, PeopleActivity.this);
        pager.setAdapter(adapter);

        tabLayout.setupWithViewPager(pager);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(adapter);//deprecated
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager));
    }

}
