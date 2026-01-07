package com.techrent.service;

public class AuthService {

    public boolean authentifier(String username, String password) {
        return "admin".equals(username) && "admin123".equals(password);
    }
}