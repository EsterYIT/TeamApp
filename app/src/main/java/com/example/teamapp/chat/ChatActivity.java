package com.example.teamapp.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.teamapp.utils.Constants;
import com.example.teamapp.R;
import com.example.teamapp.User;
import com.example.teamapp.activities.BaseActivity;
import com.example.teamapp.chat.Adapter.ChatAdapter;
import com.example.teamapp.chat.models.ChatMessage;
import com.example.teamapp.databinding.ActivityChatBinding;
import com.example.teamapp.network.ApiClient;
import com.example.teamapp.network.ApiService;
import com.example.teamapp.userDetailsFragment;
import com.example.teamapp.utils.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends BaseActivity implements PopupMenu.OnMenuItemClickListener {
    private ActivityChatBinding binding;
    private User receiverUser;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private FirebaseFirestore database;
    FirebaseDatabase realDatabase;
    FirebaseAuth auth;
    String id;
    private String conversionId=null;
    HashMap<String,Object> conversion;
    PreferenceManager preferenceManager;
    private Boolean isReceiverAvailable=false;
    long time;
    String conversions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager= new PreferenceManager(getApplicationContext());
        binding=ActivityChatBinding.inflate(getLayoutInflater());
        binding.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
            }
        });

        setContentView(binding.getRoot());
        setListeners();
        loadReceiverDetails();
        init();
        listenMessages();
    }

    private void init(){
        auth=FirebaseAuth.getInstance();
        id=FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        chatMessages=new ArrayList<>();
        chatAdapter=new ChatAdapter(chatMessages,getBitmapFromEncodedString(receiverUser.getUserImage()),
                id);
        binding.chatRecyclerView.setAdapter(chatAdapter);
        database=FirebaseFirestore.getInstance();
    }

    private void sendMessage(){
        HashMap<String ,Object> message=new HashMap<>();
        message.put("senderId",id);
        message.put("receiverId",receiverUser.getId());
        message.put("message",binding.inputMessage.getText().toString());
        message.put("timeStamp",new Date());
        message.put("timeLong", System.currentTimeMillis());
        database.collection("chat").add(message);
        if(conversionId!=null){
            updateConversion(binding.inputMessage.getText().toString());
        }else{
            conversion = new HashMap<>();
            conversion.put("senderId",id);
            conversion.put("senderName",preferenceManager.getString("userName"));
            conversion.put("senderImage",preferenceManager.getString("userImage"));
            conversion.put("receiverId",receiverUser.getId());
            conversion.put("receiverName",receiverUser.getUsername());
            conversion.put("receiverImage",receiverUser.getUserImage());
            conversion.put("lastMessage",binding.inputMessage.getText().toString());
            conversion.put("timeStamp",new Date());
            conversion.put(id,0);
            conversion.put(receiverUser.getId(),0);
            addConversion(conversion);
        }
        if(!isReceiverAvailable){
            try {
                JSONArray tokens= new JSONArray();
                tokens.put(receiverUser.getToken());

                JSONObject data=new JSONObject();
                data.put("userId",preferenceManager.getString("currentUserId"));
                data.put("username",preferenceManager.getString("userName"));
                data.put("FCMToken",preferenceManager.getString("FCMToken"));
                data.put("message",binding.inputMessage.getText().toString());
                JSONObject body=new JSONObject();
                body.put(Constants.REMOTE_MSG_DATA,data);
                body.put(Constants.REMOTE_MSG_REGISTRATION_IDS,tokens);

                sendNotification(body.toString());

            }catch (Exception exception){
                showToast(exception.getMessage());
            }
        }
        binding.inputMessage.setText(null);
    }


    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    private void sendNotification(String messageBody){
        ApiClient.getClient().create(ApiService.class).sendMessage(
                Constants.getRemoteMsgHeaders(),
                messageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call,@NonNull Response<String> response) {
            if(response.isSuccessful()){
                try {
                    if(response.body()!=null){
                        JSONObject responseJson=new JSONObject(response.body());
                        JSONArray results = responseJson.getJSONArray("results");
                        if(responseJson.getInt("failure")==1){
                            JSONObject error=(JSONObject) results.get(0);
                            showToast(error.getString("error"));
                        }
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                showToast("Notification sent successfully");
            }else{
                showToast("Error: "+ response.code());
            }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                showToast(t.getMessage());
            }
        });
    }

    private void listenAvailabilityOfReceiver(){
        database.collection("users").document(
                receiverUser.getId()
        ).addSnapshotListener(ChatActivity.this,(value,error)->{
            if(error!=null){
                return;
            }
            if(value!=null){
                if(value.getLong("availability")!=null){
                    int availability= Objects.requireNonNull(
                            value.getLong("availability")
                    ).intValue();
                    isReceiverAvailable=availability==1;
                }
                receiverUser.setToken(value.getString("FCMToken"));
                if(receiverUser.getUserImage()==null){
                    receiverUser.setUserImage("image");
                    chatAdapter.setReceiverProfileImage(getBitmapFromEncodedString(receiverUser.getUserImage()));
                    chatAdapter.notifyItemRangeChanged(0,chatMessages.size());
                }
            }
            if(isReceiverAvailable){
                binding.textAvailability.setVisibility(View.VISIBLE);
            }else{
                binding.textAvailability.setVisibility(View.GONE);
            }

        });
    }

    private void listenMessages(){
        Date date = new Date(122,8,7,11,27);
        System.out.println("date ======= " + date);
        long time = date.getTime();
        System.out.println("date ======= " + date.getTime());

        System.out.println("time=======" + time);
        database.collection("chat")
                .whereEqualTo("senderId",id)
                .whereEqualTo("receiverId",receiverUser.getId())
                .addSnapshotListener(eventListener);
        database.collection("chat")
                .whereEqualTo("senderId",receiverUser.getId())
                .whereEqualTo("receiverId",id)
                .addSnapshotListener(eventListener);
    }


