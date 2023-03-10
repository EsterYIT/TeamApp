package com.example.teamapp.carpool_info;

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

public class Carpool_Settings_Fragment extends Fragment {

    DatabaseReference myRef;
    EditText date,hour;
    TextView title;
    ImageView back;
    TextInputEditText teamName,meetingPlace;
    int hour1, minute1;
    boolean car=true;
    boolean map=true;
    boolean chat=true;
    Button carButton,mapButton,chatButton,createTeam;
    Map<String, Object> users;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_carpool__settings_, container, false);

        users= new HashMap<String, Object>();
        date = v.findViewById(R.id.dateId);
        hour = v.findViewById(R.id.hourId);
        back = v.findViewById(R.id.backIcon);
        title = v.findViewById(R.id.tool_title);
        teamName = v.findViewById(R.id.carpoolTeamName);
        meetingPlace = v.findViewById(R.id.placeId);
        createTeam = v.findViewById(R.id.createCarpoolTeam);
        carButton = v.findViewById(R.id.carBtn);
        mapButton = v.findViewById(R.id.mapBtn);
        chatButton = v.findViewById(R.id.chatBtn);
        title.setText("Carpool Settings");

        fillUpUser();

        carButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(car==false){
                    car=true;
                    carButton.setBackgroundResource(R.drawable.user_button);
                }else{
                    car=false;
                    carButton.setBackgroundResource(R.drawable.main_no_color_with_corner);
                }
            }
        });
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(map==false){
                    map=true;
                    mapButton.setBackgroundResource(R.drawable.user_button);
                }else{
                    map=false;
                    mapButton.setBackgroundResource(R.drawable.main_no_color_with_corner);
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

                FirebaseFirestore fireStore=FirebaseFirestore.getInstance();
                DocumentReference currentUserRef=fireStore.collection("users").document(FirebaseAuth.getInstance().getUid());
                List list=new LinkedList();
                String encodedImage=defaultImage();
                list.add(users);
                Map<String, String> hashMap = new HashMap<String, String>();
                Map<String, Boolean> tabs = new HashMap<String, Boolean>();
                tabs.put("carEnabled",car);
                tabs.put("mapEnabled",map);
                tabs.put("chatEnabled",chat);

                hashMap.put("imageID",encodedImage);
                Map<String, Object> hangoutTeam = new HashMap<String, Object>();
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date currentTime = new Date();
                hangoutTeam.put("createdAt", formatter.format(currentTime));
                hangoutTeam.put("lastEdit", formatter.format(currentTime));
                hangoutTeam.put("teamType","carpoolTeam");
                hangoutTeam.put("teamName",teamName.getText().toString());
                hangoutTeam.put("meetingPlace",meetingPlace.getText().toString());
                hangoutTeam.put("date",date.getText().toString());
                hangoutTeam.put("time",hour.getText().toString());
                hangoutTeam.put("tabs",tabs);
                hangoutTeam.put("teamMembers",list);
                hangoutTeam.put("teamImage",hashMap);


                fireStore.collection("Teams").add(hangoutTeam).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
//                        FirebaseFirestore fireStore=FirebaseFirestore.getInstance();
//                        CollectionReference currentUserRef=fireStore.collection("users").document(FirebaseAuth.getInstance().getUid()).collection("Teams");
//                        Map<String, Object> updateMap = new HashMap();
//                        updateMap.put("task.getResult().getId()", task.getResult().getId());
//                        currentUserRef.add(updateMap);
//                        FirebaseFirestore fireStore=FirebaseFirestore.getInstance();
//                        CollectionReference currentUserRef=fireStore.collection("users").document(FirebaseAuth.getInstance().getUid());
//                        DocumentReference FirestoreRef = fireStore.collection("users").document(FirebaseAuth.getInstance().getUid());
//
//                        Map<String, Object> map = new HashMap<>();
//                        map.put("userList", FieldValue.arrayUnion("user_id"));
//                        FirestoreRef.set(docRef, map, SetOptions.merge());
                        FirebaseFirestore fireStore=FirebaseFirestore.getInstance();

                        DocumentReference docRef = fireStore.collection("users").document(FirebaseAuth.getInstance().getUid());
                        docRef.update("teams", FieldValue.arrayUnion(task.getResult().getId()));
                        Intent intent = new Intent(getActivity(), HomePage.class);
                        startActivity(intent);

                    }
                });
                Toast.makeText(getContext(), "New team created", Toast.LENGTH_SHORT).show();
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
                users.put("userLevel", 3);
                users.put("userID", FirebaseAuth.getInstance().getUid());
            }

        });
    }
}