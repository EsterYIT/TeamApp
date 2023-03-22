package com.example.teamapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.util.Base64;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.teamapp.utils.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    FragmentManager fragment;
    FirebaseAuth auth;
    Button login, register;
    EditText email,password;
    ScrollView scrollView2;
    PreferenceManager preferenceManager;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = findViewById(R.id.loginBtn);
        register = findViewById(R.id.registerBtn);
        email = findViewById(R.id.emailInput);
        password = findViewById(R.id.passwordInput);
        auth=FirebaseAuth.getInstance();
        preferenceManager= new PreferenceManager(getApplicationContext());
        scrollView2=findViewById(R.id.scroller);

        myRef = database.getInstance().getReference("AuthUsers");
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = getSupportFragmentManager();
                fragment.beginTransaction().replace(R.id.fragmentContainerView,registerFragment.class,null).setReorderingAllowed(true).addToBackStack(null).commit();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.getText().toString().length()>2 && password.getText().toString().length()>2)
                    loginUser(email.getText().toString(),password.getText().toString());
            }
        });
    }

    private void loginUser(String email1, String password1) {
        Bitmap bm = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_profile_place_holder);
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, byteArray); // bm is the bitmap object
        byte[] byteArrayImage = byteArray.toByteArray();
        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        preferenceManager.putString("defaultPicture",encodedImage);
        auth.signInWithEmailAndPassword(email1,password1).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                    Toast.makeText(MainActivity.this, "you logged in successfully ", Toast.LENGTH_SHORT).show();
                    preferenceManager.putString("currentUserId",auth.getCurrentUser().getUid().toString());
                    startActivity(new Intent(MainActivity.this,HomePage.class));
                    FirebaseFirestore fireStore=FirebaseFirestore.getInstance();
                    DocumentReference documentReference=fireStore.collection("users").document(FirebaseAuth.getInstance().getUid());

                    documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            preferenceManager.putString("userName",value.getString("username"));
                            preferenceManager.putString("userEmail",value.getString("email"));
                            preferenceManager.putString("userPhoneNumber",value.getString("phoneNumber"));
                            preferenceManager.putString("userImage",value.getString("userImage"));
                        }
                    });
                    finish();
                }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Wrong Details", Toast.LENGTH_SHORT).show();
            }
        });
    }
}