package com.models;

public class Question {

    private int id;

    private String question;
    private int vacancyId;

    public Question (){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Question(String question, int vacancyId){
        this.question = question;
        this.vacancyId = vacancyId;
    }

    public int getVacancyId() {
        return vacancyId;
    }

    public void setVacancyId(int vacancyId) {
        this.vacancyId = vacancyId;
    }

    public String getQuestion(){
        return question;
    }
    public void setQuestion(String question){
        this.question = question;
    }
}
