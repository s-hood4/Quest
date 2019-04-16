package com.codepath.project.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.project.android.R;
import com.codepath.project.android.activities.ProductViewActivity;
import com.codepath.project.android.adapter.ProductsAdapter;
import com.codepath.project.android.helpers.ItemClickSupport;
import com.codepath.project.android.model.Product;
import com.codepath.project.android.model.ViewType;
import com.codepath.project.android.network.ParseHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by anmallya on 11/18/2016.
 */

public class CategoryDetailFragment extends Fragment {

        @BindView(R.id.rv_category_detail)
        RecyclerView rvCategoryDetail;

        protected static final String CATEGORY = "CATEGORY";
        protected static final String SUB_CATEGORY = "SUB_CATEGORY";


        protected ArrayList<Product> productList;
        protected ProductsAdapter productsAdapter;
        private String mCategory;
        private String mSubCategory;
        private static final int GRID_COLUMN_COUNT = 2;

        public CategoryDetailFragment() {
        }

        public static CategoryDetailFragment newInstance(String category, String subCategory) {
            CategoryDetailFragment fragment = new CategoryDetailFragment();
            Bundle args = new Bundle();
            args.putString(CATEGORY, category);
            args.putString(SUB_CATEGORY, subCategory);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                mCategory = getArguments().getString(CATEGORY);
                mSubCategory = getArguments().getString(SUB_CATEGORY);
            }
            productList = ParseHelper.getProductsForCategory(mCategory, mSubCategory);
            productsAdapter = new ProductsAdapter(getActivity(), productList, ViewType.VERTICAL_GRID);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_category_detail, container, false);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            ButterKnife.bind(this, view);
            setRecyclerView();
        }

        private void setRecyclerView(){
            StaggeredGridLayoutManager layoutManagerCategoryDetail = new StaggeredGridLayoutManager(GRID_COLUMN_COUNT, 1);
            rvCategoryDetail.setAdapter(productsAdapter);
            rvCategoryDetail.setLayoutManager(layoutManagerCategoryDetail);

            ItemClickSupport.addTo(rvCategoryDetail).setOnItemClickListener(
                    (recyclerView, position, v) -> {
                        Intent intent = new Intent(getActivity(), ProductViewActivity.class);
                        intent.putExtra("productId", productList.get(position).getObjectId());
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(getActivity(), v.findViewById(R.id.iv_product), "ivProductImage");
                        startActivity(intent, options.toBundle());
                    }
            );
        }
    }
