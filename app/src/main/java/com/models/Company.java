package com.models;

public class Company {
    private String companyName;
    private String cnpj;
    private String segment;
    private String phoneNumber;
    private String responsible;
    private String website;
    private String city;
    private String cep;
    private String uf;
    private String address;
    private String addressNumber;
    private String logo;
    public Company() {
    }

    public Company(String companyName, String cnpj, String segment,  String phoneNumber, String responsible, String website, String city, String cep, String uf, String address, String addressNumber,  String logo) {
        this.companyName = companyName;
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
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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

    public String getAddressNumber() {
        return addressNumber;
    }

    public void setAddressNumber(String addressNumber) {
        this.addressNumber = addressNumber;
    }



    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }


    }


