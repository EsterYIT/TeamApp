package com.example.teamapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;


public class SupportFragmentNav extends Fragment {

    ImageView back;
    TextInputEditText userInput;
    Button sendBtn;
    TextView title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_support_nav, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        back = v.findViewById(R.id.backIcon);
        userInput = v.findViewById(R.id.userTxt);
        sendBtn = v.findViewById(R.id.send);
        title = v.findViewById(R.id.tool_title);
        title.setText("Support");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HomePage.class);
                startActivity(intent);
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return v;
    }
}