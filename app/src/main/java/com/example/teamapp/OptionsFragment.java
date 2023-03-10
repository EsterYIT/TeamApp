package com.example.teamapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.teamapp.hangout_info.Update_Hangout_Settings_Fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;


public class OptionsFragment extends Fragment {

    TextView settings;
    FirebaseFirestore fStore;
    DocumentReference documentRef;
    String teamID;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_options, container, false);

        teamID = getArguments().getString("teamID");
        settings = v.findViewById(R.id.settings);

        fStore = FirebaseFirestore.getInstance();
        documentRef = fStore.collection("Teams").document(teamID);
        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot document = task.getResult();
                List<HashMap<String,String>> users = (List<HashMap<String,String>>)document.get("teamMembers");
                if(FirebaseAuth.getInstance().getUid().equals(users.get(0).get("userID"))) {
                    settings.setClickable(true);
                    settings.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String name = document.getString("teamName");
                            String place = document.getString("meetingPlace");
                            String date = document.getString("date");
                            String time = document.getString("time");
                            HashMap<String,Boolean> allTabs = (HashMap<String,Boolean>)document.get("tabs");

                            boolean album = allTabs.get("albumEnabled");
                            boolean chat = allTabs.get("chatEnabled");
                            boolean bill = allTabs.get("menuEnabled");

                            Update_Hangout_Settings_Fragment updateHangoutSettingsFragment = new Update_Hangout_Settings_Fragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("teamName", name);
                            bundle.putString("teamID", teamID);
                            bundle.putString("place", place);
                            bundle.putString("date", date);
                            bundle.putString("time", time);
                            bundle.putBoolean("album",album);
                            bundle.putBoolean("chat",chat);
                            bundle.putBoolean("bill",bill);
                            updateHangoutSettingsFragment.setArguments(bundle);
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView7,
                                    updateHangoutSettingsFragment).commit();
                        }
                    });
                }
            }
        });

        return v;
    }
}