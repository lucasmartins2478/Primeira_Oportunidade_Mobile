package com.models;

public class Application {

    private int vacancyId;
    private int userId;

    public Application(int vacancyId, int userId){
        this.vacancyId = vacancyId;
        this.userId = userId;
    }

    public int getVacancyId() {
        return vacancyId;
    }

    public void setVacancyId(int vacancyId) {
        this.vacancyId = vacancyId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
