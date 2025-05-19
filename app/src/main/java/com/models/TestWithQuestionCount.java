package com.models;

public class TestWithQuestionCount {
    private Test test;
    private int questionCount;

    public TestWithQuestionCount(Test test, int questionCount) {
        this.test = test;
        this.questionCount = questionCount;
    }

    public Test getTest() {
        return test;
    }

    public int getQuestionCount() {
        return questionCount;
    }
}
