package com.models;

public class QuestionOption {

    private int id;

    private int question_id;

    private String option_text;

    private boolean is_correct;

    public QuestionOption(int id, int question_id, String option_text, boolean is_correct) {
        this.id = id;
        this.question_id = question_id;
        this.option_text = option_text;
        this.is_correct = is_correct;
    }

    public QuestionOption(int question_id, String option_text, boolean is_correct) {
        this.question_id = question_id;
        this.option_text = option_text;
        this.is_correct = is_correct;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public String getOption_text() {
        return option_text;
    }

    public void setOption_text(String option_text) {
        this.option_text = option_text;
    }

    public boolean isIs_correct() {
        return is_correct;
    }

    public void setIs_correct(boolean is_correct) {
        this.is_correct = is_correct;
    }
}
