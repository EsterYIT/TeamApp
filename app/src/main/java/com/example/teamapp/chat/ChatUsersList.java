package com.example.teamapp.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;

import com.example.teamapp.User;
import com.example.teamapp.activities.BaseActivity;
import com.example.teamapp.chat.Adapter.ChatUsersAdapter;
import com.example.teamapp.chat.listeners.ChatUserListener;
import com.example.teamapp.databinding.ActivityChatUsersListBinding;
import com.example.teamapp.utils.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatUsersList extends BaseActivity implements ChatUserListener {
    private ActivityChatUsersListBinding binding;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth auth;
    List<User> users;
    PreferenceManager preferenceManager;
    DocumentReference documentRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager=new PreferenceManager(getApplicationContext());
        binding=ActivityChatUsersListBinding.inflate(getLayoutInflater());
        users =new ArrayList<>();
        setContentView(binding.getRoot());
        setListeners();
        getChatUsers();
    }

    private void setListeners(){
        binding.back.setOnClickListener(v->onBackPressed());
    }

    private void getChatUsers(){
        loading(true);
        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser =auth.getCurrentUser();
        myRef=database.getInstance().getReference("AuthUsers");
        FirebaseFirestore fireStore=FirebaseFirestore.getInstance();
        documentRef = fireStore.collection("users").document(firebaseUser.getUid());

        String username = getIntent().getStringExtra("username");
        String phoneNumber = getIntent().getStringExtra("userNumber");

        fireStore.collection("users").get()
                .addOnCompleteListener(task -> {
                   loading(false);
                   String currentUserId=auth.getCurrentUser().getUid();

                   if(task.isSuccessful() && task.getResult() !=null){
                       users = new ArrayList<>();
                       for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                           if(currentUserId.equals(queryDocumentSnapshot.getId())){
                               continue;
                           }
                           else if(contactNumberExist(this,queryDocumentSnapshot.getString("phoneNumber"))) {
                               User user = new User();
                               user.setUsername(queryDocumentSnapshot.getString("username"));
                               user.setEmail(queryDocumentSnapshot.getString("email"));
                               user.setUserImage(queryDocumentSnapshot.getString("userImage"));
                               user.setToken(queryDocumentSnapshot.getString("FCMToken"));
                               user.setPhoneNumber(queryDocumentSnapshot.getString("phoneNumber"));
                               user.setId(queryDocumentSnapshot.getId());
                               users.add(user);
                           }
                       }
                       found();
                   }
                });

  }

    public void found(){
        if(users.size()>0){
            ChatUsersAdapter chatUsersAdapter=new ChatUsersAdapter(users,this);
            binding.userRecyclerView.setAdapter(chatUsersAdapter);
            binding.userRecyclerView.setVisibility(View.VISIBLE);
        }else{
            showErrorMessage();
        }
        loading(false);
    }

    private void showErrorMessage(){
        binding.errorMessage.setText(String.format("%s","No user available"));
        binding.errorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.loadingBar.setVisibility(View.VISIBLE);
        }else{
            binding.loadingBar.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onUserClicked(User user) {
        Intent intent=new Intent(getApplicationContext(),ChatActivity.class);
        intent.putExtra("user",user);
        startActivity(intent);
        finish();
    }

    @SuppressLint("Range")
    private boolean contactNumberExist(Context context, String number) {

        boolean exist = false;

        // define the columns I want the query to return
        String[] projection = new String[] {
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup._ID};

        // encode the phone number and build the filter URI
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        // query time
        Cursor cursor = context.getContentResolver().query(contactUri, projection, null, null, null);

        if(cursor != null) {
            if (cursor.moveToFirst()) {
                System.out.println("Started uploadcontactphoto: Contact Found @ " + number);
                exist = true;
            }
            cursor.close();
        }
        return exist;
    }
}