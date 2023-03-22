package com.example.teamapp.gallery;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.teamapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import javax.annotation.Nullable;

public class hangoutGalleryFragment extends Fragment {

    RecyclerView recyclerView;
    ImageView camera;
    galleryFragment testGallery;
    FirebaseAuth auth;
    FirebaseUser user;
    StorageReference storageRef;
    String name = "",teamID;
    ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
    ArrayList<Bitmap> bitmaps2 = new ArrayList<Bitmap>();

    ArrayList<Uri> uri = new ArrayList<>();
    ArrayList<Uri> uri1 = new ArrayList<>();
    HashMap<Object,ArrayList> usersId = new HashMap<>();
    ArrayList<String> usersImages = new ArrayList<>();
    ArrayList<String> teamMemberIds = new ArrayList<>();
    HashMap<String,String> teamMemberNames = new HashMap<>();
    HashMap<String,ArrayList<Bitmap>> teamMemberImages = new HashMap<>();
    ArrayList<Folder> folders = new ArrayList<>();
    ArrayList<String> images = new ArrayList<>();
    ArrayList<HashMap<String,String>> teamMembers;
    FolderAdapter folderAdapter;
    FirebaseFirestore fStore;
    FirebaseDatabase realTime;
    DocumentReference documentRef;
    DocumentReference documentRef2;
    DocumentReference documentRef3;
    boolean exist = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hangout_gallery, container, false);
        realTime = FirebaseDatabase.getInstance();
        realTime.getReference("gallary/"+teamID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(user.getUid()).getValue() != null){
                    images=(ArrayList<String>)snapshot.child(user.getUid()).getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        teamID = getArguments().getString("teamID");
        usersImages = new ArrayList<>();
        recyclerView = v.findViewById(R.id.Recycler_Folder);
        camera = v.findViewById(R.id.camera);
        recyclerView.setHasFixedSize(true);
        fStore=FirebaseFirestore.getInstance();
        testGallery = new galleryFragment(new GalleryAdapter(getContext(),bitmaps));
        folderAdapter = new FolderAdapter(getContext(),new GalleryAdapter(getContext(),bitmaps), folders);
        test();
        folderFetcher();
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4));
        recyclerView.setAdapter(folderAdapter);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        DocumentReference documentReference = fStore.collection("users").document(auth.getCurrentUser().getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable DocumentSnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {
                name = (value.getString("username").toString());
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"select picture"),1);
            }
        });
        return v;
    }


    public ArrayList<Bitmap> StringsToBitmaps(ArrayList<String> string ){
        ArrayList<Bitmap> bitmaped=new ArrayList<>();
        for (int i = 0; i < string.size(); i++) {
            byte[] bytes = Base64.decode(string.get(i), Base64.DEFAULT);
            bitmaped.add(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
        }
        return bitmaped;
    }

    private void uploadedPic(Uri uploadImage) {
        final String randomKey = UUID.randomUUID().toString();
        StorageReference imageRef = storageRef.child(teamID).child(user.getUid()).child(randomKey);
        imageRef.putFile(uploadImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Toast.makeText(getContext(),"uploaded",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode,data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            if(data.getClipData() != null) {
                int x = data.getClipData().getItemCount();
                if (uri1.size() == 0){
                    for (int i = 0; i < x; i++) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getClipData().getItemAt(i).getUri());
                            bitmaps.add(bitmap);
                            uri1.add(data.getClipData().getItemAt(i).getUri());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String image = encodeImage(bitmaps.get(i));
                        usersImages.add(image);
                        uploadedPic(uri1.get(i));
                    }
                }else{
                    for (int i = 0; i < x; i++) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getClipData().getItemAt(i).getUri());
                            bitmaps.add(bitmap);
                            uri1.add(data.getClipData().getItemAt(i).getUri());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    for(int i = uri1.size()-1; i >= x; i--) {
                        String image = encodeImage(bitmaps.get(i));
                        usersImages.add(image);
                        uploadedPic(uri1.get(i));
                    }
                }
            }else {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),data.getData());
                    bitmaps2.add(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //One image
                uri.add(data.getData());
                if(uri.size() == 1){
                    String image = encodeImage(bitmaps2.get(0));
                    usersImages.add(image);
                    uploadedPic(uri.get(0));
                }else{
                    String image = encodeImage(bitmaps2.get(uri.size()-1));
                    usersImages.add(image);
                    uploadedPic(uri.get(uri.size()-1));
                }
            }
            addFolder();
            folderAdapter.notifyDataSetChanged();
        }
    }

    private void addFolder() {

        if(folders != null){
            for(int i = 0; i < folders.size();i++){
                if(folders.get(i).getId() == user.getUid()){
                    exist = true; break;
                }
            }
        }
        if(!exist || folders == null){
            Folder folder = new Folder(R.drawable.ic_folder_24, user.getUid(), name,bitmaps);
            folders.add(folder);
            documentRef = fStore.collection("Teams").document(teamID);

            images.addAll(usersImages);
            realTime.getReference("gallary/"+teamID+"/"+user.getUid()).setValue(images).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                }
            });
        }
        folderFetcher();
    }

    public void test(){
        documentRef2 = fStore.collection("Teams").document(teamID);
        documentRef2.get().addOnSuccessListener(doc ->{
            for(String key: doc.getData().keySet()){
                System.out.println(key);

            }
        });
    }

    public void folderFetcher(){
        folders = new ArrayList<>();
        documentRef2 = fStore.collection("Teams").document(teamID);
        documentRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                teamMembers = (ArrayList<HashMap<String,String>>)document.get("teamMembers");
                DatabaseReference galleryRef = realTime.getReference("gallary/"+teamID);

                galleryRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        HashMap<String,ArrayList<String>> gallery=new HashMap<>();
                        for (DataSnapshot datasnapshot: snapshot.getChildren()) {
                            ArrayList<String> pictures=new ArrayList<>();
                            for (DataSnapshot datasnapshot2: datasnapshot.getChildren()) {
                                pictures.add(datasnapshot2.getValue().toString());
                            }
                            gallery.put(datasnapshot.getKey(),pictures);
                        }
                        teamMemberIds = new ArrayList<>();
                        for (int i = 0; i <teamMembers.size(); i++) {
                            teamMemberIds.add(teamMembers.get(i).get("userID"));
                        }
                        if(gallery!=null)
                            for (String key : gallery.keySet()) {
                                teamMemberImages.put(key,StringsToBitmaps(gallery.get(key)));
                            }
                        fStore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()) {
                                    System.out.println(queryDocumentSnapshot.getId());
                                    if (teamMemberIds.contains(queryDocumentSnapshot.getId())) {
                                        teamMemberNames.put(queryDocumentSnapshot.getId(),queryDocumentSnapshot.getString("username"));
                                    }
                                }
                                folders = new ArrayList<>();
                                for (String key : teamMemberImages.keySet()) {
                                    Folder folder = new Folder(R.drawable.ic_folder_24, key, teamMemberNames.get(key),teamMemberImages.get(key));
                                    folders.add(folder);
                                    folderAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        folderAdapter.notifyDataSetChanged();
    }

    private String encodeImage(Bitmap bitmap){
        Bitmap newBitmap=Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),false);
        ByteArrayOutputStream byteArrayOutputStream =new ByteArrayOutputStream();
        newBitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] bytes=byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }

    @Override
    public void onResume() {
        super.onResume();
        folderFetcher();

    }
}