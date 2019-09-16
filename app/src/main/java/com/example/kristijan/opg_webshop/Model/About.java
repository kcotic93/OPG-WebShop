package com.example.kristijan.opg_webshop.Model;

public class About {
    private String activities;
    private String goals;
    private String iban;
    private String address;
    private String email;
    private String facebook;
    private String phone;

    public About() {
    }

    public About(String activities, String goals, String iban, String address, String email, String facebook, String phone) {
        this.activities = activities;
        this.goals = goals;
        this.iban = iban;
        this.address = address;
        this.email = email;
        this.facebook = facebook;
        this.phone = phone;
    }

    public String getActivities() {
        return activities;
    }

    public void setActivities(String activities) {
        this.activities = activities;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
