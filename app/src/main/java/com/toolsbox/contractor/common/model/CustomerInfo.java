package com.toolsbox.contractor.common.model;

import java.io.Serializable;

public class CustomerInfo implements Serializable {
    public int id;
    public String image_url;
    public String name;
    public String email;
    public String phone;

    public CustomerInfo(){
        this.id = 0;
        this.image_url = "";
        this.name = "";
        this.email = "";
        this.phone = "";
    }

    public CustomerInfo(int id, String image_url, String business_name, String email, String phone){
        this.id = id;
        this.image_url = image_url;
        this.name = business_name;
        this.email = email;
        this.phone = phone;
    }

}
