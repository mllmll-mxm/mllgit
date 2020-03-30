package com.mll.mp.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

@TableName("user")
public class User implements Serializable {
    @TableId(value = "id",type = IdType.AUTO)
    private int id;

    @TableField(value = "username")
    private String username;

    @TableField(value = "userpwd")
    private String userpwd;

    @TableField(value = "salt")
    private String salt;

    @TableField(value = "phone")
    private String phone;

    @TableField(value = "email")
    private String email;

    public User(String username, String userpwd, String salt, String phone, String email) {
        this.username = username;
        this.userpwd = userpwd;
        this.salt = salt;
        this.phone = phone;
        this.email = email;
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", userpwd='" + userpwd + '\'' +
                '}';
    }

    public User(String username, String userpwd) {
        this.username = username;
        this.userpwd = userpwd;
    }

    public User() {
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

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
