package com.codepath.project.android.model;

import com.codepath.project.android.data.TestData;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;

import java.util.ArrayList;

@ParseClassName("Product")
public class Product extends ParseObject {

    public Product() {
        super();
    }

    public String getPrice() {
        return getString("price");
    }

    public void setPrice(String price) {
        put("price", price);
    }

    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        put("name", name);
    }

    public String getBrand() {
        return getString("brand");
    }

    public void setBrand(String brand) {
        put("brand", brand);
    }

    public int getRatingCount() {
        return getInt("ratingCount");
    }

    public void incrementRatingCount() {
        this.increment("ratingCount");
    }

    public int getTotalRating() {
        return getInt("totalRating");
    }

    public void addToTotalRating(int rating) {
        this.increment("totalRating", rating);
    }

    public double getAverageRating() {
        return getDouble("averageRating");
    }

    public void setRating(int rating) {
        addToTotalRating(rating);
        int totalRating = getTotalRating();
        int oldratingCount = getRatingCount();
        incrementRatingCount();
        int ratingCount = getRatingCount();
        double averageRating = (double) totalRating/ratingCount;
        put("averageRating", averageRating);
    }

    public int getViews() {
        return getInt("views");
    }

    public void incrementViews() {
        this.increment("views");
    }

    public int getReviewCount() {
        return getInt("reviewCount");
    }

    public void incrementReviewCount() {
        this.increment("reviewCount");
    }

    public String getImageUrl() {
        return getString("imageUrl");
    }

    public JSONArray getImageSetUrls() {
        return getJSONArray("imageSetUrls");
    }

    public void setImageUrl(String imageUrl) {
        put("imageUrl", imageUrl);
    }

    public String getVideo() {
        // the stored videoUrl points to full youtube link
        // have to split the url for the "id"
        String videoUrl = getString("videoUrl");
        String videoId = videoUrl.split("=")[1];
        return videoId;
    }

    public JSONArray getVideoSetUrls() {
        return getJSONArray("videoSetUrls");
    }

    public String getCategory() {
        return getString("category");
    }

    public String getSubCategory() {
        return getString("subCategory");
    }
}
