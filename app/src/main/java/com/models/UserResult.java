package com.models;

import java.time.OffsetDateTime;
import java.util.Date;

public class UserResult {

    private int id;
    private int user_id;
    private int test_id;
    private int score;
    private int total_questions;
    private int time_taken_seconds;
    private Date created_at;

    public UserResult(int id, int user_id, int test_id, int score, int total_questions, int time_taken_seconds, Date created_at) {
        this.id = id;
        this.user_id = user_id;
        this.test_id = test_id;
        this.score = score;
        this.total_questions = total_questions;
        this.time_taken_seconds = time_taken_seconds;
        this.created_at = created_at;
    }

    public UserResult(int user_id, int test_id, int score, int total_questions, int time_taken_seconds, Date created_at) {
        this.user_id = user_id;
        this.test_id = test_id;
        this.score = score;
        this.total_questions = total_questions;
        this.time_taken_seconds = time_taken_seconds;
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getTest_id() {
        return test_id;
    }

    public void setTest_id(int test_id) {
        this.test_id = test_id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotal_questions() {
        return total_questions;
    }

    public void setTotal_questions(int total_questions) {
        this.total_questions = total_questions;
    }

    public int getTime_taken_seconds() {
        return time_taken_seconds;
    }

    public void setTime_taken_seconds(int time_taken_seconds) {
        this.time_taken_seconds = time_taken_seconds;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
