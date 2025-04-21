package com.models;

public class Answer {

    private String answer;
    private int questionId;
    private int userId;

    public Answer() {

    }


    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Answer(String answer, int questionId, int userId) {
        this.answer = answer;
        this.questionId = questionId;
        this.userId = userId;
    }
}
