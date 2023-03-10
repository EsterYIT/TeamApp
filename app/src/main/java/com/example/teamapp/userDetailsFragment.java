package com.example.teamapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.teamapp.hangout_info.hangOutTeamAct;
import com.example.teamapp.overview.Adapter.SameGroupsAdapter;
import com.example.teamapp.overview.intefaces.SameGroupsInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class userDetailsFragment extends Fragment implements SameGroupsInterface {

    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    FirebaseFirestore fStore;
    DocumentReference documentRef;
    SameGroupsAdapter sameGroupsAdapter;
    RecyclerView recyclerView;
    private ArrayList<Team> teamArrayList;
    CircleImageView userImg;
    ImageView backImg;
    TextView userName, userPhone,sameGroupsNum;
    String userId,id,teamID;
    int sameGroupsCounter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_details, container, false);

        recyclerView=v.findViewById(R.id.sameGroupsRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        teamArrayList = new ArrayList<Team>();

        sameGroupsAdapter = new SameGroupsAdapter(getContext(),teamArrayList,this);
        recyclerView.setAdapter(sameGroupsAdapter);
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        userImg = v.findViewById(R.id.userImage);
        userName = v.findViewById(R.id.userDetailsName);
        userPhone = v.findViewById(R.id.userPhoneNumber);
        backImg = v.findViewById(R.id.backIcon);
        sameGroupsNum = v.findViewById(R.id.sameGroupsNumber);

        Bundle bundle = getArguments();

        String name = bundle.getString("username");
        String phone = bundle.getString("phoneNumber");
        String image = bundle.getString("image");
        userId = bundle.getString("userId");

        Bitmap bm = getBitmapFromEncodedString(image);
        userImg.setImageBitmap(bm);
        userName.setText(name);
        userPhone.setText(phone);
        setListeners();

        fStore = FirebaseFirestore.getInstance();
        documentRef = fStore.collection("users").document(firebaseUser.getUid());
        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                List<String> idList = (List<String>)document.get("teams");
                for(int i = 0; i < idList.size(); i++){

                        documentRef = fStore.collection("Teams").document(idList.get(i));
                        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot document = task.getResult();
                                id = document.getId();
                                List<HashMap<String, String>> users = (List<HashMap<String, String>>) document.get("teamMembers");
                                Map<String, String> map = (Map<String, String>) document.get("teamImage");

                                for (int j = 0; j < users.size(); j++) {
                                    if (users.get(j).get("userID").equals(userId)) {
                                        sameGroupsCounter++;
                                        Team team = new Team(document.getString("teamName"), map.get("imageID"), id);
                                        teamArrayList.add(team);
                                        sameGroupsAdapter.notifyDataSetChanged();
                                        sameGroupsNum.setText(sameGroupsCounter + " same group/s");
                                        break;
                                    }
                                }
                            }
                        });

                }
            }
        });

        return v;
    }
    private void setListeners(){
        backImg.setOnClickListener(v->getActivity().onBackPressed());
    }

    private Bitmap getBitmapFromEncodedString(String encodedImage){
        if(encodedImage!=null){
            byte[] bytes= Base64.decode(encodedImage,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        }
        return null;
    }

    @Override
    public void onItemClicked(int position,String id,String name) {
        Intent intent = new Intent(getContext(), hangOutTeamAct.class);
        intent.putExtra("teamID",id);
        intent.putExtra("teamName",name);

        startActivity(intent);
    }
}