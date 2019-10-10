package com.clr.cityfixer;

public class User {
    private String UserEmail;
    private String UserName;
    private int UserPoints;

    public User() {
    }

    public User(String userEmail, String userName, int userPoints) {
        UserEmail = userEmail;
        UserPoints = userPoints;
        UserName = userName;
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
}
