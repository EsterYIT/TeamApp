package com.example.teamapp.chat.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamapp.User;
import com.example.teamapp.chat.listeners.ChatUserListener;
import com.example.teamapp.databinding.ChatUserItemBinding;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import listeners.UserListener;

public class ChatUsersAdapter extends RecyclerView.Adapter<ChatUsersAdapter.ChatUserViewHolder>{

    private final List<User> chatUsers;
    private final ChatUserListener userListener;

    public ChatUsersAdapter(List<User> chatUsers, ChatUserListener userListener){
        this.chatUsers=chatUsers;
        this.userListener=  userListener;
    }

    @NonNull
    @Override
    public ChatUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ChatUserItemBinding chatUserItemBinding= ChatUserItemBinding.inflate(
                LayoutInflater.from(parent.getContext()
                ), parent, false);

        return new ChatUserViewHolder(chatUserItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatUserViewHolder holder, int position) {
        holder.setChatUserData(chatUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return chatUsers.size();
    }

    class ChatUserViewHolder extends RecyclerView.ViewHolder{

        ChatUserItemBinding binding;

        ChatUserViewHolder(ChatUserItemBinding chatUserItemBinding){
            super(chatUserItemBinding.getRoot());
            binding=chatUserItemBinding;
        }

        void setChatUserData(User user){
            binding.nameText.setText(user.getUsername());
            binding.emailText.setText(user.getEmail());
            binding.profileImage.setImageBitmap(getUserImage(user.getUserImage()));
            binding.getRoot().setOnClickListener(v->userListener.onUserClicked(user));
        }
    }

    private Bitmap getUserImage(String encodedImage){
        byte[] bytes= Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}
