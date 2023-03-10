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

import com.example.teamapp.HomePage;
import com.example.teamapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    ArrayList<Uri> uri = new ArrayList<>();
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
        System.out.println("bitmapped");
        System.out.println(bitmaped.get(0));
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
            System.out.println("is there?");
            if(data.getClipData() != null){
                System.out.println("two pic");
                int x = data.getClipData().getItemCount();
                for(int i = 0; i < x; i++){
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),data.getClipData().getItemAt(i).getUri());
                        bitmaps.add(bitmap);
                        uri.add(data.getClipData().getItemAt(i).getUri());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Multiple images
                }
            }else {
                System.out.println("one pic");
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),data.getData());
                    bitmaps.add(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //One image
            }

            for(int i = 0; i < uri.size();i++) {
                String image = encodeImage(bitmaps.get(i));
                usersImages.add(image);
                uploadedPic(uri.get(i));
            }
            addFolder();
            folderAdapter.notifyDataSetChanged();
        }
    }

    private void addFolder() {
        System.out.println("add folder");
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
                    Toast.makeText(getContext(), "testststst", Toast.LENGTH_SHORT).show();
                }
            });

//                documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        System.out.println("on complete");
//                        DocumentSnapshot documentSnapshot=task.getResult();
//                        Map<String, Object> hangoutTeam = new HashMap<String, Object>();
//                        HashMap<Object,ArrayList> usersId = (HashMap<Object,ArrayList>)documentSnapshot.get("gallery");
//
//                        System.out.println(usersId);
//                        if(usersId.containsKey(user.getUid())){
//                            ((ArrayList)usersId.get(user.getUid())).addAll(usersImages);
//                        }else{
//                            usersId.put(user.getUid(),usersImages);
//                        }
//                        hangoutTeam.put("gallery",usersId);
//                        documentRef.update(hangoutTeam);
//                        folderFetcher();
//                    }
//                });
        }
        folderFetcher();
    }

    public void test(){
        documentRef2 = fStore.collection("Teams").document(teamID);
        documentRef2.get().addOnSuccessListener(doc ->{
            System.out.println("DATATATATATATA");
            System.out.println();
            for(String key: doc.getData().keySet()){
                System.out.println(key);

            }
        });
    }

    public void folderFetcher(){
        System.out.println("4445555555555555");
        folders = new ArrayList<>();
        documentRef2 = fStore.collection("Teams").document(teamID);
        documentRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                teamMembers = (ArrayList<HashMap<String,String>>)document.get("teamMembers");
                for (int i = 0; i < teamMembers.size(); i++) {
                    System.out.println("123123"+teamMembers.get(i).get("userID"));
                }
                System.out.println("123123");
//                List<DocumentReference> galleryReferences=new LinkedList<>();
//                for (int i = 0; i < teamMembers.size(); i++) {
//                    galleryReferences.add(fStore.collection("Teams").document(teamID+"/"+teamMembers.get(i).get("userID")));
//                }
//                List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
//                for (DocumentReference documentReference : galleryReferences) {
//                    Task<DocumentSnapshot> documentSnapshotTask = documentReference.get();
//                    tasks.add(documentSnapshotTask);
//                }
//                Tasks.whenAllSuccess(tasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
//                    @Override
//                    public void onSuccess(List<Object> list) {
//                        //Do what you need to do with your list
//                        for (Object object : list) {
//                            System.out.println(((DocumentSnapshot)object).getString("name"));
//                        }
//                    }
//                });
//                HashMap<String,ArrayList<String>> gallery=(HashMap<String,ArrayList<String>>)document.get("gallery");
//                fStore.collection("Teams").document(teamID).get().addOnSuccessListener(new )

//                DatabaseReference galleryRef = realTime.getReference("Galleries/"+teamID);
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
//                    System.out.println("THAT WAS FOUND!!!!!!! "+teamMemberIds.get(i));
                        }
                        if(gallery!=null)
                            for (String key : gallery.keySet()) {
                                System.out.println("keys");
                                System.out.println(key);
                                teamMemberImages.put(key,StringsToBitmaps(gallery.get(key)));
                            }
                        fStore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()) {
                                    System.out.println(queryDocumentSnapshot.getId());
                                    if (teamMemberIds.contains(queryDocumentSnapshot.getId())) {
                                        teamMemberNames.put(queryDocumentSnapshot.getId(),queryDocumentSnapshot.getString("username"));
//                                System.out.println("name was found");
//                                System.out.println(queryDocumentSnapshot.getString("username"));
                                    }
                                }
                                System.out.println("baklava");
                                folders = new ArrayList<>();
                                for (String key : teamMemberImages.keySet()) {
                                    System.out.println("lo baklava");
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