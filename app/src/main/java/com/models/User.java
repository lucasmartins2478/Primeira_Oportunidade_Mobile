package com.models;

public class User {

    private int id;
    private String email;
    private String password;
    private UserType type;

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User(){

    }
    public User( String email, String password, UserType type){
        this.email = email;
        this.password = password;
        this.type = type;
    }

    public User(int id, String email, String password, UserType userType) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.type = userType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

}
