package com.example.teamapp.GroupChat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Message;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.teamapp.R;
import com.example.teamapp.User;
import com.example.teamapp.chat.models.ChatMessage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class GroupChatFragment extends Fragment implements View.OnClickListener{

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseFirestore fireStore;
    DatabaseReference messageDB;
    GroupMessagesAdapter messageAdapter;
    User user;
    List<GroupMessage> messages;
    String TeamId;
    RecyclerView rvMessages;
    EditText input_message;
    FrameLayout sendButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_group_chat, container, false);
        init(v);
        return v;
    }

    private void init(View v){
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        fireStore=FirebaseFirestore.getInstance();
        user=new User();
        TeamId="2";
        rvMessages=(RecyclerView) v.findViewById(R.id.chatRecyclerView);
        input_message=v.findViewById(R.id.inputMessage);
        sendButton=v.findViewById(R.id.layout_send);
        sendButton.setOnClickListener(this);
        messages=new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        if(!TextUtils.isEmpty(input_message.getText().toString())){
            GroupMessage message=new GroupMessage(input_message.getText().toString(),user.getUsername(),auth.getCurrentUser().getUid(),new Date());
            input_message.setText("");
            messageDB.push().setValue(message);
        }else{
            Toast.makeText(getContext(), "You can't send empty message", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        final FirebaseUser currentUser=auth.getCurrentUser();
        user.setId(currentUser.getUid());
        fireStore.collection("users").document(currentUser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
            user=value.toObject(User.class);
            user.setId(currentUser.getUid());
            }
        });
        messageDB=database.getReference("chats/"+TeamId);
        messageDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                GroupMessage message=snapshot.getValue(GroupMessage.class);
                message.setKey(snapshot.getKey());
                messages.add(message);
                displayMessages(messages);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                GroupMessage message=snapshot.getValue(GroupMessage.class);
                message.setKey(snapshot.getKey());
                List<GroupMessage> newMessages=new ArrayList<>();

                for (GroupMessage m : messages) {
                    if (m.getMessage().equals(message.getKey())) {
                        newMessages.add(message);
                    }else{
                        newMessages.add(m);
                    }
                }
                messages=newMessages;
                displayMessages(messages);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                GroupMessage message=snapshot.getValue(GroupMessage.class);
                message.setKey(snapshot.getKey());
                List<GroupMessage> newMessages=new ArrayList<>();

                for (GroupMessage m : messages) {
                    if (!m.getMessage().equals(message.getKey())) {
                        newMessages.add(m);
                    }
                }
                messages=newMessages;
                displayMessages(messages);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        messages=new ArrayList<>();
    }



    private void displayMessages(List<GroupMessage> messages) {
        rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        messageAdapter=new GroupMessagesAdapter(getContext(),messages,messageDB,auth.getCurrentUser().getUid());
        rvMessages.setAdapter(messageAdapter);
    }
}