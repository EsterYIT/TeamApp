package com.example.teamapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.teamapp.carpool_info.Carpool_Settings_Fragment;
import com.example.teamapp.hangout_info.Hangout_Settings_Fragment;

public class CreateNewTeam extends AppCompatActivity {

//    String[] teams = {"Hangout Team", "Carpool Team"};
    String[] teams = {"Hangout Team"};

    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterTeams;
    FragmentManager fragment;
    ImageView back;
    TextView title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_team);

        back = findViewById(R.id.backIcon);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        title = findViewById(R.id.tool_title);
        title.setText("Create A New Team");
        adapterTeams = new ArrayAdapter<String>(this,R.layout.drop_down_list,teams);
        autoCompleteTextView.setAdapter(adapterTeams);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                String team = parent.getItemAtPosition(i).toString();
                fragment = getSupportFragmentManager();
                if(team.equals("Hangout Team")){
                    fragment.beginTransaction().replace(R.id.fragmentContainerView4,
                            Hangout_Settings_Fragment.class,null).setReorderingAllowed(true).addToBackStack(null).commit();
                }else{
                    fragment.beginTransaction().replace(R.id.fragmentContainerView4,
                            Carpool_Settings_Fragment.class,null).setReorderingAllowed(true).addToBackStack(null).commit();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomePage.class);
                startActivity(intent);
            }
        });

    }
}