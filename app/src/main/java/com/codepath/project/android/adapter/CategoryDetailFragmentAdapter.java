package com.codepath.project.android.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.codepath.project.android.fragments.CategoryDetailFragment;
import com.codepath.project.android.helpers.SmartFragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anmallya on 11/18/2016.
 */

public class CategoryDetailFragmentAdapter extends SmartFragmentStatePagerAdapter {
        int mPageCount = 0;
        private Context context;
        private List<String> mSubCategoryList;
        private String mCategory;

        public CategoryDetailFragmentAdapter(FragmentManager fm, Context context, String category, List<String> subCategoryList) {
            super(fm);
            this.context = context;
            mPageCount =  subCategoryList.size();
            mSubCategoryList = subCategoryList;
            mCategory = category;
        }

        @Override
        public int getCount() {
            return mPageCount;
        }


        @Override
        public Fragment getItem(int position) {
            return CategoryDetailFragment.newInstance(mCategory, mSubCategoryList.get(position));
        }
    }

