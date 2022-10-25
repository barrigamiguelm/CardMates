package com.example.cardmates.Activities.model;

public class User {

    private String user_id;
    private String Email;
    private String Usuario;

    public User(String user_id, String email, String username) {
        this.user_id = user_id;
        this.Email = email;
        this.Usuario = username;
    }
    public User(){

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
        this.Email = email;
    }

    public String getUsername() {
        return Usuario;
    }

    public void setUsername(String username) {
        this.Usuario = username;
    }


    @Override
    public String toString() {
        return "User{" +
                "user_id='" + user_id + '\'' +
                ", email='" + Email + '\'' +
                ", username='" + Usuario + '\'' +
                '}';
    }
}
