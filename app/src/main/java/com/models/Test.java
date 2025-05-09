package com.models;

import java.time.OffsetDateTime;
import java.util.Date;

public class Test {

    private int id;
    private String title;
    private String description;
    private int duration_minutes;
    private Date created_at;

    public Test(int id, String title, String description, int duration_minutes, Date created_at) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.duration_minutes = duration_minutes;
        this.created_at = created_at;
    }

    public Test(String title, String description, int duration_minutes, Date created_at) {
        this.title = title;
        this.description = description;
        this.duration_minutes = duration_minutes;
        this.created_at = created_at;
    }

    public Test() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration_minutes() {
        return duration_minutes;
    }

    public void setDurationMinutes(int duration_minutes) {
        this.duration_minutes = duration_minutes;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreatedAt(Date created_at) {
        this.created_at = created_at;
    }
}
