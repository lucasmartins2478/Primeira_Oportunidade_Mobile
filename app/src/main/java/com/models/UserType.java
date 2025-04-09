package com.models;


public enum UserType {
    CANDIDATE("candidate"),
    COMPANY("company");

    private final String value;

    UserType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
