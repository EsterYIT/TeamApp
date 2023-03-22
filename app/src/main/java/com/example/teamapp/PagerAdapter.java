package com.example.teamapp;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.teamapp.chat.HomeFragmentChat;

public class PagerAdapter extends FragmentPagerAdapter {

    private int numOfTabs;
    private final Context mContext;
    public PagerAdapter(FragmentManager fm,int numOfTabs,Context context){
        super(fm);
        this.numOfTabs=numOfTabs;
        this.mContext=context;
    }
    @Override
    public Fragment getItem(int position) {
        Fragment fragment=new HomeFragmentTeams();
        switch (position){
            case 0:
                fragment=new HomeFragmentTeams();
                break;
            case 1:
                fragment= new HomeFragmentChat();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
