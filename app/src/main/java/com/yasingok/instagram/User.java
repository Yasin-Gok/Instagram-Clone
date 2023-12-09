package com.yasingok.instagram;

public class User {

    private String Username, Name, Surname, Mail;

    public User(String username, String name, String surname, String mail) {
        Username = username;
        Name = name;
        Surname = surname;
        Mail = mail;
    }

    public User() {
        // Boş constructor gereklidir
    }

    public String getUsername() {
        return Username;
    }

    public String getName() {
        return Name;
    }

    public String getSurname() {
        return Surname;
    }

    public String getMail() {
        return Mail;
    }
}