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

    private String schoolName;
    private String schoolYear;
    private String schoolCity;
    private String schoolStartDate;
    private String schoolEndDate;
    private boolean isCurrentlyStudying;

    private String attached;

    private String aboutYou;

    private String interestArea;


    public Curriculum( String attached, String aboutYou, String interestArea) {
        this.attached = attached;
        this.aboutYou = aboutYou;
        this.interestArea = interestArea;
    }

    public String getAttached() {
        return attached;
    }

    public void setAttached(String attached) {
        this.attached = attached;
    }

    public String getAboutYou() {
        return aboutYou;
    }


    public void setAboutYou(String aboutYou) {
        this.aboutYou = aboutYou;
    }

    public String getInterestArea() {
        return interestArea;
    }

    public void setInterestArea(String interestArea) {
        this.interestArea = interestArea;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }

    public String getSchoolCity() {
        return schoolCity;
    }

    public void setSchoolCity(String schoolCity) {
        this.schoolCity = schoolCity;
    }

    public String getSchoolStartDate() {
        return schoolStartDate;
    }

    public void setSchoolStartDate(String schoolStartDate) {
        this.schoolStartDate = schoolStartDate;
    }

    public String getSchoolEndDate() {
        return schoolEndDate;
    }

    public void setSchoolEndDate(String schoolEndDate) {
        this.schoolEndDate = schoolEndDate;
    }

    public boolean isCurrentlyStudying() {
        return isCurrentlyStudying;
    }

    public Curriculum(String schoolName, String schoolYear, String schoolCity, String schoolStartDate, String schoolEndDate, boolean isCurrentlyStudying) {
        this.schoolName = schoolName;
        this.schoolYear = schoolYear;
        this.schoolCity = schoolCity;
        this.schoolStartDate = schoolStartDate;
        this.schoolEndDate = schoolEndDate;
        this.isCurrentlyStudying = isCurrentlyStudying;
    }

    public void setCurrentlyStudying(boolean currentlyStudying) {
        isCurrentlyStudying = currentlyStudying;
    }

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
