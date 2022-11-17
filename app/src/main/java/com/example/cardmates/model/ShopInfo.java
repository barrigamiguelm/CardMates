package com.example.cardmates.model;

public class ShopInfo {

    String name,desc,img;
    Double lat, lang;



    public ShopInfo(String name, String desc, Double lat, Double lang) {
        this.name = name;
        this.desc = desc;
        this.lat = lat;
        this.lang = lang;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public ShopInfo() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLang() {
        return lang;
    }

    public void setLang(Double lang) {
        this.lang = lang;
    }
}
