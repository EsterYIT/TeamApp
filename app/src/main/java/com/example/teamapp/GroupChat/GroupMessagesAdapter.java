package com.example.teamapp.GroupChat;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamapp.R;
import com.example.teamapp.chat.Adapter.ChatAdapter;
import com.example.teamapp.chat.models.ChatMessage;
import com.example.teamapp.databinding.ItemContainerReceivedMessageBinding;
import com.example.teamapp.databinding.ItemContainerSentMessageBinding;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class GroupMessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context context;
    List<GroupMessage> messages;
    DatabaseReference messagesDB;
    private final String senderId;
    public static final int VIEW_TYPE_SENT=1;
    public static final int VIEW_TYPE_RECEIVED=2;

    public GroupMessagesAdapter(Context context, List<GroupMessage> messages, DatabaseReference messagesDB,String senderId) {
        this.context = context;
        this.messages = messages;
        this.messagesDB = messagesDB;
        this.senderId=senderId;
        System.out.println("messagesize "+messages.size());

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==VIEW_TYPE_SENT){
            return new GroupMessagesAdapter.SentMessageViewHolder(ItemContainerSentMessageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        }else{
            return new GroupMessagesAdapter.ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        System.out.println("bananasNANAS");
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((GroupMessagesAdapter.SentMessageViewHolder) holder).setData(messages.get(position));
        } else {
            ((GroupMessagesAdapter.ReceivedMessageViewHolder) holder).setData(messages.get(position));
        }

    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    public int getItemViewType(int position){
        if(messages.get(position).senderId.equals(senderId)){
            return VIEW_TYPE_SENT;
        }else{
            return VIEW_TYPE_RECEIVED;
        }
    }

//    public class GroupMessagesAdapterViewHolder  extends RecyclerView.ViewHolder{
//
//        TextView tvTitle;
//
//        public GroupMessagesAdapterViewHolder(@NonNull View itemView) {
//            super(itemView);
//            tvTitle=(TextView) itemView.findViewById(R.id.textMessage);
//        }
//    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerSentMessageBinding binding;

        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding){
            super(itemContainerSentMessageBinding.getRoot());
            binding=itemContainerSentMessageBinding;
        }

        void setData(GroupMessage groupMessage){
            binding.textMessage.setText(groupMessage.getMessage());
            binding.textDateTime.setText(groupMessage.readableDateTime);
        }
    }


    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder{

        private final ItemContainerReceivedMessageBinding binding;

        ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding){
            super(itemContainerReceivedMessageBinding.getRoot());
            binding=itemContainerReceivedMessageBinding;
        }

        void setData(GroupMessage groupMessage){
            binding.textMessage.setText(groupMessage.getMessage());
            binding.textDateTime.setText(groupMessage.readableDateTime);
        }
    }
}
