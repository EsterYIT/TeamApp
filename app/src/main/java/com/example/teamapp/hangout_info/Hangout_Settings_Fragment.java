package com.example.teamapp.hangout_info;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.teamapp.CreateNewTeam;
import com.example.teamapp.HomePage;
import com.example.teamapp.PagerAdapter;
import com.example.teamapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Hangout_Settings_Fragment extends Fragment {

    DatabaseReference myRef;
    PagerAdapter pagerAdapter;
    EditText date,hour;
    TextView title;
    ImageView back;
    TextInputEditText teamName,meetingPlace;
    String teamNameStr;
    boolean album=true;
    boolean menu=true;
    boolean chat=true;
    Button albumButton,menuButton,chatButton,createTeam;
    int hour1, minute1;
    Map<String, Object> users;
    String id;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hangout_settings, container, false);
        users= new HashMap<String, Object>();

        date = v.findViewById(R.id.dateId);
        hour = v.findViewById(R.id.hourId);
        back = v.findViewById(R.id.backIcon);
        title = v.findViewById(R.id.tool_title);
        teamName = v.findViewById(R.id.hangoutTeamName);
        meetingPlace=v.findViewById(R.id.meetingPlace);
        createTeam = v.findViewById(R.id.createHangoutTeam);
        title.setText("Hangout Settings");
        albumButton=v.findViewById(R.id.albumButton);
        menuButton=v.findViewById(R.id.menuButton);
        chatButton=v.findViewById(R.id.chatButton);
        fillUpUser();

        albumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(album==false){
                    album=true;
                    albumButton.setBackgroundResource(R.drawable.user_button);
                }else{
                    album=false;
                    albumButton.setBackgroundResource(R.drawable.main_no_color_with_corner);
                }
            }
        });
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(menu==false){
                    menu=true;
                    menuButton.setBackgroundResource(R.drawable.user_button);
                }else{
                    menu=false;
                    menuButton.setBackgroundResource(R.drawable.main_no_color_with_corner);
                }
            }
        });
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chat==false){
                    chat=true;
                    chatButton.setBackgroundResource(R.drawable.user_button);
                }else{
                    chat=false;
                    chatButton.setBackgroundResource(R.drawable.main_no_color_with_corner);
                }
            }
        });
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        createTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("gallery", album);

                FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
                DocumentReference currentUserRef = fireStore.collection("users").document(FirebaseAuth.getInstance().getUid());
                List list = new LinkedList();
                String encodedImage = defaultImage();
                list.add(users);
                Map<String, String> map = new HashMap<String, String>();
                Map<String, Boolean> tabs = new HashMap<String, Boolean>();
                tabs.put("albumEnabled", album);
                tabs.put("menuEnabled", menu);
                tabs.put("chatEnabled", chat);

                map.put("imageID", encodedImage);
                Map<String, Object> hangoutTeam = new HashMap<String, Object>();
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date currentTime = new Date();
                hangoutTeam.put("createdAt", formatter.format(currentTime));
                hangoutTeam.put("lastEdit", formatter.format(currentTime));
                hangoutTeam.put("teamType", "hangoutTeam");
                hangoutTeam.put("teamName", teamName.getText().toString());
                hangoutTeam.put("meetingPlace", meetingPlace.getText().toString());
                hangoutTeam.put("date", date.getText().toString());
                hangoutTeam.put("time", hour.getText().toString());
                hangoutTeam.put("tabs", tabs);
                hangoutTeam.put("teamMembers", list);
                hangoutTeam.put("teamImage", map);


                if (date.getText().toString().equals("")) {
                    date.setError("Date must not be empty");
                } else {
                    fireStore.collection("Teams").add(hangoutTeam).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {

                            FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
                            DocumentReference docRef = fireStore.collection("users").document(FirebaseAuth.getInstance().getUid());
                            docRef.update("teams", FieldValue.arrayUnion(task.getResult().getId()));
                            Intent intent = new Intent(getActivity(), HomePage.class);
                            startActivity(intent);

                        }
                    });
                    Toast.makeText(getContext(), "New team created", Toast.LENGTH_SHORT).show();
                }
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreateNewTeam.class);
                startActivity(intent);
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month+1;
                        String date2 = day+"/"+month+"/"+year;
                        date.setText(date2);
                    }
                },year,month,day);
                datePickerDialog.getDatePicker();
                datePickerDialog.show();
            }
        });

        hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getActivity(), android.R.style.Theme_Holo_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                                hour1 = hourOfDay;
                                minute1 = minute;
                                String time = hour1+":"+minute1;
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat f24Hours = new SimpleDateFormat(
                                        "HH:mm"
                                );
                                try{
                                    Date date = f24Hours.parse(time);
                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat f12Hours = new SimpleDateFormat(
                                            "hh:mm aa"
                                    );
                                    hour.setText(f12Hours.format(date));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        },12,0,false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(hour1, minute1);
                timePickerDialog.show();
            }
        });

        return v;
    }

    public void fillor(){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference Reference = db.collection("users").document(FirebaseAuth.getInstance().getUid());

        Reference.get().addOnCompleteListener(

                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        users.put("name","test");
                    }
                });

    }

    private String defaultImage(){
        Bitmap bm = BitmapFactory.decodeResource(getResources(),R.drawable.lets_teamapp_icon);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
        byte[] byteArrayImage = baos.toByteArray();
        return Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
    }

    public synchronized void  fillUpUser(){
        FirebaseFirestore fireStore=FirebaseFirestore.getInstance();
        DocumentReference currentUserRef=fireStore.collection("users").document(FirebaseAuth.getInstance().getUid());
        currentUserRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                users.put("userLevel", 1);
                users.put("userID", FirebaseAuth.getInstance().getUid());
            }
        });
    }
}