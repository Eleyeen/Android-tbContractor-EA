package com.toolsbox.contractor.common.model;

import java.io.Serializable;
import java.util.ArrayList;

public class AvailabilityDateInfo implements Serializable {
    public ArrayList<FromToDateInfo> timeStamp;
    public boolean isSelected;

    public AvailabilityDateInfo(){

    }

    public AvailabilityDateInfo(ArrayList<FromToDateInfo> timeStamp){
        this.timeStamp = timeStamp;
    }

    public AvailabilityDateInfo(ArrayList<FromToDateInfo> timeStamp, boolean isSelected) {
        this.timeStamp = timeStamp;
        this.isSelected = isSelected;
    }


}
