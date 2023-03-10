package com.example.teamapp.addContact;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamapp.R;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private final AddContact addContact;
    Activity activity;
    ArrayList<ContactModel> arrayList;
    Bitmap bitmap;

    public SearchAdapter(AddContact addContact, Activity activity, ArrayList<ContactModel> arrayList){
        this.addContact = addContact;
        this.activity = activity;
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_search_for_friends,parent,false);

        return new ViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {

        ContactModel model = arrayList.get(position);
        bitmap = getBitmapFromEncodedString(model.getImage());
        holder.tvName.setText(model.getName1());
        holder.tvNumber.setText(model.getNumber());
        holder.userImage.setImageBitmap(bitmap);
       // holder.addCon.setImageResource(R.drawable.ic_add_person_24);

        if(contactNumberExist(activity,model.getNumber())){
            holder.addCon.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName,tvNumber;
        ImageView userImage,addCon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvNumber = itemView.findViewById(R.id.tv_number);
            userImage = itemView.findViewById(R.id.iv_image);
            addCon = itemView.findViewById(R.id.addContact);

            addCon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(addContact != null){
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION)
                            addContact.addUserToContacts(pos,arrayList.get(pos).getName1(),arrayList.get(pos).getNumber());
                    }
                }
            });
        }
    }

    public void filterList(ArrayList<ContactModel> filteredList){
        arrayList = filteredList;
        notifyDataSetChanged();
    }

    private Bitmap getBitmapFromEncodedString(String encodedImage){
        if(encodedImage!=null){
            byte[] bytes= Base64.decode(encodedImage,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        }
        return null;
    }

    @SuppressLint("Range")
    private boolean contactNumberExist(Context context, String number) {

        boolean exist = false;

        String[] projection = new String[] {
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup._ID};

        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor cursor = context.getContentResolver().query(contactUri, projection, null, null, null);

        if(cursor != null) {
            if (cursor.moveToFirst()) {
                exist = true;
            }
            cursor.close();
        }
        return exist;
    }
}
