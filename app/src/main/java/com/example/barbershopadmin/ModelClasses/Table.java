package com.example.barbershopadmin.ModelClasses;

public class Table {
    String date;
    String key;
    String percentage;
    String total;
    String payable;

    public Table() {
    }


    public Table(String date, String key, String percentage, String total, String payable) {
        this.date = date;
        this.key = key;
        this.percentage = percentage;
        this.total = total;
        this.payable = payable;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getPayable() {
        return payable;
    }

    public void setPayable(String payable) {
        this.payable = payable;
    }
}

