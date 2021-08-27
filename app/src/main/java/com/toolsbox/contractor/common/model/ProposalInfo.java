package com.toolsbox.contractor.common.model;

import java.io.Serializable;

public class ProposalInfo implements Serializable {
    public int id;
    public int status;
    public int type;
    public String hourly_rate;
    public String budget;
    public String description;
    public float duration;
    public String availability_start_dates;
    public String attachment;

    public ContractorInfo contractor;
    public JobInfo job;

    public ProposalInfo(){
        this.id = 0;
        this.type = 0;
        this.status = 0;
        this.hourly_rate = "";
        this.budget = "";
        this.description = "";
        this.duration = 0;
        this.attachment = "";
        this.availability_start_dates = "";
        this.contractor = new ContractorInfo();
        this.job = new JobInfo();
    }

}
