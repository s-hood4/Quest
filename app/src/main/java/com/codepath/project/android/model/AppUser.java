package com.codepath.project.android.model;

import com.parse.ParseClassName;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

@ParseClassName("_User")
public class AppUser extends ParseUser {

    public void addShelfProduct(Product product) {
        addUnique("shelfProducts", product);
    }

    public List<Product> getShelfProducts() {
        return (List<Product>) get("shelfProducts");
    }

    public void removeShelfProduct(Product product) {
        removeAll("shelfProducts", Arrays.asList(product));
    }

    public void addWishListProduct(Product product) {
        addUnique("wishListProducts", product);
    }

    public List<Product> getWishListProducts() {
        return (List<Product>) get("wishListProducts");
    }

    public void removeWishListProduct(Product product) {
        removeAll("wishListProducts", Arrays.asList(product));
    }

    public List<ParseUser> getFollowUsers() {
        return (List<ParseUser>) get("followUsers");
    }

    public void setFollowUsers(ParseUser user) {
        addUnique("followUsers", user);
    }

    public void removeFollowUser(ParseUser user) {
        removeAll("followUsers", Arrays.asList(user));
    }

    public List<Product> getFollowProducts() {
        return (List<Product>) get("followProducts");
    }

    public void setFollowProducts(Product product) {
        addUnique("followProducts", product);
    }

    public void removeFollowProduct(Product product) {
        removeAll("followProducts", Arrays.asList(product));
    }

    public String getImage() {
        return getString("pictureUrl");
    }

    public String getFirstName() {
        return getString("firstName");
    }

    public List<ParseUser> getFollowingUsers() {
        return (List<ParseUser>) get("followingUsers");
    }

    public void setFollowingUsers(ParseUser user) {
        addUnique("followingUsers", user);
    }

    public void removeFollowingUser(ParseUser user) {
        removeAll("followingUsers", Arrays.asList(user));
    }
}