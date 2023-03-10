package com.example.teamapp.overview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.teamapp.HomePage;
import com.example.teamapp.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChangeSubjectActivity extends AppCompatActivity {

    EditText teamName;
    Button confirm,cancel;
    FirebaseFirestore fStore;
    DocumentReference documentRef;
    String teamNameStr, teamId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_subject);

        teamName = findViewById(R.id.changeSub);
        confirm = findViewById(R.id.confirmBtn);
        cancel = findViewById(R.id.cancelBtn);

        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        teamNameStr = getIntent().getStringExtra("teamName");
        teamId = getIntent().getStringExtra("teamID");

        teamName.setText(teamNameStr);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date currentTime = new Date();
                teamNameStr = teamName.getText().toString();
                fStore = FirebaseFirestore.getInstance();
                documentRef = fStore.collection("Teams").document(teamId);
                documentRef.update("teamName",teamNameStr);
                Toast.makeText(ChangeSubjectActivity.this, "Team name changed successfully", Toast.LENGTH_SHORT).show();
                documentRef.update("lastEdit",formatter.format(currentTime));
                Intent intent = new Intent(ChangeSubjectActivity.this, HomePage.class);
                startActivity(intent);

            }
        });

        backPress();
    }
    public void backPress(){
        cancel.setOnClickListener(v->onBackPressed());
    }

}