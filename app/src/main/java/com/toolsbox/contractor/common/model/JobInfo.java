package com.toolsbox.contractor.common.model;

import java.io.Serializable;

public class JobInfo implements Serializable {
    public int id;
    public int type;
    public String name;
    public int industry;
    public int status;
    public int duration;
    public int urgency;
    public String area;
    public String area_lati;
    public String area_longi;
    public String description;
    public String accepted_budget;
    public String availability_dates;
    public String job_started_date;
    public String job_posted_date;
    public String job_completed_date;
    public String job_scheduled_date;
    public String quoted_contractors;
    public String attachment;

    public int poster_type;
    public int poster_id;
    public String poster_name;
    public int contractor_id;
    public String contractor_name;

    public JobInfo(){
        this.id = 0;
        this.type = 0;
        this.name = "";
        this.industry = 0;
        this.status = 0;
        this.duration = 0;
        this.urgency = 0;
        this.status = 0;
        this.area = "";
        this.area_lati = "";
        this.area_longi = "";
        this.description = "";
        this.accepted_budget = "";
        this.availability_dates = "";
        this.job_started_date = "";
        this.job_posted_date = "";
        this.job_completed_date = "";
        this.poster_type = 0;
        this.poster_id = 0;
        this.poster_name = "";
        this.contractor_id = 0;
        this.contractor_name = "";
        this.job_scheduled_date = "";
        this.quoted_contractors = "";
        this.attachment = "";
    }

}
