package com.models;

public class QuestionOption {

    private int id;

    // CORREÇÃO: nome do campo tem que bater com o JSON
    private int question_test_id;

    private String option_text;

    private boolean is_correct;

    public QuestionOption(int id, int question_test_id, String option_text, boolean is_correct) {
        this.id = id;
        this.question_test_id = question_test_id;
        this.option_text = option_text;
        this.is_correct = is_correct;
    }

    public int getId() {
        return id;
    }

    public int getQuestion_test_id() {
        return question_test_id;
    }

    public String getOption_text() {
        return option_text;
    }

    public boolean isIs_correct() {
        return is_correct;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setQuestion_test_id(int question_test_id) {
        this.question_test_id = question_test_id;
    }

    public void setOption_text(String option_text) {
        this.option_text = option_text;
    }

    public void setIs_correct(boolean is_correct) {
        this.is_correct = is_correct;
    }
}
