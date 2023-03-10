package com.example.teamapp;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.MediaStore;
import android.util.Base64;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.teamapp.utils.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EditProfileFragmentNav extends Fragment implements View.OnClickListener {

    private  static final int IMAGE_PICK_GALLERY = 1;
    private  static final int IMAGE_PICK_CAMERA = 2;
    private  static final int RESULT_CODE = -1;
    private  static final int PERMISSION_CODE = 1001;

    private FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseFirestore fStore;

    DocumentReference documentRef;
    DatabaseReference myRef;
    ImageView userPen, emailPen, phonePen, passwordPen,changeImg, profileImg;
    ImageView cameraImage,galleryImage;
    EditText username, email, password, phoneNumber;
    Button updateBtn;
    Toolbar toolbar;
    ProgressDialog pd;
    PreferenceManager preferenceManager;
    Dialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_profile_nav, container, false);
        myRef = database.getInstance().getReference("AuthUsers");
        username = v.findViewById(R.id.editProfileName);
        email = v.findViewById(R.id.editProfileEmail);
        password = v.findViewById(R.id.editProfilePassword);
        phoneNumber = v.findViewById(R.id.editProfilePhone);
        userPen = v.findViewById(R.id.updateUsername);
        userPen.setOnClickListener(this);
        emailPen = v.findViewById(R.id.updateEmail);
        emailPen.setOnClickListener(this);
        phonePen = v.findViewById(R.id.updatePhoneNumber);
        phonePen.setOnClickListener(this);
        passwordPen = v.findViewById(R.id.updatePassword);
        passwordPen.setOnClickListener(this);
        updateBtn = v.findViewById(R.id.updateButton);
        changeImg = v.findViewById(R.id.changePhoto);
        changeImg.setOnClickListener(this);
        profileImg = v.findViewById(R.id.profileImage);
        pd = new ProgressDialog(getActivity());
        preferenceManager=new PreferenceManager(getContext());
        toolbar = v.findViewById(R.id.toolbar);
        dialog = new Dialog(getActivity());

        auth = FirebaseAuth.getInstance();

        showUserDetails();

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser firebaseUser = auth.getCurrentUser();
                if (firebaseUser != null) {
                    fStore=FirebaseFirestore.getInstance();
                    documentRef=fStore.collection("users")
                            .document(firebaseUser.getUid());
                    documentRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if(value != null) {
                                checkNewDetailsValidation(username.getText().toString(),phoneNumber.getText().toString());
                                if (!(value.getString("username")).equals(username.getText().toString())) {
                                    documentRef.update("username",username.getText().toString());
                                }
                                if (!(value.getString("phoneNumber")).equals(phoneNumber.getText().toString())) {
                                    documentRef.update("phoneNumber",phoneNumber.getText().toString());
                                }
                            }
                        }
                    });
                }
            }
        });
        changeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraOrGallery();
            }

        });
        return v;
    }

    private void cameraOrGallery() {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.camera_or_gallery,null);
        ImageView camera = view.findViewById(R.id.cameraPicker);
        ImageView gallery = view.findViewById(R.id.galleryPicker);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    getActivity().requestPermissions(permissions, PERMISSION_CODE);
                }
                else {
                    pickImageFromCamera();
                }
                dialog.dismiss();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    getActivity().requestPermissions(permissions, PERMISSION_CODE);
                }
                else {
                    pickImageFromGallery();
                }
                dialog.dismiss();
            }
        });

    }

    private void checkNewDetailsValidation(String name, String phone){
        if(name.length() < 2)
            username.setError("User name should be at least 2 characters");
        if(phone.length() != 10)
            phoneNumber.setError("Phone number should be 10 characters");
    }

    private void pickImageFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,IMAGE_PICK_CAMERA);
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY);
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

        switch (requestCode){

            case IMAGE_PICK_GALLERY:
                if(resultCode == RESULT_CODE){
                    Uri imgURI=data.getData();
                    profileImg.setImageURI(imgURI);
                    try {
                        Bitmap bitmap=MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),imgURI);
                        String encodedImage=encodeImage(bitmap);
                        uploadImage(encodedImage);
                        Toast.makeText(getContext(), "image was uploaded successfully", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(getContext(), "Couldn't upload profile image.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }break;

            case IMAGE_PICK_CAMERA:
                if(resultCode == RESULT_CODE){
                    Bundle bundle = data.getExtras();
                    Bitmap bm = (Bitmap) bundle.get("data");
                    profileImg.setImageBitmap(bm);
                    String encodedImage=encodeImage(bm);
                    uploadImage(encodedImage);
                    Toast.makeText(getContext(), "image was uploaded successfully", Toast.LENGTH_SHORT).show();
                }break;

        }
    }

    public void uploadImage(String encodedImage){
        fStore=FirebaseFirestore.getInstance();
        fStore.collection("users").document(FirebaseAuth.getInstance().getUid()).update("userImage",encodedImage);
    }

    private void showUserDetails() {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            fStore=FirebaseFirestore.getInstance();
            documentRef=fStore.collection("users").document(auth.getCurrentUser().getUid());
            documentRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    preferenceManager.putString("userName",value.getString("username"));
                    preferenceManager.putString("userPhoneNumber",value.getString("phoneNumber"));
                    preferenceManager.putString("userEmail",value.getString("email"));
                }
            });
            username.setText(preferenceManager.getString("userName"));
            email.setText(preferenceManager.getString("userEmail"));
            phoneNumber.setText(preferenceManager.getString("userPhoneNumber"));
            byte[] bytes= Base64.decode(preferenceManager.getString("userImage"),Base64.DEFAULT);
            profileImg.setImageBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length));
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.updateUsername:
                username.setEnabled(true);
                email.setEnabled(false);
                phoneNumber.setEnabled(false);
                password.setEnabled(false);
                break;
            case R.id.updatePhoneNumber:
                phoneNumber.setEnabled(true);
                username.setEnabled(false);
                email.setEnabled(false);
                password.setEnabled(false);
                break;
            case R.id.updateEmail:
                email.setEnabled(true);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerViewAll,
                        new ChangeEmailFragment()).commit();
                username.setEnabled(false);
                phoneNumber.setEnabled(false);
                password.setEnabled(false);
                break;
            case R.id.updatePassword:
                password.setEnabled(true);
                username.setEnabled(false);
                phoneNumber.setEnabled(false);
                email.setEnabled(false);
                pd.setMessage("Changing password");
                showChangePasswordDialog();
                break;
            default:
                break;
        }
    }

    private void showChangePasswordDialog() {

     View view = LayoutInflater.from(getActivity()).inflate(R.layout.update_password,null);
     EditText oldPassword = view.findViewById(R.id.oldPass);
     EditText newPassword = view.findViewById(R.id.newPass);
     EditText confirmPassword = view.findViewById(R.id.confirmPass);
     Button updateBtn = view.findViewById(R.id.updatePassBtn);

     AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
     builder.setView(view);

     AlertDialog dialog = builder.create();
     dialog.show();

     updateBtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             String oldPassStr = oldPassword.getText().toString();
             String newPassStr = newPassword.getText().toString();
             String confirmPassStr = confirmPassword.getText().toString();

             if(!newPassStr.equals(confirmPassStr)){
                 newPassword.setError("Passwords didn't match");
                 confirmPassword.setError("Passwords didn't match");
             }
             if(TextUtils.isEmpty(oldPassStr)){
                 Toast.makeText(getActivity(), "Enter your current password", Toast.LENGTH_SHORT).show();
                 return;
             }
             if(newPassStr.length() < 8){
                 Toast.makeText(getActivity(), "Password length must be at least 8 characters", Toast.LENGTH_SHORT).show();
                 return;
             }
             dialog.dismiss();
             updatePassword(oldPassStr, newPassStr);
         }
     });



    }

    private void updatePassword(String oldPassStr, String newPassStr) {
        pd.show();
        final FirebaseUser user = auth.getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(),oldPassStr);
        user.reauthenticate(authCredential).
               addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       user.updatePassword(newPassStr).
                               addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if(task.isSuccessful()) {
                                           pd.dismiss();
                                           myRef.child(auth.getUid()).child("password").setValue(newPassStr);
                                           Toast.makeText(getActivity(), "Password Updated", Toast.LENGTH_SHORT).show();
                                           Intent intent = new Intent(getContext(),MainActivity.class);
                                           startActivity(intent);
                                       }
                                   }
                               }).addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception e) {
                                       pd.dismiss();
                                       Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                   }
                               });
                   }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}