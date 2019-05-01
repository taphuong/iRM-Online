package org.irestaurant.irm.Database;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.irestaurant.irm.CurrentPeopleFragment;
import org.irestaurant.irm.JoinPeopleFragment;

import java.util.HashMap;

public class PagerAdapter extends FragmentStatePagerAdapter {
    Context context;

    public PagerAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        this.context = context;
    }
    @Override
    public Fragment getItem(int position) {

        Fragment frag=null;
        switch (position){
            case 0:
                frag = new CurrentPeopleFragment();
                break;
            case 1:
                frag = new JoinPeopleFragment();
                break;
        }
        return frag;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        String title = "";
        switch (position){
            case 0:
                title = "Nhân viên";
                break;
            case 1:
                title = "Duyệt đơn";

                break;
        }
        return title;
    }
}