package com.example.teamapp.bill;

import android.widget.EditText;
import android.widget.ImageView;

public interface BillOnClick {

    void lockOnClick(int position, String id, String userBill);
    void addToList(int position,String id,String userBill);

}
