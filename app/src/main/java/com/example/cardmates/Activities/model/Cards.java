package com.example.cardmates.Activities.model;

public class Cards {
    private String image;
    private String title;

    public Cards() {
    }

    public Cards(String image, String title) {
        this.image = image;
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
