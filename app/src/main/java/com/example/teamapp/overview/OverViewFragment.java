package com.example.teamapp.overview;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teamapp.HomePage;
import com.example.teamapp.R;
import com.example.teamapp.inviteFriends.InviteFriendsFragment;
import com.example.teamapp.overview.Adapter.TeamMembersAdapter;
import com.example.teamapp.User;
import com.example.teamapp.overview.intefaces.RecyclerViewInterface;
import com.example.teamapp.userDetailsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class OverViewFragment extends Fragment implements PopupMenu.OnMenuItemClickListener, RecyclerViewInterface {

    private  static final int IMAGE_PICK_CODE = 1000;
    private  static final int PERMISSION_CODE = 1001;
    FirebaseFirestore fStore;
    DocumentReference documentRef;
    ImageView logout,changeSub,search,addFriend;
    CircleImageView teamImage;
    TextView teamName,teamType, numberOfTeamMembers;
    TeamMembersAdapter teamMembersAdapter;
    CoordinatorLayout coordinatorLayout;
    RecyclerView recyclerView;
    private ArrayList<User> userArrayList;
    Switch mute;
    String img, teamID,teamNameStr,teamTypeStr;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    String name;
    AlertDialog dialog;
    AlertDialog.Builder builder;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_over_view, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        teamID = getArguments().getString("teamID");
        teamNameStr = getArguments().getString("teamName");

        builder = new AlertDialog.Builder(getActivity());
        recyclerView=v.findViewById(R.id.teamMembersRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userArrayList = new ArrayList<User>();

        teamMembersAdapter = new TeamMembersAdapter(getContext(),userArrayList,this);
        recyclerView.setAdapter(teamMembersAdapter);

        numberOfTeamMembers = v.findViewById(R.id.teamMembersAboveRecycler);
        coordinatorLayout = v.findViewById(R.id.overCoo);
        teamImage = v.findViewById(R.id.teamImage);
        logout = v.findViewById(R.id.logout);
        mute = v.findViewById(R.id.switch1);
        teamName = v.findViewById(R.id.teamName);
        teamType = v.findViewById(R.id.TeamMembersAndType);
        teamName.setText(teamNameStr);
        addFriend = v.findViewById(R.id.addFriend);
        changeSub = v.findViewById(R.id.changeSubject);
        search = v.findViewById(R.id.searchFriend);
        changeSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                showPopup(view);
            }
        });

        fStore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        documentRef = fStore.collection("Teams").document(teamID);
        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                List<HashMap<String,String>> users = (List<HashMap<String,String>>)document.get("teamMembers");
                if(firebaseUser.getUid().equals(users.get(0).get("userID")))
                    addFriend.setVisibility(View.VISIBLE);

                teamTypeStr = task.getResult().getString("teamType");
                teamType.setText(teamTypeStr + " " + users.size() + " member/s");
                numberOfTeamMembers.setText(users.size() + " member/s");

                for(int i = 0; i < users.size();i++){
                    documentRef = fStore.collection("users").document(users.get(i).get("userID"));
                    documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            String userId = document.getId();
                            String result = getContactName(getContext(),document.get("phoneNumber").toString());
                                if (firebaseUser.getUid().equals(userId)) {
                                    User user = new User(document.get("userImage").toString(), "You", document.getString("phoneNumber"));
                                    user.setId(userId);
                                    userArrayList.add(user);
                                } else if (result != null) {
                                    User user = new User(document.get("userImage").toString(), document.get("username").toString(), document.getString("phoneNumber"));
                                    user.setId(userId);
                                    userArrayList.add(user);
                                } else {
                                    User user = new User(document.get("userImage").toString(), document.get("phoneNumber").toString(), document.getString("phoneNumber"));
                                    user.setId(userId);
                                    userArrayList.add(user);
                                }
                             teamMembersAdapter.notifyDataSetChanged();

                        }
                    });
                }
            }
        });

        documentRef = fStore.collection("Teams").document(teamID);
        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                Map<String,String>map = (Map<String, String>)document.get("teamImage");
                if(map != null) {
                    img = map.get("imageID");
                }
                Bitmap bm = getBitmapFromEncodedString(img);
                teamImage.setImageBitmap(bm);
            }
        });

        teamImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    getActivity().requestPermissions(permissions, PERMISSION_CODE);
                }
                else{
                    pickImageFromGallery();
                }

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teamImage.setClickable(false);
                SearchInOverViewFragment searchInOverViewFragment = new SearchInOverViewFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("usersList", userArrayList);
                searchInOverViewFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView8,
                        searchInOverViewFragment).commit();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setTitle("Are you sure you want to leave the group?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        documentRef = fStore.collection("Teams").document(teamID);
                        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot document = task.getResult();
                                List<HashMap<String,String>> users = (List<HashMap<String,String>>)document.get("teamMembers");
                                if(users.size() == 1){
                                    fStore.collection("Teams").document(teamID).delete();
                                }
                                else {
                                    for (int i = 0; i < users.size(); i++) {
                                        if (users.get(i).get("userID").equals(firebaseUser.getUid())) {
                                            users.remove(i);
                                            break;
                                        }
                                    }
                                    documentRef.update("teamMembers", users);
                                }
                                documentRef = fStore.collection("users").document(firebaseUser.getUid());
                                documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        DocumentSnapshot document = task.getResult();
                                        ArrayList<String> al = (ArrayList<String>)document.get("teams");

                                        for(int i = 0; i < al.size();i++){
                                            if(al.get(i).equals(teamID)){
                                                al.remove(i);break;
                                            }
                                        }
                                        documentRef.update("teams",al);
                                        Intent intent = new Intent(getContext(),HomePage.class);
                                        startActivity(intent);
                                    }
                                });
                            }
                        });
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog = builder.create();
                dialog.show();
            }
        });

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InviteFriendsFragment inviteFriendsFragment = new InviteFriendsFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", userArrayList);
                bundle.putString("teamID", teamID);
                inviteFriendsFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView8,
                        inviteFriendsFragment).commit();
            }
        });

        return v;
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    private String encodeImage(Bitmap bitmap){
        int imageWidth=150;
        int imageHeight=bitmap.getHeight()*imageWidth/bitmap.getWidth();
        Bitmap newBitmap=Bitmap.createScaledBitmap(bitmap,imageWidth,imageHeight,false);
        ByteArrayOutputStream byteArrayOutputStream =new ByteArrayOutputStream();
        newBitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] bytes=byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    pickImageFromGallery();
                else
                    Toast.makeText(getContext(), "Permission denied...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == -1 && requestCode == IMAGE_PICK_CODE) {
            Uri imgURI=data.getData();
            teamImage.setImageURI(imgURI);
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),imgURI);
                String encodedImage=encodeImage(bitmap);
                uploadImage(encodedImage);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date currentTime = new Date();
                documentRef = fStore.collection("Teams").document(teamID);
                documentRef.update("lastEdit",formatter.format(currentTime));
                Toast.makeText(getContext(), "image was uploaded successfully", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(getContext(), "Couldn't upload profile image.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    public void uploadImage(String encodedImage){
        fStore= FirebaseFirestore.getInstance();
        documentRef = fStore.collection("Teams").document(teamID);
        Map<String,String> map = new HashMap<String,String>();
        map.put("imageID",encodedImage);
        if(map != null)
            documentRef.update(FieldPath.of("teamImage"), map);

    }


    private Bitmap getBitmapFromEncodedString(String encodedImage){
        if(encodedImage!=null){
            byte[] bytes= Base64.decode(encodedImage,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        }
        return null;
    }

    public void showPopup(View v){
        PopupMenu popupMenu = new PopupMenu(getActivity(),v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.change_subject);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.changeSubItem:
                documentRef = fStore.collection("Teams").document(teamID);
                documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        name = task.getResult().getString("teamName");

                        Intent intent = new Intent(getContext(), ChangeSubjectActivity.class);
                        intent.putExtra("teamName",name);
                        intent.putExtra("teamID",teamID);
                        startActivity(intent);
                    }
                });break;
            default:
                return false;
        }
        return false;
    }

    @Override
    public void onItemClicked(int position,String username, String imageView,String phone,String id) {
                teamImage.setClickable(false);
                userDetailsFragment userDetails = new userDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("username",username);
                bundle.putString("phoneNumber",phone);
                bundle.putString("image",imageView);
                bundle.putString("userId",id);

        userDetails.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView8,
                        userDetails).commit();
    }

    @SuppressLint("Range")
    private String getContactName(Context context, String number) {

        String name = null;

        // define the columns I want the query to return
        String[] projection = new String[] {
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup._ID};

        // encode the phone number and build the filter URI
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        // query time
        Cursor cursor = context.getContentResolver().query(contactUri, projection, null, null, null);

        if(cursor != null) {
            if (cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                System.out.println("Started uploadcontactphoto: Contact Found @ " + number);;
                System.out.println("Started uploadcontactphoto: Contact name  = " + name);
            } else {
                System.out.println("Contact Not Found @ " + number);
            }
            cursor.close();
        }
        return name;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}