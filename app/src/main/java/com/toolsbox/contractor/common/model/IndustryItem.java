package com.toolsbox.contractor.common.model;

public class IndustryItem {
    private boolean isChecked;
    private String industryName;

    public IndustryItem(String industryName){
        this.industryName = industryName;
        this.isChecked = false;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getIndustryName() {
        return industryName;
    }

    public void setIndustryName(String industryName) {
        this.industryName = industryName;
    }

}
