package com.toolsbox.contractor.common.model;

import java.io.Serializable;
import java.util.Date;

public class FromToDateInfo implements Serializable {
    public Date fromDate;
    public Date toDate;

    public FromToDateInfo(){

    }

    public FromToDateInfo(Date fromDate, Date toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

}
