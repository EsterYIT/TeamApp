package com.example.teamapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.Toast;

import com.example.teamapp.chat.ChatUsersList;
import com.example.teamapp.inviteFriends.Notifications;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;


public class HomeFragmentNav extends Fragment {

    FragmentManager fragmentManagerHome;
    ViewPager viewPager;
    TabLayout tableLayout;
    PagerAdapter pagerAdapter;
    TabItem TeamsTab;
    TabItem ChatTab;
    View v;
    FloatingActionButton floaty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_home_nav, container, false);

        tableLayout = v.findViewById(R.id.TabBar);
        TeamsTab= v.findViewById(R.id.TeamsTab);
        ChatTab= v.findViewById(R.id.ChatTab);
        viewPager= v.findViewById(R.id.ViewPager);
        floaty= v.findViewById(R.id.floaty);

        floaty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext(), "floaty is clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), ChatUsersList.class));
            }
        });

        fragmentManagerHome= getChildFragmentManager();
        pagerAdapter= new PagerAdapter(fragmentManagerHome ,
                tableLayout.getTabCount(),getContext());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setCurrentItem(0);
        tableLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setAdapter(pagerAdapter);
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setHasOptionsMenu(true);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tableLayout.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return v;
    }
    private void addTab(String title) {
        tableLayout.addTab(tableLayout.newTab().setText(title));

    }
    public void removeTab(int position) {
        if (tableLayout.getTabCount() >= 1 && position<tableLayout.getTabCount()) {
            tableLayout.removeTabAt(position);
        }
    }

}