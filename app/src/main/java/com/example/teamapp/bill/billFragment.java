package com.example.teamapp.bill;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teamapp.R;
import com.example.teamapp.User;
import com.example.teamapp.bill.BillAdapter;
import com.example.teamapp.gallery.ZoomedImage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Key;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class billFragment extends Fragment implements BillOnClick{

    private  static final int IMAGE_PICK_CODE = 1000;
    private  static final int PERMISSION_CODE = 1001;

    FirebaseFirestore fStore;
    DocumentReference documentRef;
    HashMap<String,Object>billStore = new HashMap<>();
    HashMap<String,Object>billParam = new HashMap<>();
    HashMap<String,String> test = new HashMap<>();
    HashMap<String,String> billMap = new HashMap<>();

    Map<String,Object> bill1 = new HashMap<>();

    ArrayList<User> userArrayList;
    BillAdapter billAdapter;
    RecyclerView recyclerView;
    EditText bill,tip,userPayment;
    TextView totalPayed;
    TextView totalPrice,payed;
    ImageView billImg,btn;
    ImageView picImage,bit;
    String teamID;
    double billDouble,add,total;
    double totalBill;
    String totalPayedStr,totalPriceStr,billa;
    TableLayout tab;
    boolean chosePic = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_bill, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        teamID = getArguments().getString("teamID");
        bill = v.findViewById(R.id.billPrice);
        tip = v.findViewById(R.id.percent);
        totalPrice = v.findViewById(R.id.totalPrice);
        btn = v.findViewById(R.id.calcBtn);
        picImage = v.findViewById(R.id.picImage);
        billImg = v.findViewById(R.id.billImage);
        userPayment = v.findViewById(R.id.userBill);
        payed = v.findViewById(R.id.payed);
        totalPayed = v.findViewById(R.id.totalPayed);
        bit = v.findViewById(R.id.bit);

        recyclerView = v.findViewById(R.id.billRecycler);
        userArrayList = new ArrayList<>();
        billAdapter = new BillAdapter(getContext(),userArrayList,this);
        recyclerView.setAdapter(billAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fStore = FirebaseFirestore.getInstance();


        documentRef = fStore.collection("Teams").document(teamID);
        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task){
                DocumentSnapshot document = task.getResult();
                List<HashMap<String,String>> users = (List<HashMap<String,String>>)document.get("teamMembers");
                HashMap<HashMap<String,String>,HashMap<String,String>> usersBill = (HashMap<HashMap<String,String>,HashMap<String,String>>)document.get("bill");

                for(int i = 0; i < users.size();i++){

                    documentRef = fStore.collection("users").document(users.get(i).get("userID"));
                    documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            String userId = document.getId();
                            if(usersBill != null) {
                                if (usersBill.containsKey("usersBill"))
                                    billa = usersBill.get("usersBill").get(userId);
                            }

                            if(userId.equals(FirebaseAuth.getInstance().getUid())){
                                User user = new User(document.getString("userImage"),"YOU",userId, billa,document.getString("phoneNumber"));
                                userArrayList.add(user);
                            }
                            else {
                                User user = new User(document.getString("userImage"), document.getString("username"), userId, billa, document.getString("phoneNumber"));
                                userArrayList.add(user);
                            }
                            billAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    billDouble = Double.parseDouble(bill.getText().toString());
                } catch (NumberFormatException e) {
                    System.out.println("ERROR " + e.getMessage());
                }
                try {
                    add = Double.parseDouble(tip.getText().toString());
                } catch (NumberFormatException e) {
                    System.out.println("ERROR " + e.getMessage());
                }
                total = billDouble + (billDouble*(add/100));
                totalPrice.setText(Double.toString(total));
                totalPriceStr = totalPrice.getText().toString();
                billParam.put("totalPrice",total);
                billParam.put("billPrice",billDouble);
                billParam.put("tip",add);
                billParam.put("totalPayed",0);
                billStore.put("bill",billParam);
                fStore.collection("Teams").document(teamID).update(billStore);
            }
        });

        picImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        getActivity().requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else
                        pickImageFromGallery();
                }
                else
                    pickImageFromGallery();
            }
        });

        billImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                documentRef=fStore.collection("Teams").document(teamID);
                documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        Map<String,Object> bill1 = (Map<String, Object>)document.get("bill");
                        if (bill1 != null) {
                            Intent intent = new Intent(getActivity(), ZoomedImage.class);
                            Bitmap bm = getBitmapFromEncodedString(bill1.get("billImage").toString());
                            intent.putExtra("image",encodeImage(bm));
                            startActivity(intent);
                        }
                    }
                });
            }
        });
        showDetails();

        return v;
    }

    private Bitmap getBitmapFromEncodedString(String encodedImage){
        if(encodedImage!=null){
            byte[] bytes= Base64.decode(encodedImage,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        }
        return null;
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }


    private String encodeImage(Bitmap bitmap){
        int imageWidth=400;
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
            billImg.setImageURI(imgURI);
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),imgURI);
                String encodedImage=encodeImage(bitmap);
                uploadImage(encodedImage);
                chosePic = true;
                Toast.makeText(getContext(), "image was uploaded successfully", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(getContext(), "Couldn't upload profile image.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    public void uploadImage(String encodedImage){
        billParam.put("billImage",encodedImage);
        billStore.put("bill",billParam);
        fStore.collection("Teams").document(teamID).update(billStore);
    }

    private void showDetails(){
            documentRef=fStore.collection("Teams").document(teamID);
            documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                     bill1 = (Map<String, Object>)document.get("bill");
                   if (bill1 != null) {
                       if(chosePic){
                           Bitmap bm = getBitmapFromEncodedString(bill1.get("billImage").toString());
                           billImg.setImageBitmap(bm);
                           billParam.put("billImage",bill1.get("billImage").toString());
                       }
                           totalPrice.setText(bill1.get("totalPrice").toString());
                           totalPayed.setText(bill1.get("totalPayed").toString());
                           bill.setText(bill1.get("billPrice").toString());
                           tip.setText(bill1.get("tip").toString());
                           totalPayedStr = totalPayed.getText().toString();
                           totalPriceStr = totalPrice.getText().toString();
                           totalBill = Double.parseDouble(totalPayedStr);
                           billParam.put("totalPrice",bill1.get("totalPrice"));
                           billParam.put("billPrice",bill1.get("billPrice"));
                           billParam.put("tip",bill1.get("tip"));
                           billParam.put("totalPayed",bill1.get("totalPayed"));

                        if(totalBill >= Double.parseDouble(totalPriceStr) && totalBill != 0)
                            bit.setVisibility(View.VISIBLE);
                        else
                            bit.setVisibility(View.GONE);
                     }
                  }
              });
           }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void lockOnClick(int position,String id,String bill) {

        updateUserBill(position,bill);

            if (!test.containsKey(id)) {
                test.put(id, bill);
                totalBill += Double.parseDouble(bill);
            } else {
                test.forEach((key, value) -> {
                    if (key.equals(id)) {
                        totalBill -= Double.parseDouble(value);
                        totalBill += Double.parseDouble(bill);
                        test.replace(id,bill);
                    }
                });
            }
            if(totalBill < 0)
                totalBill = 0;

            billParam.put("totalPayed", totalBill);
            billStore.put("bill", billParam);
            fStore.collection("Teams").document(teamID).update(billStore);

            documentRef = fStore.collection("Teams").document(teamID);
            documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    bill1 = (Map<String, Object>) document.get("bill");
                    if (bill1 != null) {
                        try {
                            totalPayed.setText(bill1.get("totalPayed").toString());
                        } catch (Exception ex) {
                            // System.out.println();
                        }
                    }
                }
            });

            billParam.put("usersBill", test);
            billStore.put("bill", billParam);
            fStore.collection("Teams").document(teamID).update(billStore);
        if(totalBill >= Double.parseDouble(totalPriceStr) && Double.parseDouble(totalPriceStr) != 0)
            bit.setVisibility(View.VISIBLE);
        else
            bit.setVisibility(View.GONE);
    }

    @Override
    public void addToList(int position, String id, String userBill) {
        test.put(id,userBill);
    }

    private void updateUserBill(int position,String bill){
        userArrayList.get(position).setUserBill(bill);
    }
}