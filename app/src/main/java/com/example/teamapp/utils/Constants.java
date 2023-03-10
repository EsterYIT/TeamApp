package com.example.teamapp.utils;

import java.util.HashMap;

public class Constants {
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content_Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";
    public static HashMap<String,String> remoteMsgHeaders=null;
    public static  HashMap<String,String> getRemoteMsgHeaders(){
        if(remoteMsgHeaders==null){
            remoteMsgHeaders=new HashMap<>();
            remoteMsgHeaders.put(REMOTE_MSG_AUTHORIZATION,"key=AAAAjbLZVVo:APA91bFCei_6uQQx6ebzVp-2t-8iArC4FaxiRdlVmZO8OE0-m3dL7PODreIKvLvog9BXfn6jCiD6cHf2IRTfdB_GSCtwA-jZ2myjUDN536btR-0ZEt_XDVinOmTxq0mGbMYyjL2lAwzT"
            );
            remoteMsgHeaders.put(
                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
            );
        }
        return remoteMsgHeaders;
    };

}
