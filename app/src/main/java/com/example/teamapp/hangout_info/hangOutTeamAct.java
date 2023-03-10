package com.example.teamapp.hangout_info;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.TextView;

import com.example.teamapp.GroupChat.GroupChatFragment;
import com.example.teamapp.chat.HomeFragmentChat;
import com.example.teamapp.OptionsFragment;
import com.example.teamapp.R;
import com.example.teamapp.bill.billFragment;
import com.example.teamapp.gallery.hangoutGalleryFragment;
import com.example.teamapp.allTeamsAdapter;
import com.example.teamapp.overview.OverViewFragment;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class hangOutTeamAct extends AppCompatActivity {

    ArrayList<Fragment> fragmentsList=new ArrayList<Fragment>();
    FragmentManager fragmentManagerHome;
    ViewPager viewPager;
    TabLayout tableLayout;
    allTeamsAdapter hangoutAdapter;
    TextView teamName;
    TabItem overView;
    TabItem gallery;
    TabItem chatTab;
    TabItem billTab;
    TabItem options;
    String teamNameStr,id;
    boolean album,chat,bill;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hang_out_team);

        teamNameStr = getIntent().getStringExtra("teamName");
        id = getIntent().getStringExtra("teamID");
        album = getIntent().getBooleanExtra("album",album);
        chat = getIntent().getBooleanExtra("chat",chat);
        bill = getIntent().getBooleanExtra("bill",bill);

        overView=findViewById(R.id.hangout_overview_tab);
        gallery=findViewById(R.id.hangout_gallery_tab);
        chatTab=findViewById(R.id.hangout_chat_tab);
        billTab=findViewById(R.id.hangout_bill_tab);
        options=findViewById(R.id.hangout_options_tab);
        tableLayout = findViewById(R.id.hangoutTabLayout);
        viewPager= findViewById(R.id.hangoutViewPage);
        teamName = findViewById(R.id.toolbar_title);
        fragmentManagerHome= getSupportFragmentManager();

        fragmentsList.add(new OverViewFragment());
        fragmentsList.add(new hangoutGalleryFragment());
        fragmentsList.add(new GroupChatFragment());
        fragmentsList.add(new billFragment());
        fragmentsList.add(new OptionsFragment());
        Bundle bundle = new Bundle();
        bundle.putString("teamID", id);
        bundle.putString("teamName",teamNameStr);

        for(int i = 0; i < fragmentsList.size(); i++) {
            fragmentsList.get(i).setArguments(bundle);
            tableLayout.invalidate();
            tableLayout.refreshDrawableState();
        }
        hangoutAdapter= new allTeamsAdapter(fragmentManagerHome ,fragmentsList);
        removeTabs();

        viewPager.setAdapter(hangoutAdapter);
        viewPager.setOffscreenPageLimit(5);
        viewPager.setCurrentItem(0);


        teamName.setText(teamNameStr);

        tableLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setAdapter(hangoutAdapter);
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
    private void removeTabs() {
            for(int i = 1; i < fragmentsList.size();i++){
                if(!album) {
                    if (fragmentsList.get(i) instanceof hangoutGalleryFragment) {
                        hangoutAdapter.removeTabPage(i);
                        tableLayout.removeTabAt(i);
                    }
                }
                if(!chat) {
                    if (fragmentsList.get(i) instanceof HomeFragmentChat) {
                        hangoutAdapter.removeTabPage(i);
                        tableLayout.removeTabAt(i);
                    }
                }
                if(!bill) {
                    if (fragmentsList.get(i) instanceof billFragment) {
                        hangoutAdapter.removeTabPage(i);
                        tableLayout.removeTabAt(i);
                    }
                }
            }
     }
}