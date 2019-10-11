package com.clr.cityfixer;

public class User {
    private String UserId;
    private String UserEmail;
    private String UserName;
    private int UserPoints;

    public User() {
    }

    public User(String userId, String userEmail, String userName, int userPoints) {
        UserEmail = userEmail;
        UserPoints = userPoints;
        UserName = userName;
        UserId = userId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public int getUserPoints() {
        return UserPoints;
    }

    public void setUserPoints(int userPoints) {
        UserPoints = userPoints;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }
}
