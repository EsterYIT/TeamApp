package com.example.teamapp.inviteFriends.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.teamapp.R;
import com.example.teamapp.inviteFriends.interfaces.InvitationAnswer;
import com.example.teamapp.inviteFriends.InvitationRequest;

import java.util.ArrayList;


public class InvitationRequestAdapter extends ArrayAdapter<InvitationRequest> {

    InvitationAnswer invitationAnswer;
    ArrayList<InvitationRequest> requestArrayList;
    ArrayList<InvitationRequest> al = new ArrayList<>();
    ImageView yes,no;
    String inviteStr = "Invitation to ";
    String by = "by ";


    public InvitationRequestAdapter(Context context, int resource, ArrayList<InvitationRequest> requestArrayList,InvitationAnswer invitationAnswer){
        super(context,resource,requestArrayList);
        this.invitationAnswer = invitationAnswer;
        this.requestArrayList = requestArrayList;
    }

    @Override
    public View getView(int position,View convertView, ViewGroup parent) {

        InvitationRequest request = getItem(position);
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.invitation_request, parent, false);

        TextView type = convertView.findViewById(R.id.teamTypeReq);
        TextView name = convertView.findViewById(R.id.managerName);
        no = convertView.findViewById(R.id.no);
        yes = convertView.findViewById(R.id.yes);

        type.setText(inviteStr + request.getTeamType());
        name.setText(by + request.getManagerName());
        yes.setImageResource(R.drawable.ic_yes_24);
        no.setImageResource(R.drawable.ic_no_24);


        invitationAnswer.userResponse(position,yes,no);
        return convertView;

        }

}
