package com.example.teamapp.hangout_info;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.teamapp.HomePage;
import com.example.teamapp.R;
import com.example.teamapp.hangout_info.Hangout_Settings_Fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Update_Hangout_Settings_Fragment extends Fragment {

    Calendar calendar = Calendar.getInstance();
    final int year = calendar.get(Calendar.YEAR);
    final int month = calendar.get(Calendar.MONTH);
    final int day = calendar.get(Calendar.DAY_OF_MONTH);

    DocumentReference documentReference;
    TextInputEditText name,place;
    EditText date,time;
    Button albumButton,menuButton,chatButton,update;
    String nameStr,placeStr,dateStr,timeStr,teamId;
    boolean album,chat,bill;
    int hour1, minute1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_update__hangout__settings_, container, false);

        date = v.findViewById(R.id.dateId);
        time = v.findViewById(R.id.hourId);
        name = v.findViewById(R.id.hangoutTeamName);
        place=v.findViewById(R.id.meetingPlace);
        update = v.findViewById(R.id.updateHangoutSettings);
        albumButton=v.findViewById(R.id.albumButton);
        menuButton=v.findViewById(R.id.menuButton);
        chatButton=v.findViewById(R.id.chatButton);

        teamId = getArguments().getString("teamID");
        nameStr = getArguments().getString("teamName");
        placeStr = getArguments().getString("place");
        dateStr = getArguments().getString("date");
        timeStr = getArguments().getString("time");
        album = getArguments().getBoolean("album");
        chat = getArguments().getBoolean("chat");
        bill = getArguments().getBoolean("bill");

        name.setText(nameStr);
        place.setText(placeStr);
        date.setText(dateStr);
        time.setText(timeStr);

        if(!album)
            albumButton.setBackgroundResource(R.drawable.main_no_color_with_corner);
        if(!chat)
            chatButton.setBackgroundResource(R.drawable.main_no_color_with_corner);
        if(!bill)
            menuButton.setBackgroundResource(R.drawable.main_no_color_with_corner);

        albumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!album){
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
                if(!bill){
                    bill=true;
                    menuButton.setBackgroundResource(R.drawable.user_button);
                }else{
                    bill=false;
                    menuButton.setBackgroundResource(R.drawable.main_no_color_with_corner);
                }
            }
        });
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!chat){
                    chat=true;
                    chatButton.setBackgroundResource(R.drawable.user_button);
                }else{
                    chat=false;
                    chatButton.setBackgroundResource(R.drawable.main_no_color_with_corner);
                }
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("ffffffffffffff " + date);
                FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                documentReference = fStore.collection("Teams").document(teamId);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(!name.getText().toString().equals(nameStr))
                            documentReference.update("teamName",name.getText().toString());
                        if(!place.getText().toString().equals(placeStr))
                            documentReference.update("meetingPlace",place.getText().toString());
                        if(!date.getText().toString().equals(dateStr))
                            documentReference.update("date",date.getText().toString());
                        if(!time.getText().toString().equals(timeStr))

                            documentReference.update("time",time.getText().toString());
                            HashMap<String, Boolean> tabs = new HashMap<String, Boolean>();
                            tabs.put("albumEnabled",album);
                            tabs.put("chatEnabled",chat);
                            tabs.put("menuEnabled",bill);
                            documentReference.update("tabs",tabs);

                        Toast.makeText(getContext(), "Settings updated", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), HomePage.class);
                            startActivity(intent);
                    }
                });
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date currentTime = new Date();
                documentReference = fStore.collection("Teams").document(teamId);
                documentReference.update("lastEdit",formatter.format(currentTime));
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

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getActivity(), android.R.style.Theme_Holo_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                                hour1 = hourOfDay;
                                minute1 = minute;
                                String time1 = hour1+":"+minute1;
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat f24Hours = new SimpleDateFormat(
                                        "HH:mm"
                                );
                                try{
                                    Date date = f24Hours.parse(time1);
                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat f12Hours = new SimpleDateFormat(
                                            "hh:mm aa"
                                    );
                                    time.setText(f12Hours.format(date));
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
}