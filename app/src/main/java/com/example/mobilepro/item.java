package com.example.mobilepro;

import java.util.List;

public class item {

    private String name;
    private String city;
    private double latitude;
    private double longitude;
    private String address;
    private String image;
    private String shopName;
    private double price;
    private String phone;
    private String description;
    private String time;
    private List<String> reviews;
    private String tags[];
    private List<item> recommendations;

    private void createTags(){
        tags = name.split(" ");
    }

    public item(String name, String address, String shopName, String image, double price)
    {
        this.name = name;
        createTags();
        this.address = address;
        this.image = image;
        this.shopName = shopName;
        this.price = price;
    }

    public item(String name, String address, String image, String shopName, double price, String phone, String description, String time, String city, List<String> reviews, List<item> recommendations, double latitude, double longitude){
        this.name = name;
        createTags();
        this.address = address;
        this.image = image;
        this.shopName = shopName;
        this.price = price;
        this.phone = phone;
        this.description = description;
        this.time = time;
        this.reviews = reviews;
        this.recommendations = recommendations;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getAddress(){
        return address;
    }

    public String getImage() {
        return image;
    }

    public String getShopName() {
        return shopName;
    }

    public double getPrice() {
        return price;
    }

    public String getPhone() {
        return phone;
    }

    public String getDescription() {
        return description;
    }

    public String getTime() {
        return time;
    }

    public List<String> getReviews() {
        return reviews;
    }

    public List<item> getRecommendations() {
        return recommendations;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String[] getTags() {
        return tags;
    }

    public String getCity() { return city; }
}
