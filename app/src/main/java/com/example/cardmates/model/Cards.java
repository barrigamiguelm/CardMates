package com.example.cardmates.model;

public class Cards {
    private String image;
    private String title;
    private String tagTitle;
    private boolean isChecked;

    public Cards() {
    }


    public Cards(String image, String title, String tagTitle, boolean isChecked) {
        this.image = image;
        this.title = title;
        this.tagTitle = tagTitle;
        this.isChecked = isChecked;
    }


    public String getTagTitle() {
        return tagTitle;
    }

    public void setTagTitle(String tagTitle) {
        this.tagTitle = tagTitle;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
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

    @Override
    public String toString() {
        return "Cards{" +
                "image='" + image + '\'' +
                ", title='" + title + '\'' +
                ", isChecked=" + isChecked +
                '}';
    }
}
