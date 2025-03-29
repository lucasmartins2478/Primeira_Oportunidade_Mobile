package com.models;

public class Curriculum {
    private String birthDate;
    private String age;
    private String gender;
    private String race;
    private String city;
    private String cep;
    private String uf;
    private String address;
    private String addressNumber;

    public Curriculum(String birthDate, String age, String gender, String race, String city, String cep, String uf, String address, String addressNumber) {
        this.birthDate = birthDate;
        this.age = age;
        this.gender = gender;
        this.race = race;
        this.city = city;
        this.cep = cep;
        this.uf = uf;
        this.address = address;
        this.addressNumber = addressNumber;
    }


    public String getAddressNumber() {
        return addressNumber;
    }

    public void setAddressNumber(String addressNumber) {
        this.addressNumber = addressNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }


}
