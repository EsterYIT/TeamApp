package com.example.teamapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Base64;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teamapp.utils.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Objects;

public class registerFragment extends Fragment {

    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    FragmentManager fragmentManager;
    EditText username,email,phoneNumber,password,confirmPas;
    String emailStr;
    Button signup,login;
    TextView title;
    ImageView back;
    private PreferenceManager preferenceManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        preferenceManager=new PreferenceManager(getContext());
        fragmentManager = getFragmentManager();
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        username = v.findViewById(R.id.usernameInputReg);
        email = v.findViewById(R.id.emailInput);
        phoneNumber = v.findViewById(R.id.phoneInput);
        password = v.findViewById(R.id.passInputReg);
        confirmPas = v.findViewById(R.id.confirmPasInput);
        signup = v.findViewById(R.id.singupBtn);
        mAuth = FirebaseAuth.getInstance();
        emailStr = email.getText().toString();
        title = v.findViewById(R.id.tool_title);
        back = v.findViewById(R.id.backIcon);
        title.setText("Signup Page");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if(!confirmPas.getText().toString().equals(password.getText().toString())) {
                        password.setError("Password and username didn't match");
                        confirmPas.setError("Password and username didn't match");
                        return;
                    }if(password.getText().length() < 8){
                            password.setError("Password must be at least 8 characters");
                        return;
                        }
                    if(Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()){
                        email.setError("Invalid email");
                        return;
                    }if(phoneNumber.getText().length() != 10) {
                        phoneNumber.setError("Invalid phone number");
                        return;
                    }if(username.getText().length() < 2){
                        username.setError("Username must be ay least 2 letters");
                        return;
                    }
                String encodedImage = defaultImage();
                User user= new User(username.getText().toString(),email.getText().toString(),phoneNumber.getText().toString(),encodedImage);
                singUp(user);
                Toast.makeText(getActivity(), "signup successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    private String defaultImage(){
        Bitmap bm = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_profile_place_holder);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
        byte[] byteArrayImage = baos.toByteArray();
        return Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
    }

    private void singUp(User user) {
        String emailS=email.getText().toString();
        String passwordS=password.getText().toString();
        mAuth.createUserWithEmailAndPassword(emailS,passwordS).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseFirestore fireStore=FirebaseFirestore.getInstance();
                    fireStore.collection("users").document(FirebaseAuth.getInstance().getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user2 = mAuth.getCurrentUser();
                                user2.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getActivity(), "Verification email has been sent", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        startActivity(intent);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), "Failure: email not sent", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else{
                                Toast.makeText(getContext(), "user wasn't able to be added", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(getContext(), "user wasn't able to be added", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}