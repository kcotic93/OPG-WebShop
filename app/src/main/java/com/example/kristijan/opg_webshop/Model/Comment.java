package com.example.kristijan.opg_webshop.Model;

public class Comment {
    private String comment;
    private String date;
    private String productId;
    private String username;

    public Comment() {
    }

    public Comment(String comment, String date, String productId, String username) {
        this.comment = comment;
        this.date = date;
        this.productId = productId;
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
