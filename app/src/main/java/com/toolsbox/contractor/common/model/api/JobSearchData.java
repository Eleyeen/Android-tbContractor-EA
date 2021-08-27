package com.toolsbox.contractor.common.model.api;

import com.toolsbox.contractor.common.model.JobInfo;

import java.util.List;

public class JobSearchData {
    public int status;
    public String message;
    public int total_number;
    public List<JobInfo> info;
}
