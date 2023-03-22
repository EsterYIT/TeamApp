package com.example.teamapp.chat.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamapp.User;
import com.example.teamapp.chat.listeners.ConversionListener;
import com.example.teamapp.chat.models.ChatMessage;
import com.example.teamapp.databinding.ItemRecentConversionBinding;

import java.util.List;

public class RecentConversionAdapter extends RecyclerView.Adapter<RecentConversionAdapter.ConversionViewHolder>{

    private final List<ChatMessage> chatMessages;
    private final ConversionListener conversionListener;

    public RecentConversionAdapter(List<ChatMessage> chatMessages, ConversionListener conversionListener) {
        this.chatMessages = chatMessages;
        this.conversionListener=conversionListener;
    }

    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHolder(
                ItemRecentConversionBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionViewHolder holder, int position) {
        holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder{

        ItemRecentConversionBinding binding;

        ConversionViewHolder(ItemRecentConversionBinding itemRecentConversionBinding){
            super(itemRecentConversionBinding.getRoot());
            binding=itemRecentConversionBinding;
        }

        void setData(ChatMessage chatMessage){
            binding.profileImage.setImageBitmap(getConversionImage(chatMessage.conversionImage));
            binding.nameText.setText(chatMessage.conversionName);
            binding.textRecentMessage.setText(chatMessage.message);
            binding.getRoot().setOnClickListener(v -> {
                User user= new User();
                user.setId(chatMessage.conversionId);
                user.setUsername(chatMessage.conversionName);
                user.setUserImage(chatMessage.conversionImage);
                conversionListener.onConversionClicked(user);
            });
        }
    }

    private Bitmap getConversionImage(String encodedImage){
        byte[] bytes= Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
    }
}
