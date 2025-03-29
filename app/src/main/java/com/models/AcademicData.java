package com.models;

public class AcademicData {

    private String institutionName;
    private String level;
    private String period;
    private String startDate;
    private String endDate;
    private String courseName;
    private String city;

    private boolean inProgress;

    public AcademicData(String city, String courseName, String endDate, String startDate, String period, String level, String institutionName, boolean inProgress) {
        this.city = city;
        this.courseName = courseName;
        this.endDate = endDate;
        this.startDate = startDate;
        this.period = period;
        this.level = level;
        this.institutionName = institutionName;
        this.inProgress = inProgress;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    public boolean getInProgress(){
        return  inProgress;
    }
    public void setInProgress(boolean inProgress){
        this.inProgress = inProgress;
    }
}
