package com.example.bookitapp;

import java.io.Serializable;

public class UserInfo implements Serializable {
    int id;
    String username;
    String password;
    String fn;
    String ln;

    public UserInfo (int id,String un, String pass, String fn, String ln){
        this.id=id;
        this.username=un;
        this.password=pass;
        this.fn = fn;
        this.ln=ln;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFn() {
        return fn;
    }

    public String getLn() {
        return ln;
    }

    public int getId(){
        return this.id;
    }

}
