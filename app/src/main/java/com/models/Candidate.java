package com.models;

public class Candidate {

    private int id;

    private String name;
    private String phoneNumber;
    private String cpf;
    private Integer curriculumId; // permite null

    private int userId;


    public Candidate(String name, String cpf, String phoneNumber, Integer curriculumId, int userId) {
        this.name = name;
        this.cpf = cpf;
        this.phoneNumber = phoneNumber;
        this.curriculumId = curriculumId;
        this.userId = userId;
    }

    public Candidate(int id,String name,String cpf, String phoneNumber, Integer curriculumId,int userId    ) {
        this.userId = userId;
        this.cpf = cpf;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.id = id;
        this.curriculumId = curriculumId;
    }

    public Candidate(String name, String cpf, String phoneNumber, int userId) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.cpf = cpf;
        this.userId = userId;

    }
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getCurriculumId() {
        return curriculumId;
    }

    public void setCurriculumId(Integer curriculumId) {
        this.curriculumId = curriculumId;
    }
}
