package com.example.teamapp.overview;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.teamapp.R;
import com.example.teamapp.overview.Adapter.TeamMembersAdapter;
import com.example.teamapp.User;
import com.example.teamapp.overview.intefaces.RecyclerViewInterface;
import com.example.teamapp.userDetailsFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SearchInOverViewFragment extends Fragment implements RecyclerViewInterface {

    FirebaseFirestore fStore;
    DocumentReference documentRef;
    TeamMembersAdapter teamMembersAdapter;
    RecyclerView recyclerView;
    ArrayList<User> userArrayList;
    ImageView back;
    EditText search;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_search_in_over_view, container, false);

        Bundle bundle = getArguments();
        bundle.getParcelableArrayList("usersList");
        userArrayList = (ArrayList<User>)bundle.getSerializable("usersList");

        back = v.findViewById(R.id.backFromSearch);
        recyclerView=v.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        teamMembersAdapter = new TeamMembersAdapter(getContext(),userArrayList,this);
        recyclerView.setAdapter(teamMembersAdapter);
        search = v.findViewById(R.id.search);

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

        backPress();
        return v;
    }

    private void filter(String text) {

        ArrayList<User> filteredList = new ArrayList<>();
        for (User item: userArrayList) {
            if (item.getUsername().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }
        teamMembersAdapter.filterList(filteredList);
    }

    public void backPress(){
        back.setOnClickListener(v->getActivity().onBackPressed());
    }

    @Override
    public void onItemClicked(int position, String username,String imageView,String phone,String id) {
                userDetailsFragment userDetails = new userDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("username",username);
                bundle.putString("phoneNumber", phone);
                bundle.putString("image",imageView);

                userDetails.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView8,
                        userDetails).commit();

    }
}