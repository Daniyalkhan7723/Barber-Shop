package com.example.barbershopadmin.ModelClasses;

public class Category {
    String image;
    String name;
    String percentage1;

    public Category() {
    }

    @Override
    public String toString() {
        return "Category{" +
                "image='" + image + '\'' +
                ", name='" + name + '\'' +
                ", percentage1='" + percentage1 + '\'' +
                '}';
    }



    public Category(String image, String name, String percentage1) {
        this.image = image;
        this.name = name;
        this.percentage1 = percentage1;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPercentage1() {
        return percentage1;
    }

    public void setPercentage1(String percentage1) {
        this.percentage1 = percentage1;
    }
}
