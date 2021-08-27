package com.toolsbox.contractor.common.model;

import java.io.Serializable;

public class TimePreferItemInfo implements Serializable {
    public String title;
    public boolean isSelected;
    public FromToDateInfo timeStamp;

    public TimePreferItemInfo(){

    }

    public TimePreferItemInfo(String title, boolean isSelected, FromToDateInfo timeStamp) {
        this.title = title;
        this.isSelected = isSelected;
        this.timeStamp = timeStamp;
    }


}
