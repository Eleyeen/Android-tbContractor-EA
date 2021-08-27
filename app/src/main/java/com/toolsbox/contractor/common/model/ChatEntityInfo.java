package com.toolsbox.contractor.common.model;

import java.io.Serializable;
import java.util.Date;

public class ChatEntityInfo implements Serializable {
    public enum USER_TYPE  {I, YOU}
    public enum MESSAGE_TYPE {DATE, TEXT, VIDEO, IMAGE}

    public MESSAGE_TYPE type;
    public USER_TYPE userType;
    public String text;
    public Date date;
    public String author;
    public boolean isLoading;



    public ChatEntityInfo(MESSAGE_TYPE type, USER_TYPE userType, String text, Date date, String author, boolean isLoading) {
        this.type = type;
        this.userType = userType;
        this.text = text;
        this.date = date;
        this.author = author;
        this.isLoading = isLoading;
    }




}
