package com.example.kristijan.opg_webshop.Model;

public class Currency {

    private String currency_code;
    private int unit_value;
    private String buying_rate;
    private String median_rate;
    private String selling_rate;

    public Currency() {
    }

    public Currency(String currency_code, int unit_value, String buying_rate, String median_rate, String selling_rate) {
        this.currency_code = currency_code;
        this.unit_value = unit_value;
        this.buying_rate = buying_rate;
        this.median_rate = median_rate;
        this.selling_rate = selling_rate;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public int getUnit_value() {
        return unit_value;
    }

    public void setUnit_value(int unit_value) {
        this.unit_value = unit_value;
    }

    public String getBuying_rate() {
        return buying_rate;
    }

    public void setBuying_rate(String buying_rate) {
        this.buying_rate = buying_rate;
    }

    public String getMedian_rate() {
        return median_rate;
    }

    public void setMedian_rate(String median_rate) {
        this.median_rate = median_rate;
    }

    public String getSelling_rate() {
        return selling_rate;
    }

    public void setSelling_rate(String selling_rate) {
        this.selling_rate = selling_rate;
    }
}
