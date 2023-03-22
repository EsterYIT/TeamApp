package com.example.teamapp.addContact;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.teamapp.HomePage;
import com.example.teamapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ContactListFragment extends Fragment implements AddContact {

    ArrayList<String>phoneNumberList;
    List<String> usersId;

    RecyclerView recyclerView;
    ArrayList<ContactModel> arrayList = new ArrayList<ContactModel>();
    ArrayList<String> currentUserTeams;
    ArrayList<String> userTeams;
    SearchAdapter adapter;
    EditText search;
    ImageView back;
    String username,phoneNumber;
    FirebaseFirestore fStore;
    FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_contact_list, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        recyclerView = v.findViewById(R.id.recycler_view);
        back = v.findViewById(R.id.backFromSearch);
        search = v.findViewById(R.id.search);
        phoneNumberList = new ArrayList<>();
        currentUserTeams = new ArrayList<>();
        usersId = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), HomePage.class);
                startActivity(intent);
            }
        });

        checkPermission();

        return v;
    }

    private void filter(String text){
        ArrayList<ContactModel> filteredList = new ArrayList<>();

        for (ContactModel item: arrayList) {
            if (item.getName1().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }else if(item.getNumber().contains(text)){
                filteredList.add(item);
            }
        }
        if(text.length() == 10 && filteredList.get(0).getNumber().equals(text)) {
            username = filteredList.get(0).getName1();
            phoneNumber = filteredList.get(0).getNumber();
        }


        if(text.length() > 0) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            adapter = new SearchAdapter(this, getActivity(), arrayList);
            recyclerView.setAdapter(adapter);
        }else{
            recyclerView.setAdapter(null);
        }
        adapter.filterList(filteredList);

    }

    private void checkPermission() {

        if (ContextCompat.checkSelfPermission(getContext()
                , Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            addUserToContacts();
        }else{
            ActivityCompat.requestPermissions(getActivity()
                    ,new String[]{Manifest.permission.READ_CONTACTS},100);
        }
    }

    private void addUserToContacts() {

        String currentUserId=auth.getCurrentUser().getUid();
        fStore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && task.getResult() !=null) {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                       if(currentUserId.equals(queryDocumentSnapshot.getId())) {
                           currentUserTeams = (ArrayList<String>) queryDocumentSnapshot.get("teams");
                           for (QueryDocumentSnapshot Snapshot : task.getResult()) {
                               if(currentUserId.equals(Snapshot.getId()))
                                   continue;
                               userTeams = (ArrayList<String>) Snapshot.get("teams");
                               if(userTeams != null && currentUserTeams!=null) {
                                     for (int i = 0; i < userTeams.size(); i++) {
                                         if (currentUserTeams.contains(userTeams.get(i))) {
                                             ContactModel model = new ContactModel();
                                             model.setName1(Snapshot.getString("username"));
                                             model.setNumber(Snapshot.getString("phoneNumber"));
                                             model.setImage(Snapshot.getString("userImage"));
                                             arrayList.add(model);
                                             break;
                                         }
                                     }
                                 }
                           }
                           break;
                       }
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults[0]
                == PackageManager.PERMISSION_GRANTED){
            addUserToContacts();
        }else{
            checkPermission();

        }
    }

    @Override
    public void addUserToContacts(int position, String name, String phone) {

        if(name != null && phone != null) {
            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
            intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone);
            startActivity(intent);
        }
    }
}