package com.yasingok.instagram.Classes;

public class Post {
    public String mail;
    public String postUrl;
    public String profileUrl;
    public String username;
    public String title;

    public Post(String mail, String postUrl, String profileUrl, String username, String title) {
        this.mail = mail;
        this.postUrl = postUrl;
        this.profileUrl = profileUrl;
        this.username = username;
        this.title = title;
    }
}