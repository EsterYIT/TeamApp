package com.example.teamapp.gallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.SystemClock;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.teamapp.HomeFragmentNav;
import com.example.teamapp.HomePage;
import com.example.teamapp.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class galleryFragment extends Fragment implements OnPicClickListener {

    public RecyclerView recyclerView;
    ArrayList<Uri> uri = new ArrayList<>();
    ArrayList<Bitmap> galleryList = new ArrayList<>();
    GalleryAdapter galleryAdapter;
    ImageView back;
    TextView title;

    public galleryFragment(GalleryAdapter galleryAdapter) {
        this.galleryAdapter = galleryAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_gallery, container, false);

        recyclerView = v.findViewById(R.id.Recycler_Gallery);
        back = v.findViewById(R.id.backIcon);
        title = v.findViewById(R.id.tool_title);
        title.setText("Gallery");
        galleryList=galleryAdapter.getUriArrayList();
        galleryAdapter.setListener(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4));
        recyclerView.setAdapter(galleryAdapter);
        setListeners();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();

        return v;
    }

    private void setListeners(){
        back.setOnClickListener(v->getActivity().onBackPressed());
    }

    @Override
    public void onClick(int position) {
        System.out.println("mamamia");
        Intent intent = new Intent(getActivity(), ZoomedImage.class);
        intent.putExtra("image",encodeImage(galleryList.get(position)));
        intent.putExtra("pos",position);
        startActivity(intent);
    }

    private String encodeImage(Bitmap bitmap){
        int imageWidth=500;
        int imageHeight=bitmap.getHeight()*imageWidth/bitmap.getWidth();
        Bitmap newBitmap=Bitmap.createScaledBitmap(bitmap,imageWidth,imageHeight,false);
        ByteArrayOutputStream byteArrayOutputStream =new ByteArrayOutputStream();
        newBitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] bytes=byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);

    }

}