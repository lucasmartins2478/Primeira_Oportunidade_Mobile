package com.models;

import java.time.OffsetDateTime;
import java.util.Date;

public class QuestionTest {

    private int id;

    private int test_id;
    private String question_text;
    private Date created_at;

    public QuestionTest(int id, int test_id, String question_text, Date created_at) {
        this.id = id;
        this.test_id = test_id;
        this.question_text = question_text;
        this.created_at = created_at;
    }

    public QuestionTest(Date created_at, String question_text, int test_id) {
        this.created_at = created_at;
        this.question_text = question_text;
        this.test_id = test_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTest_id() {
        return test_id;
    }

    public void setTest_id(int test_id) {
        this.test_id = test_id;
    }

    public String getQuestion_text() {
        return question_text;
    }

    public void setQuestion_text(String question_text) {
        this.question_text = question_text;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
