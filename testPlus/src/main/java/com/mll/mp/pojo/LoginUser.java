package com.mll.mp.pojo;

import java.io.Serializable;

public class LoginUser implements Serializable {
    private int id;
    private String username;
    private String userpwd;

    public LoginUser(){

    }

    public LoginUser(String account){
        this.username = account;
    }

    public LoginUser(int id, String username, String userpwd) {
        this.id = id;
        this.username = username;
        this.userpwd = userpwd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserpwd() {
        return userpwd;
    }

    public void setUserpwd(String userpwd) {
        this.userpwd = userpwd;
    }
}
