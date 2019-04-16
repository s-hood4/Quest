package com.codepath.project.android.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.codepath.project.android.R;
import com.codepath.project.android.adapter.CategoryDetailFragmentAdapter;
import com.codepath.project.android.helpers.Constants;
import com.codepath.project.android.helpers.ThemeUtils;
import com.codepath.project.android.network.ParseHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.codepath.project.android.data.TestData.tabNameList;

public class CategoryDetailActivity extends AppCompatActivity {

    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.sliding_tabs)
    TabLayout tabLayout;

    private static final int MAX_TABS_SHOWN = 4;
    private String category;
    private ArrayList<String> topSubCategoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_category_detail);
        category  = getIntent().getStringExtra(Constants.CATEGORY);
        ButterKnife.bind(this);
        setupTabs();
    }

    private void setupTabs(){
        topSubCategoryList = new ArrayList<>();
        getTabNameList();
        viewPager.setAdapter(new CategoryDetailFragmentAdapter(getSupportFragmentManager(),
                this,
                category,
                topSubCategoryList));
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setText(topSubCategoryList.get(i));
        }
    }

    private void getTabNameList(){
        List<String> subCatList = ParseHelper.getSubCategoryList(category);
        topSubCategoryList.add(Constants.ALL);
        int length = (subCatList.size() > (MAX_TABS_SHOWN - 1))? (MAX_TABS_SHOWN - 1): subCatList.size();
        for(int i = 0; i < length; i ++){
            topSubCategoryList.add(subCatList.get(i));
        }
    }
}
