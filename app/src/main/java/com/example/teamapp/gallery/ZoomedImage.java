package com.example.teamapp.gallery;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

import com.example.teamapp.R;

public class ZoomedImage extends AppCompatActivity {

    int position;
    ImageView imageView;
    String imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoomed_image);

        imageView=findViewById(R.id.zoomedImage);

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            position=bundle.getInt("pos");
            imageUri=bundle.getString("image");
        }

//        imageView.setImageURI(Uri.parse(imageUri));
        byte[] bytes = Base64.decode(imageUri, Base64.DEFAULT);

        imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
    }
}