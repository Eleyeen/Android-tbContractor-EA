package com.toolsbox.contractor.common.model;

import java.io.Serializable;

public class RecordHoursInfo implements Serializable {
    public int id;
    public int job_id;
    public float hours;
    public String created_time;

    public RecordHoursInfo(){
        this.id = 0;
        this.job_id = 0;
        this.hours = 0;
        this.created_time = "";
    }
}
