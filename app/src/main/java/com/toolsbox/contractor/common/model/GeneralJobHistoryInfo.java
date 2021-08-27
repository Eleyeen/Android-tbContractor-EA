package com.toolsbox.contractor.common.model;

import java.io.Serializable;

public class GeneralJobHistoryInfo implements Serializable {
    public int posterType;
    public int jobId;
    public String jobName;
    public int jobStatus;
    public int jobPosterType;
    public int jobPosterId;
    public String jobCustomerName;
    public String jobContractorName;
    public String jobContractorTitle;
    public int jobContractorId;
    public int industry;
    public String startDate;
    public String postedDate;
    public float duration;
    public String budget;
    public String detail;
    public String area;
    public String area_lati;
    public String area_longi;
    public int urgency;
    public int n_quotes;
    public String jobAvailability;

    public int bidId;
    public String bidBudget;
    public String bidDetail;
    public float bidDuration;
    public String bidStartDates;

    public GeneralJobHistoryInfo(){
        this.posterType = 0;
        this.jobId = 0;
        this.jobName = "";
        this.jobStatus = 0;
        this.jobPosterType = 0;
        this.jobPosterId = 0;
        this.jobCustomerName = "";
        this.jobContractorName = "";
        this.jobContractorTitle = "";
        this.jobContractorId = 0;
        this.industry = 0;
        this.startDate = "";
        this.postedDate = "";
        this.duration = 0;
        this.budget = "";
        this.detail = "";
        this.area = "";
        this.area_lati = "";
        this.area_longi = "";
        this.urgency = 0;
        this.n_quotes = 0;
        this.jobAvailability = "";
        this.bidId = 0;
        this.bidBudget = "";
        this.bidDetail = "";
        this.bidDuration = 0;
        this.bidStartDates = "";
    }

    public GeneralJobHistoryInfo(int posterType, int jobId, String jobName, int jobStatus, int jobPosterType, int jobPosterId, String jobCustomerName, String jobContractorName, String jobContractorTitle, int jobContractorId, int industry, String startDate, String postedDate, float duration, String budget, String detail, String area, String area_lati, String area_longi, int urgency, int n_quotes, String arrJobDates, int bidId, String bidBudget, String bidDetail, float bidDuration, String arrStartDates) {
        this.posterType = posterType;
        this.jobId = jobId;
        this.jobName = jobName;
        this.jobStatus = jobStatus;
        this.jobPosterType = jobPosterType;
        this.jobPosterId = jobPosterId;
        this.jobCustomerName = jobCustomerName;
        this.jobContractorName = jobContractorName;
        this.jobContractorTitle = jobContractorTitle;
        this.jobContractorId = jobContractorId;
        this.industry = industry;
        this.startDate = startDate;
        this.postedDate = postedDate;
        this.duration = duration;
        this.budget = budget;
        this.detail = detail;
        this.area = area;
        this.area_lati = area_lati;
        this.area_longi = area_longi;
        this.urgency = urgency;
        this.n_quotes = n_quotes;
        this.jobAvailability = arrJobDates;
        this.bidId = bidId;
        this.bidBudget = bidBudget;
        this.bidDetail = bidDetail;
        this.bidDuration = bidDuration;
        this.bidStartDates = arrStartDates;
    }









}
