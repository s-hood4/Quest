package com.codepath.project.android.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.project.android.R;
import com.codepath.project.android.adapter.ProductsFragmentPagerAdapter;

import butterknife.BindView;

public class MyProductsFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    @BindView(R.id.rv_products)
    RecyclerView rvProducts;
    @BindView(R.id.rv_categories)
    RecyclerView rvCategory;
    @BindView(R.id.rv_reviews)
    RecyclerView rvReviews;

    public static MyProductsFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        MyProductsFragment fragment = new MyProductsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_products, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new ProductsFragmentPagerAdapter(getActivity().getSupportFragmentManager(),
                getActivity()));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
}
