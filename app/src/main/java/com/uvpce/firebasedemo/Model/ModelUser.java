package com.uvpce.firebasedemo.Model;

public class ModelUser {
    private String email, id, password, username;

    public ModelUser() {
    }

    public ModelUser(String email, String id, String password, String username) {
        this.email = email;
        this.id = id;
        this.password = password;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
