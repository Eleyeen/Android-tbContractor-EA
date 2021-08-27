package com.toolsbox.contractor.common.model;

import java.io.Serializable;

public class BankInfo implements Serializable {
    public String id;
    public String last4;
    public String routingNumber;
    public boolean defaultCurrency;
    public String bankName;

    public BankInfo(){
        this.id = "";
        this.last4 = "";
        this.routingNumber = "";
        this.defaultCurrency = false;
        this.bankName = "";
    }

    public BankInfo(String id, String last4, String routingNumber, boolean defaultCurrency, String bankName){
        this.id = id;
        this.last4 = last4;
        this.routingNumber = routingNumber;
        this.defaultCurrency = defaultCurrency;
        this.bankName = bankName;
    }
}
