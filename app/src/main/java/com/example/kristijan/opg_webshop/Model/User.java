package com.example.kristijan.opg_webshop.Model;

public class User {

    private String name;
    private String surname;
    private String phone;
    private String state;
    private String city;
    private String postalCode;
    private String streetHouseNum;

    public User() {

    }

    public User(String name, String surname, String phone) {
        this.name = name;
        this.surname = surname;
        this.phone = phone;
    }

    public User(String state, String city, String postalCode, String streetHouseNum) {
        this.state = state;
        this.city = city;
        this.postalCode = postalCode;
        this.streetHouseNum = streetHouseNum;
    }

    public User(String name, String surname, String phone, String state, String city, String postalCode, String streetHouseNum) {
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.state = state;
        this.city = city;
        this.postalCode = postalCode;
        this.streetHouseNum = streetHouseNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStreetHouseNum() {
        return streetHouseNum;
    }

    public void setStreetHouseNum(String streetHouseNum) {
        this.streetHouseNum = streetHouseNum;
    }
}
