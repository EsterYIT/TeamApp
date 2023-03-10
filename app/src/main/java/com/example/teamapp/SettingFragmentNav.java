package com.example.teamapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


public class SettingFragmentNav extends Fragment {

    ImageView back;
    TextView title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings_nav, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        back = v.findViewById(R.id.backIcon);
        title = v.findViewById(R.id.tool_title);
        title.setText("Settings");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),HomePage.class);
                startActivity(intent);
            }
        });
        return v;
    }
}