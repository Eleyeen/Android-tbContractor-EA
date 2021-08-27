package com.toolsbox.contractor.common.model.api;


import com.toolsbox.contractor.common.model.ContractorInfo;

import java.util.List;

public class ContractorSearchData {
    public int status;
    public String message;
    public int total_number;
    public List<ContractorInfo> info;
}
