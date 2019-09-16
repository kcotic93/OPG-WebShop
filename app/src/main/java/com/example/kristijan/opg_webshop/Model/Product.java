package com.example.kristijan.opg_webshop.Model;

public class Product {

    private String name;
    private String image;
    private String description;
    private String mesUnit;
    private int price;
    private int discount;
    private String categoryId;
    private int quantity;

    public Product() {
    }

    public Product(String name, String image, String description, String mesUnit, int price, int discount, String categoryId, int quantity) {
        this.name = name;
        this.image = image;
        this.description = description;
        this.mesUnit = mesUnit;
        this.price = price;
        this.discount = discount;
        this.categoryId = categoryId;
        this.quantity=quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMesUnit() {
        return mesUnit;
    }

    public void setMesUnit(String mesUnit) {
        this.mesUnit = mesUnit;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

