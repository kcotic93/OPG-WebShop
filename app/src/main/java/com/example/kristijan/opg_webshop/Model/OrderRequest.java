package com.example.kristijan.opg_webshop.Model;

import java.util.List;

public class OrderRequest {

    private String comment;
    private String email;
    private String date;
    private String total;
    private String status;
    private String paymentMethom;
    private String paymentStatus;
    private String Delivery_option;
    private List<Order> products;
    private User user;
    private String Shipping;
    private String userUid;

    public OrderRequest() {
    }

    public OrderRequest(String comment, String total, String status, String paymentMethom, String paymentStatus, List<Order> products, User user, String Delivery_option,String email, String date,String userUid) {
        this.comment = comment;
        this.total = total;
        this.status = status;
        this.paymentMethom = paymentMethom;
        this.paymentStatus = paymentStatus;
        this.products = products;
        this.user = user;
        this.Delivery_option=Delivery_option;
        this.date=date;
        this.email=email;
        this.userUid=userUid;
    }

    public OrderRequest(String comment, String total, String status, String paymentMethom, String paymentStatus, List<Order> products, User user, String Delivery_option,String email, String date,String shipping,String userUid) {
        this.comment = comment;
        this.total = total;
        this.status = status;
        this.paymentMethom = paymentMethom;
        this.paymentStatus = paymentStatus;
        this.products = products;
        this.user = user;
        this.Delivery_option=Delivery_option;
        this.date=date;
        this.email=email;
        Shipping=shipping;
        this.userUid=userUid;
    }

    public String getShipping() {
        return Shipping;
    }

    public void setShipping(String shipping) {
        Shipping = shipping;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethom() {
        return paymentMethom;
    }

    public void setPaymentMethom(String paymentMethom) {
        this.paymentMethom = paymentMethom;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public List<Order> getProducts() {
        return products;
    }

    public void setProducts(List<Order> products) {
        this.products = products;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDelivery_option() {
        return Delivery_option;
    }

    public void setDelivery_option(String delivery_option) {
        Delivery_option = delivery_option;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }
}
