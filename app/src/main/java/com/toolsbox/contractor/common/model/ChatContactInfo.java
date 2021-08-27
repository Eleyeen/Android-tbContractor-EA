package com.toolsbox.contractor.common.model;

import java.io.Serializable;
import java.util.Date;

public class ChatContactInfo implements Serializable {

    public String sid;
    public String unique_name;
    public String contact_photo;
    public String contact_name;
    public String last_msg;
    public int last_msg_type;
    public int n_unread_msg;
    public Date last_date;

    public ChatContactInfo(){
        this.sid = "";
        this.unique_name = "";
        this.contact_photo = "";
        this.contact_name = "";
        this.last_msg = "";
        this.last_msg_type = 0;
        this.n_unread_msg = 0;
        this.last_date = new Date();
    }

    public ChatContactInfo(String sid, String unique_name, String contact_photo, String contact_name, String last_msg, int last_msg_type,
                           int n_unread_msg, Date last_date){
        this.sid = sid;
        this.unique_name = unique_name;
        this.contact_photo = contact_photo;
        this.contact_name = contact_name;
        this.last_msg = last_msg;
        this.last_msg_type = last_msg_type;
        this.n_unread_msg = n_unread_msg;
        this.last_date = last_date;
    }
}
