package com.models;

public class CourseData {
    private String courseName;
    private String modality;
    private String duration;
    private String endDate;
    private String grantingIntitution;

    private boolean inProgress;

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getModality() {
        return modality;
    }

    public void setModality(String modality) {
        this.modality = modality;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getGrantingIntitution() {
        return grantingIntitution;
    }

    public void setGrantingIntitution(String grantingIntitution) {
        this.grantingIntitution = grantingIntitution;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public CourseData(String courseName, String modality, String duration, String endDate, String grantingIntitution, boolean inProgress) {
        this.courseName = courseName;
        this.modality = modality;
        this.duration = duration;
        this.endDate = endDate;
        this.grantingIntitution = grantingIntitution;
        this.inProgress = inProgress;
    }
}
