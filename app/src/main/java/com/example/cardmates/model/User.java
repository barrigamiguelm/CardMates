package com.example.cardmates.model;

public class User {

    private String user_id;
    private String Email;
    private String Name;
    private String Date;
    private String Description;

    public User(){}
    public User(String user_id, String email, String name, String date, String description) {
        this.user_id = user_id;
        Email = email;
        Name = name;
        Date = date;
        Description = description;
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
