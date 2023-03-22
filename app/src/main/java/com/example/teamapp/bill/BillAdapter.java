package com.example.teamapp.bill;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamapp.MainActivity;
import com.example.teamapp.R;
import com.example.teamapp.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder>{

    public static final int LEFT_CARD = 0;
    public static final int RIGHT_CARD = 1;

    FirebaseUser firebaseUser;
    BillOnClick onClick;
    Context context;
    ArrayList<User> users;
    Bitmap bitmap;
    int flag = 0;

    public BillAdapter(Context context,ArrayList<User> users,BillOnClick onClick){
        this.context = context;
        this.users = users;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public BillAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == LEFT_CARD) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.bill_card_right, parent, false);
        }else{
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.bill_card_right, parent, false);
        }
        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull BillAdapter.ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        User user = users.get(position);
        bitmap = getBitmapFromEncodedString(user.getUserImage());
        holder.userImage.setImageBitmap(bitmap);
        holder.username.setText(user.getUsername());
        holder.userBill.setText(user.getUserBill());
        holder.userBill.requestFocus();

        if(!users.get(position).getId().equals(firebaseUser.getUid())){
            holder.userBill.setEnabled(false);
            holder.userBill.setInputType(InputType.TYPE_NULL);
            holder.lock.setClickable(false);
        }

        if(users.get(position).getUserBill() != null) {
            if (!users.get(position).getUserBill().equals("0")) {
                holder.lock.setBackgroundResource(R.drawable.ic_lock_24);
                onClick.addToList(position, users.get(position).getId(), holder.userBill.getText().toString());
            }
        }else
            holder.userBill.setText("0");

        holder.userBill.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){
                    holder.lock.setBackgroundResource(R.drawable.ic_baseline_lock_24);
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        EditText userBill;
        ImageView lock,userImage;
        TextView username;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            lock = itemView.findViewById(R.id.lock);
            userBill = itemView.findViewById(R.id.userBill);
            userImage = itemView.findViewById(R.id.userImageBill);
            username = itemView.findViewById(R.id.userNameBill);

                lock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onClick != null) {
                            int pos = getAdapterPosition();
                            if (pos != RecyclerView.NO_POSITION) {
                                onClick.lockOnClick(pos, users.get(pos).getId(), userBill.getText().toString());
                                lock.setBackgroundResource(R.drawable.ic_lock_24);
                            }
                        }
                    }
                });
            }

    }

    public int getItemViewType(int position){
        if(flag == 0) {
            flag = 1;
            return LEFT_CARD;
        }else{
            flag = 0;
            return RIGHT_CARD;
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
