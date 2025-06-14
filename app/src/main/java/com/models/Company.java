package com.models;

public class Company {
    private int id;
    private String name;
    private String cnpj;
    private String segment;
    private String phoneNumber;
    private String responsible;
    private String website;
    private String city;
    private String cep;
    private String uf;
    private String address;
    private int addressNumber;
    private String logo;

    private int userId;
    public Company() {
    }

    public Company(String name, String cnpj, String segment, String responsible , String phoneNumber, String city, String cep,  String address, int addressNumber, String uf , String url, String logo, int userId) {
        this.name = name;
        this.cnpj = cnpj;
        this.segment = segment;
        this.phoneNumber = phoneNumber;
        this.responsible = responsible;
        this.website = website;
        this.city = city;
        this.cep = cep;
        this.uf = uf;
        this.address = address;
        this.addressNumber = addressNumber;
        this.logo = logo;
        this.userId = userId;
    }

    public Company(int id, String name, String cnpj, String segment,  String responsible, String phoneNumber,String city, String cep,String address, int addressNumber, String uf, String website,   String logo, int userId) {
        this.id = id;
        this.name = name;
        this.cnpj = cnpj;
        this.segment = segment;
        this.responsible = responsible;
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.cep = cep;
        this.address = address;
        this.addressNumber = addressNumber;
        this.uf = uf;
        this.website = website;
        this.logo = logo;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompanyName() {
        return name;
    }

    public void setCompanyName(String companyName) {
        this.name = companyName;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }



    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAddressNumber() {
        return addressNumber;
    }

    public void setAddressNumber(int addressNumber) {
        this.addressNumber = addressNumber;
    }



    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    }


