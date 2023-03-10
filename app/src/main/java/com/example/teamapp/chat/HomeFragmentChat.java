package com.example.teamapp.chat;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.teamapp.User;
import com.example.teamapp.chat.Adapter.RecentConversionAdapter;
import com.example.teamapp.chat.ChatActivity;
import com.example.teamapp.chat.listeners.ConversionListener;
import com.example.teamapp.chat.models.ChatMessage;
import com.example.teamapp.databinding.FragmentHomeChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class HomeFragmentChat extends Fragment implements ConversionListener {
    private FragmentHomeChatBinding binding;
    private List<ChatMessage> conversions;
    private RecentConversionAdapter conversionAdapter;
    private FirebaseFirestore database;
    private FirebaseAuth auth;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        auth= FirebaseAuth.getInstance();
        binding=FragmentHomeChatBinding.inflate(getActivity().getLayoutInflater());
        init();
        listenConversations();
        View v=binding.getRoot();
        return v;
    }

    private void listenConversations(){
        database.collection("conversions")
                .whereEqualTo("senderId",auth.getCurrentUser().getUid().toString())
                .addSnapshotListener(eventListener);
        database.collection("conversions")
                .whereEqualTo("receiverId",auth.getCurrentUser().getUid().toString())
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener=((value, error) -> {
        if(error!=null){
            System.out.println(error);
            return;
        }
        if(value!=null){
            for(DocumentChange documentChange: value.getDocumentChanges()){
                if(documentChange.getType()==DocumentChange.Type.ADDED){
                   String senderId=documentChange.getDocument().getString("senderId");
                   String receiverId=documentChange.getDocument().getString("receiverId");
                   ChatMessage chatMessage= new ChatMessage();
                   chatMessage.senderId=senderId;
                   chatMessage.receiverId=receiverId;
                   if(auth.getInstance().getCurrentUser().getUid().equals(senderId)){
                       chatMessage.conversionImage=documentChange.getDocument().getString("receiverImage");
                       chatMessage.conversionName=documentChange.getDocument().getString("receiverName");
                       chatMessage.conversionId=documentChange.getDocument().getString("receiverId");
                   }else{
                       chatMessage.conversionImage=documentChange.getDocument().getString("senderImage");
                       chatMessage.conversionName=documentChange.getDocument().getString("senderName");
                       chatMessage.conversionId=documentChange.getDocument().getString("senderId");
                   }
                   chatMessage.message=documentChange.getDocument().getString("lastMessage");
                   chatMessage.dateObject=documentChange.getDocument().getDate("timeStamp");
                   System.out.println("message");
                   System.out.println(chatMessage.message);
                   conversions.add(chatMessage);
                }else if(documentChange.getType()== DocumentChange.Type.MODIFIED){
                    System.out.println("we never got inside the for, you little poop head");
                    for (int i = 0; i < conversions.size(); i++) {
                        String senderId=documentChange.getDocument().getString("senderId");
                        String receiverId=documentChange.getDocument().getString("receiverId");
                        System.out.println("message1");
                        System.out.println(documentChange.getDocument().getString("lastMessage"));
                        System.out.println(conversions.get(i).message);
                        if(conversions.get(i).senderId.equals(senderId) && conversions.get(i).receiverId.equals(receiverId)){
                            conversions.get(i).message=documentChange.getDocument().getString("lastMessage");
                            conversions.get(i).dateObject=documentChange.getDocument().getDate("timeStamp");
                            System.out.println("message");
                            System.out.println(conversions.get(i).message);
                            break;
                        }
                    }
                }
            }
            Collections.sort(conversions,(obj1,obj2)->obj2.dateObject.compareTo(obj1.dateObject));
            conversionAdapter.notifyDataSetChanged();
            binding.conversionRecyclerView.smoothScrollToPosition(0);
            binding.conversionRecyclerView.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
            System.out.println("found recent");
        }
        System.out.println("VALUE NULL?!");
    });



    private void init(){
        conversions=new ArrayList<>();
        conversionAdapter=new RecentConversionAdapter(conversions,this);
        binding.conversionRecyclerView.setAdapter(conversionAdapter);
        database=FirebaseFirestore.getInstance();
    }
    @Override
    public void onConversionClicked(User user){
        Intent intent=new Intent(getContext(), ChatActivity.class);
        intent.putExtra("user",user);
        startActivity(intent);
    }

}