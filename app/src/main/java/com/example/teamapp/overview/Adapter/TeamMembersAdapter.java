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
import com.example.teamapp.User;
import com.example.teamapp.overview.intefaces.RecyclerViewInterface;

import java.util.ArrayList;

public class TeamMembersAdapter extends RecyclerView.Adapter<TeamMembersAdapter.viewHolder>{

    private final RecyclerViewInterface recyclerViewInterface;
    Context context;
    ArrayList<User>usersList;
    Bitmap bitmap;

    public TeamMembersAdapter(Context context,ArrayList<User>usersList,RecyclerViewInterface recyclerViewInterface){
        this.context = context;
        this.usersList = usersList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.team_members_card,parent,false);
        return new TeamMembersAdapter.viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamMembersAdapter.viewHolder holder, int position) {

        User user = usersList.get(position);
        bitmap = getBitmapFromEncodedString(user.getUserImage());
        holder.teamImage.setImageBitmap(bitmap);
        holder.userName.setText(user.getUsername());
        holder.userNumber.setText(user.getPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        ImageView teamImage;
        TextView userName, userNumber;
        CardView teamMemberCard;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            teamImage = itemView.findViewById(R.id.userMemberImage);
            userName = itemView.findViewById(R.id.userMember);
            userNumber = itemView.findViewById(R.id.userMemberPhone);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface != null){
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION)
                            recyclerViewInterface.onItemClicked(pos,usersList.get(pos).getUsername(),
                                    usersList.get(pos).getUserImage(),usersList.get(pos).getPhoneNumber(),usersList.get(pos).getId());
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

    public void filterList(ArrayList<User> filteredList){
        usersList = filteredList;
        notifyDataSetChanged();
    }
}
