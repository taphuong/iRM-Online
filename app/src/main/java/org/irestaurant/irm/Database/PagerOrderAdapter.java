package org.irestaurant.irm.Database;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.irestaurant.irm.CurrentPeopleFragment;
import org.irestaurant.irm.FragmentChoose;
import org.irestaurant.irm.FragmentOrdered;
import org.irestaurant.irm.JoinPeopleFragment;

public class PagerOrderAdapter extends FragmentStatePagerAdapter {
    Context context;

    public PagerOrderAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        this.context = context;
    }
    @Override
    public Fragment getItem(int position) {

        Fragment frag=null;
        switch (position){
            case 0:
                frag = new FragmentChoose();
                break;
            case 1:
                frag = new FragmentOrdered();
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
                title = "Chọn món";
                break;
            case 1:
                title = "Đã chọn";

                break;
        }
        return title;
    }
}