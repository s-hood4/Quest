package com.codepath.project.android.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.codepath.project.android.R;
import com.codepath.project.android.adapter.CategoryAdapter;
import com.codepath.project.android.helpers.ThemeUtils;
import com.codepath.project.android.model.Category;
import com.codepath.project.android.model.ViewType;
import com.codepath.project.android.network.ParseHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryActivity extends AppCompatActivity {

    @BindView(R.id.rv_categories)
    RecyclerView rvCategory;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_category);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Categories");
        setupRecyclerView();
    }

    private void setupRecyclerView(){
        ArrayList<Category> categoryList = new ArrayList<>();
        CategoryAdapter categoryAdapter = new CategoryAdapter(this, categoryList, ViewType.VERTICAL);
        categoryList.addAll(ParseHelper.getCategoryList());
        rvCategory.setAdapter(categoryAdapter);
        rvCategory.setLayoutManager(new LinearLayoutManager(this));
    }
}
