package com.models;

public class CompetenceData {
    private String competence;

    private String level;


    public CompetenceData(String competence, String level) {
        this.competence = competence;
        this.level = level;
    }

    public CompetenceData(String competence) {
        this.competence = competence;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCompetence() {
        return competence;
    }

    public void setCompetence(String competence) {
        this.competence = competence;
    }
}
