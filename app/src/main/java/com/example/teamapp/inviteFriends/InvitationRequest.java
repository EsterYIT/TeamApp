package com.example.teamapp.inviteFriends;

import android.widget.ImageView;

public class InvitationRequest {

    String teamType, managerName;
    String teamId;

    public InvitationRequest(String managerName, String teamType,String id) {
        this.managerName = managerName;
        this.teamType = teamType;
        this.teamId = id;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamType() {
        return teamType;
    }

    public void setTeamType(String teamType) {
        this.teamType = teamType;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

}
