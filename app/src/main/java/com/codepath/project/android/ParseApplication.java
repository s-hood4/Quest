package com.codepath.project.android;

import android.app.Application;

import com.codepath.project.android.model.AppUser;
import com.codepath.project.android.model.Category;
import com.codepath.project.android.model.Feed;
import com.codepath.project.android.model.Product;
import com.codepath.project.android.model.Recommend;
import com.codepath.project.android.model.Review;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.interceptors.ParseLogInterceptor;

public class ParseApplication extends Application {

    public static int currentPosition;

    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Recommend.class);
        ParseObject.registerSubclass(AppUser.class);
        ParseObject.registerSubclass(Product.class);
        ParseObject.registerSubclass(Review.class);
        ParseObject.registerSubclass(Category.class);
        ParseObject.registerSubclass(Feed.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("myAppId")
                .clientKey(null)
                .addNetworkInterceptor(new ParseLogInterceptor())
                .server("https://codepath.herokuapp.com/parse/").build());

        ParseFacebookUtils.initialize(this);
    }
}