package com.example.teamapp.inviteFriends.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamapp.addContact.ContactModel;
import com.example.teamapp.R;
import com.example.teamapp.inviteFriends.interfaces.inviteInterface;


import java.util.ArrayList;


public class inviteFriendsAdapter extends RecyclerView.Adapter<inviteFriendsAdapter.viewHolder> {

    Context context;
    ArrayList<ContactModel> arrayList;
    Bitmap bitmap;
    inviteInterface inviteInter;

    public inviteFriendsAdapter(inviteInterface anInterface,Context context, ArrayList<ContactModel>allUsers){
        this.inviteInter = anInterface;
        this.context = context;
        this.arrayList = allUsers;
    }

    @NonNull
    @Override
    public inviteFriendsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.invite_friends_card,parent,false);

        return new inviteFriendsAdapter.viewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull inviteFriendsAdapter.viewHolder holder,int position) {

        ContactModel model = arrayList.get(position);
        bitmap = getBitmapFromEncodedString(model.getImage());
        holder.name.setText(model.getName1());
        holder.phone.setText(model.getNumber());
        holder.userImage.setImageBitmap(bitmap);

        if(model.isClicked()){
            holder.invite.setText("invitation sent");
            holder.invite.setBackgroundColor(R.color.design_default_color_on_primary);
            holder.invite.setClickable(false);
        }

//        holder.invite.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(position != RecyclerView.NO_POSITION) {
//                    inviteInter.inviteFriend(position, arrayList.get(position).getId(), holder.invite);
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        TextView name,phone,id;
        ImageView userImage;
        Button invite;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.invite_name);
            phone = itemView.findViewById(R.id.invite_number);
            userImage = itemView.findViewById(R.id.invite_image);
            invite = itemView.findViewById(R.id.inviteBtn);
            id = itemView.findViewById(R.id.invite_id);


            invite.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View view) {

                    if(inviteInter != null){
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION) {
                            inviteInter.inviteFriend(pos, arrayList.get(pos).getId(), invite);
                            invite.setText("invitation sent");
                            invite.setBackgroundColor(R.color.design_default_color_on_primary);
                            invite.setClickable(false);
                        }
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

    public void filterList(ArrayList<ContactModel> filteredList){
        arrayList = filteredList;
        notifyDataSetChanged();
    }
}
