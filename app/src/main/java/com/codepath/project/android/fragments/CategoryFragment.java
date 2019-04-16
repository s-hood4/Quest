package com.codepath.project.android.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.project.android.R;
import com.codepath.project.android.adapter.CategoryDetailFragmentAdapter;
import com.codepath.project.android.helpers.Constants;
import com.codepath.project.android.network.ParseHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by anmallya on 11/21/2016.
 */

public class CategoryFragment extends Fragment {
    public static final String ARG_CATEGORY = "CATEGORY";

    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.sliding_tabs)
    TabLayout tabLayout;

    private static final int MAX_TABS_SHOWN = 5;
    private String category;
    private ArrayList<String> topSubCategoryList;


    public static CategoryFragment newInstance(String page) {
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, page);
        CategoryFragment fragment = new CategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString(ARG_CATEGORY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_category_detail, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        setupTabs();
    }



    private void setupTabs(){
        topSubCategoryList = new ArrayList<>();
        getTabNameList();
        viewPager.setAdapter(new CategoryDetailFragmentAdapter(getActivity().getSupportFragmentManager(),
                getActivity(),
                category,
                topSubCategoryList));
        tabLayout = (TabLayout) getActivity().findViewById(R.id.sliding_tabs);
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
