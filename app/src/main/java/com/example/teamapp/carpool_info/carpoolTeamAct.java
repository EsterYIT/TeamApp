package com.example.teamapp.carpool_info;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.TextView;

import com.example.teamapp.R;
import com.example.teamapp.allTeamsAdapter;
import com.example.teamapp.overview.OverViewFragment;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class carpoolTeamAct extends AppCompatActivity {

    FragmentManager fragmentManagerHome;
    DatabaseReference myRef;
    ViewPager viewPager;
    TabLayout tableLayout;
    allTeamsAdapter carpoolAdapter;
    TextView teamName;
    TabItem overView;
    TabItem car;
    TabItem chat;
    TabItem map;
    TabItem options;
    String teamNameStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpool_team);

        teamNameStr = getIntent().getStringExtra("teamName");
        String id = getIntent().getStringExtra("teamID");

        overView=findViewById(R.id.carpool_overview_tab);
        car=findViewById(R.id.carpool_car_tab);
        chat=findViewById(R.id.carpool_chat_tab);
        map=findViewById(R.id.carpool_map_tab);
        options=findViewById(R.id.carpool_options_tab);
        tableLayout = findViewById(R.id.carpoolTabLayout);
        viewPager= findViewById(R.id.carpoolViewPage);
        teamName = findViewById(R.id.toolbar_title);
        fragmentManagerHome= getSupportFragmentManager();
        ArrayList<Fragment> fragmentsList=new ArrayList<Fragment>();
        fragmentsList.add(new OverViewFragment());
        Bundle bundle = new Bundle();
        bundle.putString("teamID", id);
        bundle.putString("teamName",teamNameStr);

        for(int i = 0; i < fragmentsList.size(); i++){
            fragmentsList.get(i).setArguments(bundle);
        }
        carpoolAdapter = new allTeamsAdapter(fragmentManagerHome,fragmentsList);
        viewPager.setAdapter(carpoolAdapter);
        viewPager.setOffscreenPageLimit(5);
        viewPager.setCurrentItem(0);

        teamName.setText(teamNameStr);

        tableLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setAdapter(carpoolAdapter);
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

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
    }
}