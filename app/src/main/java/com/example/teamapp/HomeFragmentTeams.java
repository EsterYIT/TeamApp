package com.example.teamapp;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeFragmentTeams extends Fragment {

    ArrayList<Team> teams;
    TeamsAdapter teamsAdapter;
    RecyclerView recyclerView;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    DocumentReference documentRef;
    Button changeOrder;
    String meeting = "MEETING DATE";
    String lastEdit = "LAST ACTIVE";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View v= inflater.inflate(R.layout.fragment_home_teams, container, false);

        recyclerView=v.findViewById(R.id.teamsRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        changeOrder = v.findViewById(R.id.orderBtn);
        if (container == null) // must put this in
            return null;

        changeOrder.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                if(lastEdit.equals(changeOrder.getText().toString())) {
                    Collections.sort(teams, (obj1, obj2) -> obj1.getDate().compareTo(obj2.getDate()));
                    teamsAdapter.notifyDataSetChanged();
                    changeOrder.setText(meeting);
                }else{
                    Collections.sort(teams, (obj1, obj2) -> obj1.getLast().compareTo(obj2.getLast()));
                    Collections.reverse(teams);
                    teamsAdapter.notifyDataSetChanged();
                    changeOrder.setText(lastEdit);
                }
            }
        });

       return v;
    }

    public void dataFetcher(){
        teams = new ArrayList<Team>();
        teamsAdapter = new TeamsAdapter(getContext(),teams);
        recyclerView.setAdapter(teamsAdapter);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        fStore = FirebaseFirestore.getInstance();

        documentRef = fStore.collection("users").document(user.getUid());
        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                List<String> idList = (List<String>)document.get("teams");
                if(idList != null) {
                    for (int i = 0; i < idList.size(); i++) {
                        documentRef = fStore.collection("Teams").document(idList.get(i));
                        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot document = task.getResult();
                                HashMap<String,Boolean> allTabs = (HashMap<String,Boolean>)document.get("tabs");
                                Map<String, String> map = (Map<String, String>) document.get("teamImage");
                                if (map != null) {
                                    Team team = null;
                                    try {
                                        team = new Team(document.getString("date"), document.getString("teamName"),
                                                document.getString("teamType"), map.get("imageID"), task.getResult().getId(),allTabs.get("albumEnabled"),
                                                allTabs.get("chatEnabled"), allTabs.get("menuEnabled"),document.getString("lastEdit"));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    teams.add(team);
                                    if(teams.size() == idList.size()){
                                        Collections.sort(teams,(obj1, obj2)-> obj1.getLast().compareTo(obj2.getLast()));
                                        Collections.reverse(teams);
                                        teamsAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        dataFetcher();
    }

    public void test(View view){

    }
}