//firestore shit need to replace.
    private final EventListener<QuerySnapshot> eventListener=(value, error)-> {
        if (error != null) {
            return;
        }
        if (value != null) {
            int count = chatMessages.size();
            time = 0;
            System.out.println("chkalaka "+conversions);

            if(conversions != null) {
                DocumentReference doc = database.collection("conversions").document(conversions);
                doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        time = value.getLong(id);
                    }
                });
            }

            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if(documentChange.getType()== DocumentChange.Type.ADDED && documentChange.getDocument().get("timeLong",
                        long.class) > time){
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId=documentChange.getDocument().getString("senderId");
                    chatMessage.receiverId=documentChange.getDocument().getString("receiverId");
                    chatMessage.message=documentChange.getDocument().getString("message");
                    chatMessage.dateTime= getReadableDateTime(documentChange.getDocument().getDate("timeStamp"));
                    chatMessage.dateObject=documentChange.getDocument().getDate("timeStamp");
                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages,(obj1,obj2)-> obj1.dateObject.compareTo(obj2.dateObject));
            if(count==0){
                chatAdapter.notifyDataSetChanged();
            }else{
                chatAdapter.notifyItemRangeInserted(chatMessages.size(),chatMessages.size());
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size()-1);
            }
            binding.chatRecyclerView.setVisibility(View.VISIBLE);
        }
        binding.progressBar.setVisibility(View.GONE);
        if(conversionId==null){
            checkForConversion();
        }
    };

    private Bitmap getBitmapFromEncodedString(String encodedImage){
        if(encodedImage!=null){
        byte[] bytes= Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        }
        return null;
    }
    private void loadReceiverDetails(){
        receiverUser=(User)getIntent().getSerializableExtra("user");
        binding.nameText.setText(receiverUser.getUsername());
    }

    private void setListeners(){
        binding.back.setOnClickListener(v->onBackPressed());
        binding.layoutSend.setOnClickListener(v->sendMessage());
    }

    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }


    private  void addConversion(HashMap<String,Object> conversion){
        database.collection("conversions")
                .add(conversion)
                .addOnSuccessListener(documentReference -> conversionId= documentReference.getId());
    }


    private void updateConversion(String message){
        DocumentReference documentReference=
                database.collection("conversions").document(conversionId);
        documentReference.update("lastMessage",message,"timeStamp",new Date());
    }

    private void checkForConversion(){
        if(chatMessages.size()!=0){
            checkForConversionRemotely(
                    id,receiverUser.getId()
            );
            checkForConversionRemotely(
                    receiverUser.getId(),
                    id
            );
        }
    }


    private void checkForConversionRemotely(String senderId,String receiverId){
        database.collection("conversions").
                whereEqualTo("senderId",senderId)
                .whereEqualTo("receiverId",receiverId)
                .get()
                .addOnCompleteListener(conversionCompleteListener);
    }

    private final OnCompleteListener<QuerySnapshot> conversionCompleteListener=task -> {
      if(task.isSuccessful()&& task.getResult()!=null && task.getResult().getDocuments().size()>0){
          DocumentSnapshot documentSnapshot =task.getResult().getDocuments().get(0);
          conversionId = documentSnapshot.getId().toString();
      }
    };

    protected void onResume(){
        super.onResume();
        listenAvailabilityOfReceiver();
        conversionIdGetter();
    }
    public void showPopup(View v){
        PopupMenu popupMenu = new PopupMenu(this,v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.user_info);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.userDetails:
                userDetailsFragment userDetails = new userDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("username",receiverUser.getUsername());
                bundle.putString("phoneNumber", receiverUser.getPhoneNumber());
                bundle.putString("image",receiverUser.getUserImage());
                bundle.putString("userId",receiverUser.getId());

                userDetails.setArguments(bundle);
                this.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView6,
                        userDetails).commit();
                return true;
            default:
                return false;
        }
    }

    private synchronized void conversionIdGetter(){
        System.out.println("receiver id = " + receiverUser.getId());
        System.out.println("id = " + id);

        database.collection("conversions").whereEqualTo("senderId",id).whereEqualTo("receiverId",receiverUser.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size()>0) {
                    System.out.println("======" + queryDocumentSnapshots.getDocuments().get(0).getId());
                    conversions = queryDocumentSnapshots.getDocuments().get(0).getId().toString();
                    System.out.println("======/" + conversions);
                }
            }
        });
        if(conversions!=null) {
            return;
        }
        System.out.println("converrrrrrrrr = " + conversions);

        database.collection("conversions").whereEqualTo("senderId",receiverUser.getId()).whereEqualTo("receiverId",id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size()>0) {
                    System.out.println("======" + queryDocumentSnapshots.getDocuments().get(0).getId());
                    conversions = queryDocumentSnapshots.getDocuments().get(0).getId().toString();
                    System.out.println("======/" + conversions);
                }
            }
        });
        if(conversions!=null) {
            return;
        }
        System.out.println("converrrrrrrrr = " + conversions);
    }
}