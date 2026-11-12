package com.example.dublin8community.models;

import java.util.Locale;

public class User {

    public String name;
    public String email;
    public String role;
    public long createdAt;

    public User() {

    }

    public User(String name, String email, String role, long createdAt) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
    }
}
