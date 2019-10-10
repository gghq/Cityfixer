package com.clr.cityfixer;

import com.google.android.gms.maps.model.LatLng;

public class Post {
    private String Id;
    private String User;
    private String Title;
    private String Description;
    private Location Location;
    private String Image;
    private String Date;
    private String Category;
    private boolean Approved;

    public Post() {
    }

    public Post(String id, String user, String title, String description, Location location, String image, String date, String category, boolean approved) {
        Id = id;
        User = user;
        Title = title;
        Description = description;
        Location = location;
        Image = image;
        Date = date;
        Category = category;
        Approved = approved;
    }

    public Post(String user, String title, String description, Location location, String date, String category, boolean approved) {
        User = user;
        Title = title;
        Description = description;
        Location = location;
        Date = date;
        Category = category;
        Approved = approved;
    }

    public Location getLocation() {
        return Location;
    }

    public void setLocation(Location location) {
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

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
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
}

