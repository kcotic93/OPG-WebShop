package com.example.kristijan.opg_webshop.Model;

public class NewsModel {

    private String heading;
    private String content;
    private String date;

    public NewsModel() {
    }

    public NewsModel(String heading, String content, String date) {
        this.heading = heading;
        this.content = content;
        this.date = date;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
