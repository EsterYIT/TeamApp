package com.example.teamapp.gallery;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.collect.BiMap;


import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private ArrayList<Bitmap> uriArrayList;
    Context context;
    OnPicClickListener listener;

    public GalleryAdapter(Context context, ArrayList<Bitmap> uriArrayList) {
        this.context = context;
        this.uriArrayList = uriArrayList;
    }

    public ArrayList<Bitmap> getUriArrayList(){
        return uriArrayList;
    }

    @NonNull
    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.single_image,parent,false);
        return new ViewHolder(view);
    }

    public void setUriArrayList(ArrayList<Bitmap> uriArrayList) {
        this.uriArrayList = uriArrayList;
    }

    public void setListener(OnPicClickListener listener){
        this.listener=listener;
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryAdapter.ViewHolder holder, int position) {

        holder.imageView.setImageBitmap(uriArrayList.get(position));
//        holder.imageView.setImageURI(uriArrayList.get(position));


    }

    @Override
    public int getItemCount() {
        return uriArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(getAdapterPosition());
                }
            });
        }
    }
}
