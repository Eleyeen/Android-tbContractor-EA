package com.toolsbox.contractor.common.model;

public class QuoteInfo {
    public int status;
    public int id;
    public int job_id;
    public int contractor_id;
    public float duration;
    public String availability_start_dates;
    public String budget;
    public String description;
    public String created_date;
    public JobInfo job;

    public QuoteInfo(){
        this.status = 0;
        this.id = 0;
        this.job_id = 0;
        this.contractor_id = 0;
        this.duration = 0;
        this.availability_start_dates = "";
        this.budget = "";
        this.description = "";
        this.created_date = "";
        this.job = new JobInfo();
    }
}
