package com.example.songsapp;

public class UserInfo{
    public String username;
    public String firstname;
    public String lastname;

    public static UserInfo shared = new UserInfo();

    private UserInfo() {};
}
