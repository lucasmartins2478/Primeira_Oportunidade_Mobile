package com.models;

public class TestWithQuestionCount {
    private Test test;
    private int questionCount;
    private Integer userScore; // porcentagem (pode ser null se o teste não foi feito)

    public TestWithQuestionCount(Test test, int questionCount, Integer userScore) {
        this.test = test;
        this.questionCount = questionCount;
        this.userScore = userScore;
    }

    public Test getTest() { return test; }
    public int getQuestionCount() { return questionCount; }
    public Integer getUserScore() { return userScore; }

    public void setUserScore(Integer score) {
        this.userScore = score;
    }

}

