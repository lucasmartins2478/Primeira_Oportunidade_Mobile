package com.models;

public class Candidate {

    private int id;

    private String name;
    private String phoneNumber;
    private String cpf;
    private String email;




    public Candidate() {
    }

    public Candidate( String name, String phoneNumber, String cpf) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.cpf = cpf;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
