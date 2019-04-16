package com.codepath.project.android.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

@ParseClassName("Recommend")
public class Recommend extends ParseObject {

    public Recommend() {
        super();
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

    public ParseUser getUser() {
        return getParseUser("user");
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

    public List<ParseUser> getFacebookFriend() {
        return (List<ParseUser>) get("facebookFriends");
    }
}
