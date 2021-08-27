package com.toolsbox.contractor.common.model;

import java.io.Serializable;

public class ContractorInfo implements Serializable {
    public int id;
    public int status;
    public String image_url;
    public String business_name;
    public String business_number;
    public String email;
    public String phone;
    public String address;
    public String address_lati;
    public String address_longi;
    public String speciality_title;
    public String industry;
    public String about;
    public int business_structure;
    public String fcm_token;
    public String token;


    public ContractorInfo(){
        this.id = 0;
        this.status = 0;
        this.image_url = "";
        this.business_name = "";
        this.business_number = "";
        this.email = "";
        this.phone = "";
        this.address = "";
        this.address_lati = "";
        this.address_longi = "";
        this.speciality_title = "";
        this.industry = "";
        this.business_structure = 0;
        this.fcm_token = "";
        this.token = "";
        this.about = "";
    }

//    public ContractorInfo(int id, String image_url, String business_name, String business_number, String email, String phone, String address,
//                          String address_lati, String address_longi, String speciality_title, String industry, int business_structure){
//        this.id = id;
//        this.image_url = image_url;
//        this.business_name = business_name;
//        this.business_number = business_number;
//        this.email = email;
//        this.phone = phone;
//        this.address = address;
//        this.address_lati = address_lati;
//        this.address_longi = address_longi;
//        this.speciality_title = speciality_title;
//        this.industry = industry;
//        this.business_structure = business_structure;
//    }

}
