package com.example.teamapp.inviteFriends;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;

import com.example.teamapp.HomeFragmentNav;
import com.example.teamapp.HomeFragmentTeams;
import com.example.teamapp.HomePage;
import com.example.teamapp.MainActivity;
import com.example.teamapp.R;

import com.example.teamapp.chat.HomeFragmentChat;
import com.example.teamapp.inviteFriends.adapters.InvitationRequestAdapter;
import com.example.teamapp.inviteFriends.interfaces.InvitationAnswer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Notifications implements InvitationAnswer {

    InvitationAnswer invitationAnswer = this;
    private ArrayList<InvitationRequest> list;
    private ListPopupWindow listPopupWindow;
    private TextView notificationNum;
    ArrayList<String> teamId;
    InvitationRequestAdapter requestAdapter;
    List<HashMap<String,String>> invitation;
    FirebaseFirestore fStore;
    DocumentReference docRef;
    ImageView bell;
    Context context;
    Toolbar toolbar;
    View view;

    public Notifications(View view, Context context, Toolbar toolbar){
        this.context = context;
        this.view = view;
        this.toolbar = toolbar;
        notificationNum = view.findViewById(R.id.noteCounter);
        bell = view.findViewById(R.id.invitation);
        teamId = new ArrayList<>();

          bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list = new ArrayList<>();
                invitationRequest();
            }
        });
    }

    public void getNumOfNotifications(){
        fStore = FirebaseFirestore.getInstance();
        docRef = fStore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                invitation = (List<HashMap<String, String>>) document.get("invitation");
                if(invitation != null){
                for (int i = 0; i < invitation.size(); i++) {
                        view.setVisibility(View.VISIBLE);
                        notificationNum.setText(String.valueOf(invitation.size()));
                        teamId.add(invitation.get(i).get("teamId"));
                    }
                }
            }
        });
    }

    private void invitationRequest(){
        fStore = FirebaseFirestore.getInstance();
        docRef = fStore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                invitation = (List<HashMap<String, String>>) document.get("invitation");
                if(invitation != null) {
                    for (int i = 0; i < invitation.size(); i++) {
                        InvitationRequest request = new InvitationRequest(invitation.get(i).get("managerName"),invitation.get(i).get("teamType"),teamId.get(i));
                        list.add(request);
                        listPopupWindow = new ListPopupWindow(context);
                        requestAdapter = new InvitationRequestAdapter(context,0,list,invitationAnswer);
                        listPopupWindow.setAnchorView(toolbar);
                        listPopupWindow.setAdapter(requestAdapter);
                        requestAdapter.notifyDataSetChanged();
                    }
                    listPopupWindow.show();
                }
            }
        });
    }

    @Override
    public void userResponse(int position, ImageView yes, ImageView no) {

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(position);
                notificationNum.setText(String.valueOf(list.size()));
                requestAdapter.notifyDataSetChanged();
                if(notificationNum.getText().toString().equals("0")) {
                    listPopupWindow.dismiss();
                    view.setVisibility(View.GONE);
                }

                fStore = FirebaseFirestore.getInstance();
                docRef = fStore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

                if(list != null)
                    docRef.update("invitation",list);
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = list.get(position).teamId;
                list.remove(position);
                notificationNum.setText(String.valueOf(list.size()));
                requestAdapter.notifyDataSetChanged();
                if(notificationNum.getText().toString().equals("0")) {
                    listPopupWindow.dismiss();
                    view.setVisibility(View.GONE);
                }

                if(list != null) {
                    docRef.update("invitation",list);
                }

                //add user to the group
                DocumentReference documentReference = fStore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        ArrayList<String> arrayList = (ArrayList<String>)task.getResult().get("teams");

                        arrayList.add(id);
                        documentReference.update("teams",arrayList);
                    }
                });

                //add group to the user
                docRef = fStore.collection("Teams").document(id);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        List<HashMap<String,Object>> users = (List<HashMap<String,Object>>)task.getResult().get("teamMembers");
                        HashMap<String,Object>addUser = new HashMap<>();
                        addUser.put("userID",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        addUser.put("userLevel",3);
                        users.add(addUser);
                        docRef.update("teamMembers",users);
                    }
                });

                SystemClock.sleep(2000);
                ((HomePage)context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,new HomeFragmentNav()).commit();

            }
        });
    }
}
