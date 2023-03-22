package com.example.teamapp.inviteFriends;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.teamapp.addContact.ContactModel;
import com.example.teamapp.HomePage;
import com.example.teamapp.R;
import com.example.teamapp.User;
import com.example.teamapp.inviteFriends.adapters.inviteFriendsAdapter;
import com.example.teamapp.inviteFriends.interfaces.inviteInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InviteFriendsFragment extends Fragment implements inviteInterface {

    HashMap<String,Object> invitation = new HashMap<>();
    List<HashMap<String,Object>> showInvitation;
    List<HashMap<String,Object>> checkInvitationClicked;

    ArrayList<HashMap>hashMapArrayList = new ArrayList<>();
    HashMap<String,Object> answer = new HashMap<>();
    RecyclerView recyclerView;
    ArrayList<ContactModel> arrayList = new ArrayList<ContactModel>();
    com.example.teamapp.inviteFriends.adapters.inviteFriendsAdapter inviteFriendsAdapter;
    FirebaseFirestore fStore;
    DocumentReference documentRef;
    FirebaseAuth auth;
    EditText search;
    ImageView back;
    ArrayList<User>list;
    String teamID,managerName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_invite_friends, container, false);

        recyclerView = v.findViewById(R.id.inviteRecycler);
        search = v.findViewById(R.id.search);
        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        list = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        inviteFriendsAdapter = new inviteFriendsAdapter(this,getActivity(),arrayList);
        recyclerView.setAdapter(inviteFriendsAdapter);

        list = (ArrayList<User>) getArguments().getSerializable("list");
        teamID = getArguments().getString("teamID");

        getInvitationList();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

        documentRef = fStore.collection("Teams").document(teamID);
        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                answer.put("teamType",task.getResult().getString("teamType"));
            }
        });

        return v;
    }

    private void filter(String text){
        ArrayList<ContactModel> filteredList = new ArrayList<>();

        for (ContactModel item: arrayList) {
            if (item.getName1().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }else if(item.getNumber().contains(text)){
                filteredList.add(item);
            }
        }
        inviteFriendsAdapter.filterList(filteredList);

    }

    private void getInvitationList() {

        fStore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && task.getResult() !=null) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        boolean contains = false;
                        if(list != null) {
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).getId().equals(document.getId()))
                                    contains = true;
                            }
                            if (!contains && contactNumberExist(getContext(),document.getString("phoneNumber"))) {
                                ContactModel model = new ContactModel();
                                model.setName1(document.getString("username"));
                                model.setNumber(document.getString("phoneNumber"));
                                model.setImage(document.getString("userImage"));
                                model.setId(document.getId());
                                if(document.contains("invitation")) {
                                    checkInvitationClicked = (List<HashMap<String, Object>>) document.get("invitation");
                                    for(int i = 0; i < checkInvitationClicked.size();i++) {
                                        if(checkInvitationClicked.get(i).get("teamId").equals(teamID))
                                            model.setClicked(true);
                                            break;
                                    }
                                }
                                arrayList.add(model);
                                inviteFriendsAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }
      });
    }

    @SuppressLint({"ResourceAsColor", "NotifyDataSetChanged"})
    @Override
    public void inviteFriend(int position, String id,Button btn) {
        documentRef = fStore.collection("users").document(id);
        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();

                showInvitation = (List<HashMap<String, Object>>) document.get("invitation");
                if(showInvitation != null)
                    invitationExist(id);
                 else{
                    documentRef = fStore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            managerName = task.getResult().get("username").toString();
                            answer.put("managerName",managerName);
                            answer.put("teamId",teamID);
                            hashMapArrayList.add(answer);
                            invitation.put("invitation",hashMapArrayList);
                            documentRef = fStore.collection("users").document(id);
                            documentRef.update(invitation);
                        }
                    });
                }
            }
        });
    }

    public void invitationExist(String id) {
        fStore = FirebaseFirestore.getInstance();

        documentRef = fStore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
          managerName = task.getResult().get("username").toString();
          answer.put("managerName",managerName);
          answer.put("teamId",teamID);
          showInvitation.add(answer);
          invitation.put("invitation",showInvitation);
          documentRef = fStore.collection("users").document(id);
          documentRef.update(invitation);
         }
      });
    }

    @SuppressLint("Range")
    private boolean contactNumberExist(Context context, String number) {

        boolean exist = false;

        String[] projection = new String[] {
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup._ID};

        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor cursor = context.getContentResolver().query(contactUri, projection, null, null, null);

        if(cursor != null) {
            if (cursor.moveToFirst()) {
                exist = true;
            }
            cursor.close();
        }
        return exist;
    }
}