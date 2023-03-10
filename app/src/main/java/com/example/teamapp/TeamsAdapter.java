package com.example.teamapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamapp.carpool_info.carpoolTeamAct;
import com.example.teamapp.hangout_info.hangOutTeamAct;

import java.util.ArrayList;

public class TeamsAdapter extends RecyclerView.Adapter<TeamsAdapter.viewHolder> {

    ArrayList<Team> teams;
    Context context;
    Bitmap bitmap;

    public TeamsAdapter(Context context, ArrayList<Team> teams){
        this.context=context;
        this.teams=teams;
    }
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.team_card,parent,false);
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, @SuppressLint("RecyclerView") int position) {

        Team team=teams.get(position);
        bitmap = getBitmapFromEncodedString(team.getTeamImage());
        holder.image.setImageBitmap(bitmap);
        holder.date.setText(team.getDateStr());
        holder.teamName.setText(team.getName());
        holder.teamType.setText(team.getDescription());

                holder.teamCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ContextCompat.checkSelfPermission(context
                                , Manifest.permission.READ_CONTACTS)
                                == PackageManager.PERMISSION_GRANTED) {

                            if (teams.get(position).getDescription().equals("hangoutTeam")) {
                                Intent intent = new Intent(context, hangOutTeamAct.class);
                                intent.putExtra("teamName", team.getName());
                                intent.putExtra("teamID", team.getTeamId());
                                intent.putExtra("album", team.isAlbum());
                                intent.putExtra("chat", team.isChat());
                                intent.putExtra("bill", team.isBill());
                                context.startActivity(intent);
                            } else {
                                Intent intent = new Intent(context, carpoolTeamAct.class);
                                intent.putExtra("teamName", team.getName());
                                intent.putExtra("teamID", team.getTeamId());
                                context.startActivity(intent);
                            }
                        }
                        else{
                               ActivityCompat.requestPermissions((Activity) context
                                    , new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        }
                    }
                });

    }


    @Override
    public int getItemCount() {
        return teams.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        TextView teamName,date,teamType;
        ImageView image;
        CardView teamCard;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageView2);
            date=itemView.findViewById(R.id.meetingDate);
            teamName=itemView.findViewById(R.id.teamNameCon);
            teamType=itemView.findViewById(R.id.teamType);
            teamCard=itemView.findViewById(R.id.teamCard);
        }
    }
    private Bitmap getBitmapFromEncodedString(String encodedImage){
        if(encodedImage!=null){
            byte[] bytes= Base64.decode(encodedImage,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        }
        return null;
    }

}
