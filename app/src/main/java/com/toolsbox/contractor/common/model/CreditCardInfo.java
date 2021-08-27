package com.toolsbox.contractor.common.model;

import java.io.Serializable;

public class CreditCardInfo implements Serializable {
    public String id;
    public String last4;
    public String brand;
    public boolean isDefault;

    public CreditCardInfo(){
        this.id = "";
        this.last4 = "";
        this.brand = "";
        this.isDefault = false;
    }

    public CreditCardInfo(String id, String last4, String brand, boolean isDefault){
        this.id = id;
        this.last4 = last4;
        this.brand = brand;
        this.isDefault = isDefault;
    }
}
