package com.example.todomeet.model;

import com.kakao.sdk.user.model.Account;

public class User {
    private String userEmail;
    private String userName;
    private String profileImage;

    public User(String userEmail, String userName, String profileImage) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.profileImage = profileImage;
    }

}