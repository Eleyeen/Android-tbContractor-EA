package com.toolsbox.contractor.common.model;

import java.io.Serializable;

public class AttachFileInfo implements Serializable {
    public String name;
    public String url;
    public String localUrl;

    public boolean isDownloading;

    public AttachFileInfo(){

    }

    public AttachFileInfo(String name, String url, String localUrl, boolean isDownloading) {
        this.name = name;
        this.url = url;
        this.localUrl = localUrl;
        this.isDownloading = isDownloading;
    }


}
