package com.example.teamapp.overview.Adapter;

import android.content.Context;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamapp.R;
import com.example.teamapp.overview.intefaces.SameGroupsInterface;
import com.example.teamapp.Team;

import java.util.ArrayList;


public class SameGroupsAdapter extends RecyclerView.Adapter<SameGroupsAdapter.viewHolder>{
    private final SameGroupsInterface sameGroupsInterface;
    Context context;
    ArrayList<Team>teams;
    Bitmap bitmap;


    public SameGroupsAdapter(Context context,ArrayList<Team>teams,SameGroupsInterface sameGroupsInterface){
        this.context = context;
        this.teams = teams;
        this.sameGroupsInterface = sameGroupsInterface;
    }
    @NonNull
    @Override
    public SameGroupsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.same_groups_card,parent,false);
        return new SameGroupsAdapter.viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SameGroupsAdapter.viewHolder holder, int position) {

        Team team = teams.get(position);
        bitmap = getBitmapFromEncodedString(team.getTeamImage());
        holder.teamImage.setImageBitmap(bitmap);
        holder.teamName.setText(team.getName());
        holder.teamId.setText(team.getTeamId());
    }

    @Override
    public int getItemCount() {
        return teams.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        ImageView teamImage;
        TextView teamName,teamId;
        CardView teamCard;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            teamCard = itemView.findViewById(R.id.sameGroupsCard);
            teamName = itemView.findViewById(R.id.sameTeamName);
            teamImage = itemView.findViewById(R.id.sameTeamImage);
            teamId = itemView.findViewById(R.id.teamId);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(sameGroupsInterface != null){
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION)
                            sameGroupsInterface.onItemClicked(pos,teams.get(pos).getTeamId(),teams.get(pos).getName());
                    }
                }
            });
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
