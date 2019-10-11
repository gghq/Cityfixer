package com.clr.cityfixer;

import com.google.android.gms.maps.model.LatLng;

public class Post {
    private String Id;
    private User User;
    private String Description;
    private PostLocation Location;
    private String Image;
    private String Date;
    private String Category;
    private int Priority;
    private boolean Approved;

    public Post() {
    }

    public Post(String id, User user, String description, PostLocation location, String image, String date, String category, boolean approved, int priority) {
        Id = id;
        User = user;
        Description = description;
        Location = location;
        Image = image;
        Date = date;
        Category = category;
        Approved = approved;
        Priority = priority;
    }

    public Post(User user, String description, PostLocation location, String date, String category, boolean approved, int priority) {
        User = user;
        Description = description;
        Location = location;
        Date = date;
        Category = category;
        Approved = approved;
        Priority = priority;
    }

    public PostLocation getLocation() {
        return Location;
    }

    public void setLocation(PostLocation location) {
        Location = location;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public User getUser() {
        return User;
    }

    public void setUser(User user) {
        User = user;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public boolean isApproved() {
        return Approved;
    }

    public void setApproved(boolean approved) {
        Approved = approved;
    }

    public int getPriority() {
        return Priority;
    }

    public void setPriority(int priority) {
        Priority = priority;
    }
}

