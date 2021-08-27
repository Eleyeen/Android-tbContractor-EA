package com.toolsbox.contractor.common.model;

public class ProjectInfo {
    private String projectName;
    private int status;              /* In bidding process = 0  In process = 1   Complete  = 2 */
    private String contractorName;
    private String contractorTitle;
    private String contractorSpecialization;
    private String startDate;
    private int duration;
    private double cost;
    private String projectDescription;

    public ProjectInfo(String projectName, int status, String contractorName, String contractorTitle, String contractorSpecialization,
                       String startDate, int duration, double cost, String projectDescription){
        this.projectName = projectName;
        this.status = status;
        this.contractorName = contractorName;
        this.contractorTitle = contractorTitle;
        this.contractorSpecialization = contractorSpecialization;
        this.startDate = startDate;
        this.duration = duration;
        this.cost = cost;
        this.projectDescription = projectDescription;
    }


    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getContractorName() {
        return contractorName;
    }

    public void setContractorName(String contractorName) {
        this.contractorName = contractorName;
    }

    public String getContractorTitle() {
        return contractorTitle;
    }

    public void setContractorTitle(String contractorTitle) {
        this.contractorTitle = contractorTitle;
    }

    public String getContractorSpecialization() {
        return contractorSpecialization;
    }

    public void setContractorSpecialization(String contractorSpecialization) {
        this.contractorSpecialization = contractorSpecialization;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }


}
