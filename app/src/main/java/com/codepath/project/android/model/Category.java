package com.codepath.project.android.model;

import com.codepath.project.android.adapter.CategoryAdapter;
import com.codepath.project.android.data.TestData;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anmallya on 11/18/2016.
 */

@ParseClassName("Category")
public class Category extends ParseObject {

    public Category() {
        super();
    }

    public String getName() {
        return getString("categoryName");
    }

    public void setName(String name) {
        put("categoryName", name);
    }

    public String getImageUrl() {
        return getString("imageUrl");
    }

    public void setImageUrl(String imageUrl) {
        put("imageUrl", imageUrl);
    }
}
