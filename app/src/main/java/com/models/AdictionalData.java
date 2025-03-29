package com.models;

public class AdictionalData {

    private String aboutYou;

    private String curriculum;

    private String interestArea;

    public String getAboutYou() {
        return aboutYou;
    }

    public void setAboutYou(String aboutYou) {
        this.aboutYou = aboutYou;
    }

    public String getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(String curriculum) {
        this.curriculum = curriculum;
    }

    public String getInterestArea() {
        return interestArea;
    }

    public void setInterestArea(String interestArea) {
        this.interestArea = interestArea;
    }

    public AdictionalData(String aboutYou, String curriculum, String interestArea) {
        this.aboutYou = aboutYou;
        this.curriculum = curriculum;
        this.interestArea = interestArea;
    }
}
