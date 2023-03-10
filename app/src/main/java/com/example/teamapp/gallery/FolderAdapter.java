package com.example.teamapp.gallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamapp.R;
import com.example.teamapp.hangout_info.hangOutTeamAct;

import java.util.ArrayList;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {

    Context context;
    GalleryAdapter adapter;
    ArrayList<Folder> folders;

    public FolderAdapter(Context context,GalleryAdapter galleryAdapter,ArrayList<Folder> uriFolders) {
        this.context = context;
        this.folders = uriFolders;
        for (int i = 0; i < folders.size(); i++) {

        }
        this.adapter = galleryAdapter;

    }


    @NonNull
    @Override
    public FolderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.single_folder,parent,false);
        return new FolderAdapter.ViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull FolderAdapter.ViewHolder holder, int position) {
            Folder f = folders.get(position);
            holder.folder.setImageResource(f.getImageView());
            holder.username.setText(f.getName());
            System.out.println("names");
            System.out.println(f.getName());

    }

    @Override
    public int getItemCount() {
        return folders.size();
        }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView folder;
        TextView username;

        @SuppressLint("ResourceType")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            folder = itemView.findViewById(R.id.imageFolder);
            username = itemView.findViewById(R.id.userName);
            folder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    adapter.setUriArrayList(folder[ids[getAdapterPosition()]]);

                    ((hangOutTeamAct)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView5,
                            new galleryFragment(new GalleryAdapter(context,folders.get(getAdapterPosition()).bitmaps))).addToBackStack(null).commit();
                }
            });
        }
    }
}
