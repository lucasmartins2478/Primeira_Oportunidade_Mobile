package com.models;

public class CourseData {
    private int id;
    private String courseName;
    private String modality;
    private String duration;
    private String endDate;
    private String grantingIntitution;

    private boolean inProgress;

    private int curriculumId;

    public CourseData(int id, String name, String modality, String duration, String endDate, boolean isCurrentlyStudying, String institutionName, int curriculumId) {
        this.id = id;
        this.courseName = name;
        this.modality = modality;
        this.duration = duration;
        this.endDate = endDate;
        this.inProgress = isCurrentlyStudying;
        this.grantingIntitution = institutionName;
        this.curriculumId = curriculumId;
    }

    public CourseData() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public int getCurriculumId() {
        return curriculumId;
    }

    public void setCurriculumId(int curriculumId) {
        this.curriculumId = curriculumId;
    }

    public CourseData(String courseName, String modality, String duration, String endDate, boolean inProgress, String grantingIntitution, int curriculumId) {
        this.courseName = courseName;
        this.modality = modality;
        this.duration = duration;
        this.endDate = endDate;
        this.inProgress = inProgress;
        this.grantingIntitution = grantingIntitution;
        this.curriculumId = curriculumId;
    }
}
