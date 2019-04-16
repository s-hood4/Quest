package com.codepath.project.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.project.android.R;
import com.codepath.project.android.activities.ProductViewActivity;
import com.codepath.project.android.activities.SplashScreenActivity;
import com.codepath.project.android.adapter.MyProductsAdapter;
import com.codepath.project.android.helpers.ItemClickSupport;
import com.codepath.project.android.model.AppUser;
import com.codepath.project.android.model.Product;
import com.parse.ParseUser;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class ProductsListFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private MyProductsAdapter productsAdapter;
    private List<Product> productsList;

    private int mPage;

    public static ProductsListFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        ProductsListFragment fragment = new ProductsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        productsList = new ArrayList<>();
        productsAdapter = new MyProductsAdapter(getActivity(), productsList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products_list, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rvMyProducts);
        TextView tvHeading = (TextView) view.findViewById(R.id.tvHeading);
        TextView tvHeadingTitle = (TextView) view.findViewById(R.id.tvHeadingTitle);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).build());
        recyclerView.setAdapter(productsAdapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mLayoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(mLayoutManager);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(
                (rview, position, v) -> {
                    Intent intent = new Intent(getActivity(), ProductViewActivity.class);
                    intent.putExtra("productId", productsList.get(position).getObjectId());
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(getActivity(), v.findViewById(R.id.ivProduct), "ivProductImage");
                    startActivity(intent, options.toBundle());
                }
        );

        AppUser user = (AppUser) ParseUser.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getActivity(), SplashScreenActivity.class);
            startActivity(intent);
        } else {
            if (mPage == 1) {
                if(user.getShelfProducts() != null) {
                    productsList.addAll(user.getShelfProducts());
                }
                productsAdapter.notifyDataSetChanged();
            } else {
                if(user.getWishListProducts() != null) {
                    productsList.addAll(user.getWishListProducts());
                }
                productsAdapter.notifyDataSetChanged();
            }
            final Double sum = 0.0;
            if(productsList != null && productsList.size() > 0) {
                for (Product p : productsList) {
                    p.fetchIfNeededInBackground((object, e) -> {
                        Product t = (Product) object;
                        Double tempSum = 0.0;
                        if(!TextUtils.isEmpty(tvHeading.getText().toString())) {
                            tempSum = Double.parseDouble(tvHeading.getText().toString());
                        }
                        tempSum += Double.parseDouble(t.getPrice());
                        tvHeading.setText(tempSum.toString());
                    });
                }
            } else {
                tvHeadingTitle.setText("List is empty. Add products to list");
            }
        }
        return view;
    }
}
