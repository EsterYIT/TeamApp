package com.example.teamapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teamapp.addContact.ContactListFragment;
import com.example.teamapp.inviteFriends.Notifications;
import com.example.teamapp.utils.PreferenceManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    ArrayList<Team> teams;
    private DrawerLayout drawer;
    private FirebaseAuth auth;
    FirebaseFirestore fStore;
    DocumentReference docRef;
    View headerView;
    TextView email,name,counter;
    ImageView profileImage,plus;
    NavigationView navigationView;
    Toolbar toolbar;
    RelativeLayout relativeLayout;
    Notifications notifications;

    private PreferenceManager preferenceManager;


    @SuppressLint({"ResourceType", "WrongViewCast"})
    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        drawer = findViewById(R.id.drawer_layout);
        auth = FirebaseAuth.getInstance();
        teams = new ArrayList<Team>();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferenceManager = new PreferenceManager(getApplicationContext());
        plus = findViewById(R.id.plusId);
        counter = findViewById(R.id.noteCounter);
        navigationView = findViewById(R.id.nav_view);
        relativeLayout = findViewById(R.id.rela);
        headerView = navigationView.getHeaderView(0);
        email = (TextView) headerView.findViewById(R.id.emailBar);
        name = (TextView) headerView.findViewById(R.id.nameBar);
        profileImage = (ImageView) headerView.findViewById(R.id.profileImage);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));

        drawer.addDrawerListener(toggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toggle.syncState();
        if (auth.getCurrentUser() != null)
            getToken();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragmentNav()).commit();
                break;
            case R.id.nav_profile_edit:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new EditProfileFragmentNav()).commit();
                break;
            case R.id.nav_search_friends:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerViewAll,
                        new ContactListFragment()).commit();
                break;
            case R.id.nav_support:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerViewAll,
                        new SupportFragmentNav()).commit();
                break;
            case R.id.nav_share:
                Toast.makeText(this, "here gonna be sharing option", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                signOut();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            LinearLayout rl = (LinearLayout) findViewById(R.id.navLayout);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View vi = inflater.inflate(R.layout.nav_header, null); //log.xml is your file.
            fStore = FirebaseFirestore.getInstance();

            docRef = fStore.collection("users").document(auth.getCurrentUser().getUid());
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    preferenceManager.putString("userName", value.getString("username"));
                    preferenceManager.putString("userEmail", value.getString("email"));
                    preferenceManager.putString("userPhoneNumber", value.getString("phoneNumber"));
                    preferenceManager.putString("userImage", value.getString("userImage"));
                    name.setText(value.getString("username"));
                    email.setText(value.getString("email"));
                    Bitmap bm = getBitmapFromEncodedString(value.getString("userImage"));
                    profileImage.setImageBitmap(bm);
                    if (!firebaseUser.isEmailVerified()) {
                        HomePage.this.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerViewAll,
                                new EmailVerifyFragment()).commit();
                    }
                }
            });
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_team, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.plusId) {
            Intent intent = new Intent(HomePage.this, CreateNewTeam.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        preferenceManager.putString("FCMToken", token);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection("users").document(
                        auth.getCurrentUser().getUid()
                );
        documentReference.update("FCMToken", token)
                .addOnSuccessListener(unused -> showToast("Token updated successfully"))
                .addOnFailureListener(e -> showToast("unable to update token"));

    }


    private void signOut() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection("users").document(FirebaseAuth.getInstance().getUid());
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("FCMToken", FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show();
                    auth.signOut();
                    startActivity(new Intent(HomePage.this, MainActivity.class));
                    finish();
                }).addOnFailureListener(e -> showToast("unable to log out"));
    }


    @Override
    public void onResume() {
        super.onResume();
        notifications = new Notifications(findViewById(R.id.bellInclude),this,toolbar);
        notifications.getNumOfNotifications();
    }

    private Bitmap getBitmapFromEncodedString(String encodedImage){
        if(encodedImage!=null){
            byte[] bytes= Base64.decode(encodedImage,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        }
        return null;
    }
}