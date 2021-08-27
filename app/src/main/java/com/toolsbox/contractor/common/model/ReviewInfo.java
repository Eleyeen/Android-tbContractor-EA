package com.toolsbox.contractor.common.model;

public class ReviewInfo {
    public int id;
    public int contractor_id;
    public int provider_type;
    public int provider_id;
    public float quality_rate;
    public float time_rate;
    public float overall_rate;
    public String comment;
    public String created_date;


    public ReviewInfo(){
        this.id = 0;
        this.contractor_id = 0;
        this.provider_type = 0;
        this.provider_id = 0;
        this.quality_rate = 0;
        this.time_rate = 0;
        this.overall_rate = 0;
        this.comment = "";
        this.created_date = "";
    }
}
