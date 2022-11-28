package com.example.cardmates.model;

import com.example.cardmates.activities.LoadingDialog;

public class User {

    private String user_id;
    private String Email;
    private String Name;
    private String Date;
    private String Description;
    private String image;
    private String Localidad;

    public User() {
    }

    public User(String user_id, String email, String name, String date, String description, String imageUser, String localidadUser) {
        this.user_id = user_id;
        Email = email;
        Name = name;
        Date = date;
        Description = description;
        image = imageUser;
        Localidad = localidadUser;
    }

    public String getLocalidad() {
        return Localidad;
    }

    public void setLocalidad(String localidad) {
        this.Localidad = localidad;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
