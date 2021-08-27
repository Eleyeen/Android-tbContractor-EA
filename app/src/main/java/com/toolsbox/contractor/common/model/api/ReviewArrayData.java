package com.toolsbox.contractor.common.model.api;

import com.toolsbox.contractor.common.model.ContractorInfo;
import com.toolsbox.contractor.common.model.CustomerInfo;
import com.toolsbox.contractor.common.model.ReviewInfo;

public class ReviewArrayData {
    public ContractorInfo provider_contractor;
    public CustomerInfo provider_customer;
    public ReviewInfo review;

    public ReviewArrayData(){
        this.provider_contractor = new ContractorInfo();
        this.provider_customer = new CustomerInfo();
        this.review = new ReviewInfo();
    }
}
