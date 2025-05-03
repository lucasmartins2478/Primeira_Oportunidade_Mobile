package com.models;

public class CompetenceData {
    private int id;
    private String competence;

    private int curriculumId;

    public CompetenceData() {

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CompetenceData(String competence, int curriculumId) {
        this.competence = competence;
        this.curriculumId = curriculumId;

    }

    public int getCurriculumId() {
        return curriculumId;
    }

    public void setCurriculumId(int curriculumId) {
        this.curriculumId = curriculumId;
    }

    public CompetenceData(String competence) {
        this.competence = competence;
    }


    public String getCompetence() {
        return competence;
    }

    public void setCompetence(String competence) {
        this.competence = competence;
    }
}
