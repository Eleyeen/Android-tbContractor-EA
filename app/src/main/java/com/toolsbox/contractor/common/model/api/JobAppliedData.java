package com.toolsbox.contractor.common.model.api;

import com.toolsbox.contractor.common.model.QuoteInfo;

import java.util.List;

public class JobAppliedData {
    public int status;
    public String message;
    public int total_number;
    public List<QuoteInfo> info;
}
