package com.example.teamapp.activities;

import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import com.example.teamapp.utils.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class BaseActivity extends AppCompatActivity {

    private DocumentReference documentReference;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager preferenceManager=new PreferenceManager(getApplicationContext());
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        documentReference=database.collection("users").document(auth.getCurrentUser().getUid());

    }

    @Override
    protected void onPause(){
        super.onPause();
        documentReference.update("availability",0);
    }


    @Override
    protected void onResume(){
        super.onResume();
        documentReference.update("availability",1);
    }
}
