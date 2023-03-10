package com.example.teamapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangeEmailFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    ImageView back;
    TextView changeEmail;
    TextInputEditText editOldEmail,editNewEmail, editPass;
    TextView verify, title;
    Button updateBtn, authBtn;
    String userPass,oldEmail,newEmail;
    ProgressBar progressBar;
    FragmentManager fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_change_email, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        updateBtn = v.findViewById(R.id.save);
        authBtn = v.findViewById(R.id.authBtn);
        changeEmail = v.findViewById(R.id.changeEmailTXT);
        verify = v.findViewById(R.id.verifyEmail);
        editOldEmail = v.findViewById(R.id.oldEmail);
        editNewEmail = v.findViewById(R.id.newEmail);
        editPass = v.findViewById(R.id.verifyPass);
        back = v.findViewById(R.id.backIcon);
        title = v.findViewById(R.id.tool_title);
        progressBar = v.findViewById(R.id.progressbar);

        updateBtn.setEnabled(false);
        editNewEmail.setEnabled(false);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        oldEmail = firebaseUser.getEmail();
        editOldEmail.setText(firebaseUser.getEmail().toString());
        editOldEmail.setEnabled(false);

        title.setText("Change Email");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), HomePage.class);
                startActivity(intent);
            }
        });

        if(firebaseUser.equals(""))
            Toast.makeText(getContext(), "User's details not available", Toast.LENGTH_SHORT).show();
        else
            reAuthenticate(firebaseUser);

        return v;
    }


    private void reAuthenticate(FirebaseUser firebaseUser) {
        authBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userPass = editPass.getText().toString();
                if(TextUtils.isEmpty(userPass)){
                    Toast.makeText(getContext(), "password needed", Toast.LENGTH_SHORT).show();
                    editPass.setError("Please enter a password for authentication");
                    editPass.requestFocus();
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    AuthCredential credential = EmailAuthProvider.getCredential(oldEmail,userPass);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);

                                verify.setText("You are authenticated. You can update your email now.");
                                editNewEmail.setEnabled(true);
                                updateBtn.setEnabled(true);
                               // updateBtn.setTextColor(1);
                                editPass.setEnabled(false);
                                authBtn.setEnabled(false);

                                updateBtn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(),
                                        R.color.light_main));

                                updateBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        newEmail = editNewEmail.getText().toString();
                                        if(TextUtils.isEmpty(newEmail)){
                                            Toast.makeText(getContext(), "New email is required", Toast.LENGTH_SHORT).show();
                                            editNewEmail.setError("Please enter new Email");
                                            editNewEmail.requestFocus();
                                        }else if(!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()){
                                            Toast.makeText(getContext(), "Email not valid", Toast.LENGTH_SHORT).show();
                                            editNewEmail.setError("Please enter valid Email");
                                            editNewEmail.requestFocus();
                                        }else if(oldEmail.matches(newEmail)){
                                            Toast.makeText(getContext(), "New Email cannot be same as old Email", Toast.LENGTH_SHORT).show();
                                            editNewEmail.setError("Please enter new Email");
                                            editNewEmail.requestFocus();
                                        }else{
                                            progressBar.setVisibility(View.VISIBLE);
                                            updateEmail(firebaseUser);
                                        }
                                    }
                                });
                            }else{
                                try {
                                    throw task.getException();
                                }catch (Exception e){
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void updateEmail(FirebaseUser firebaseUser) {
        firebaseUser.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    firebaseUser.sendEmailVerification();
                    Toast.makeText(getContext(), "Email has been updated, please verify your new Email", Toast.LENGTH_SHORT).show();
                    fragment = getActivity().getSupportFragmentManager();
                    fragment.beginTransaction().replace(R.id.fragment_container,EditProfileFragmentNav.class,null)
                            .setReorderingAllowed(true).addToBackStack(null).commit();

                }else{
                    try {
                        throw task.getException();
                    }catch (Exception e){
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

}