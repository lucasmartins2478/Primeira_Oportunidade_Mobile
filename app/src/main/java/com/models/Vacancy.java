package com.models;

public class Vacancy {
    private int id;
    private String title;
    private String description;
    private String aboutCompany;
    private String benefits;
    private String requirements;
    private String modality;
    private String locality;
    private String uf;
    private String contact;
    private String salary;
    private String level;
    private int companyId;

    private boolean isActive;
    private boolean isFilled;

    public Vacancy(String title, String description, String aboutCompany, String benefits,
                   String requirements, String modality, String locality, String uf,
                   String contact, String salary, String level, int companyId) {
        this.title = title;
        this.description = description;
        this.aboutCompany = aboutCompany;
        this.benefits = benefits;
        this.requirements = requirements;
        this.modality = modality;
        this.locality = locality;
        this.uf = uf;
        this.contact = contact;
        this.salary = salary;
        this.level = level;
        this.companyId = companyId;
    }

    public Vacancy(int id, String title, String description, String aboutCompany, String benefits, String requirements, String modality, String locality, String uf, String contact, String salary, String level, int companyId, boolean isActive, boolean isFilled) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.aboutCompany = aboutCompany;
        this.benefits = benefits;
        this.requirements = requirements;
        this.modality = modality;
        this.locality = locality;
        this.uf = uf;
        this.contact = contact;
        this.salary = salary;
        this.level = level;
        this.companyId = companyId;
        this.isActive = isActive;
        this.isFilled = isFilled;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isFilled() {
        return isFilled;
    }

    public void setFilled(boolean filled) {
        isFilled = filled;
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

    public String getAboutCompany() {
        return aboutCompany;
    }

    public void setAboutCompany(String aboutCompany) {
        this.aboutCompany = aboutCompany;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getModality() {
        return modality;
    }

    public void setModality(String modality) {
        this.modality = modality;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }
}