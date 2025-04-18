package com.models;

public class AcademicData {

    private String institutionName;
    private String level;
    private String period;
    private String startDate;
    private String endDate;
    private String courseName;
    private String city;

    private boolean isCurrentlyStudying;

    private int curriculumId;

    public AcademicData(String courseName, String period, String startDate, String endDate, boolean isCurrentlyStudying, String institutionName, String level, String city, int curriculumId) {
        this.courseName = courseName;
        this.period = period;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isCurrentlyStudying = isCurrentlyStudying;
        this.institutionName = institutionName;
        this.level = level;
        this.city = city;
        this.curriculumId = curriculumId;
    }

    public AcademicData() {

    }

    public boolean isCurrentlyStudying() {
        return isCurrentlyStudying;
    }

    public void setCurrentlyStudying(boolean currentlyStudying) {
        isCurrentlyStudying = currentlyStudying;
    }

    public int getCurriculumId() {
        return curriculumId;
    }

    public void setCurriculumId(int curriculumId) {
        this.curriculumId = curriculumId;
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
    public boolean getIsCurrentlyStudying(){
        return  isCurrentlyStudying;
    }
    public void setIsCurrentlyStudying(boolean isCurrentlyStudying){
        this.isCurrentlyStudying = isCurrentlyStudying;
    }
}
