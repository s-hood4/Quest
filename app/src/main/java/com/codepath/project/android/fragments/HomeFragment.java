package com.codepath.project.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.project.android.R;
import com.codepath.project.android.activities.ProductViewActivity;
import com.codepath.project.android.adapter.CategoryAdapter;
import com.codepath.project.android.adapter.ProductsAdapter;
import com.codepath.project.android.adapter.RecommendedProductsAdapter;
import com.codepath.project.android.helpers.ItemClickSupport;
import com.codepath.project.android.model.AppUser;
import com.codepath.project.android.model.Category;
import com.codepath.project.android.model.Product;
import com.codepath.project.android.model.ViewType;
import com.codepath.project.android.network.ParseHelper;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    @BindView(R.id.rv_products)
    RecyclerView rvProducts;
    @BindView(R.id.rv_categories)
    RecyclerView rvCategory;
    @BindView(R.id.rv_reviews)
    RecyclerView rvReviews;
    @BindView(R.id.rv_recommended_products)
    RecyclerView rvRecommendedProducts;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tv_categories)
    TextView tvCategories;
    @BindView(R.id.tv_popular_products)
    TextView tvPopularProducts;
    @BindView(R.id.tv_popular_reviews)
    TextView tvPopularReviews;
    @BindView(R.id.tv_recommended_products)
    TextView tvRecommendedProducts;

    public static final int GRID_ROW_COUNT = 1;

    ProductsAdapter productsAdapter;
    List<Product> products;


    public static HomeFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        HomeFragment fragment = new HomeFragment();
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        setRecycleView();
    }

    private void setRecycleView() {
        progressBar.setVisibility(View.VISIBLE);
        tvCategories.setVisibility(View.INVISIBLE);
        tvPopularProducts.setVisibility(View.INVISIBLE);
        tvPopularReviews.setVisibility(View.INVISIBLE);
        setCategories();
        setRecommendedProducts();
        setPopularProducts();
        setBestRatedProducts();
    }

    private void setCategories(){
        GridLayoutManager layoutManagerCategory = new GridLayoutManager(getActivity(), GRID_ROW_COUNT, GridLayoutManager.HORIZONTAL, false);
        ArrayList<Category> categoryList = new ArrayList<>();
        CategoryAdapter categoryAdapter = new CategoryAdapter(getActivity(), categoryList, ViewType.GRID);
        ParseHelper.createCategoryListFromProducts(categoryList, categoryAdapter, tvCategories, progressBar);
        rvCategory.setAdapter(categoryAdapter);
        rvCategory.setLayoutManager(layoutManagerCategory);

        ItemClickSupport.addTo(rvCategory).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    CategoryFragment nextFrag = CategoryFragment.newInstance(categoryList.get(position).getName());
                    this.getFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                            .add(R.id.fragment_container, nextFrag, "TAG")
                            .addToBackStack(null)
                            .commit();

                }
        );
    }

    private void setRecommendedProducts(){
        LinearLayoutManager layoutManagerProducts
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvRecommendedProducts.setLayoutManager(layoutManagerProducts);
        List<Product> recProducts = new ArrayList<>();
        RecommendedProductsAdapter recommendedProductsAdapter = new RecommendedProductsAdapter(getActivity(), recProducts, ViewType.HORIZONTAL);
        rvRecommendedProducts.setAdapter(recommendedProductsAdapter);

        tvRecommendedProducts.setVisibility(View.GONE);

        ItemClickSupport.addTo(rvRecommendedProducts).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    Intent intent = new Intent(getActivity(), ProductViewActivity.class);
                    intent.putExtra("productId", recProducts.get(position).getObjectId());
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(getActivity(), v.findViewById(R.id.iv_product), "ivProductImage");
                    startActivity(intent, options.toBundle());
                }
        );
        recommendedProductsAdapter.notifyDataSetChanged();

        if(ParseUser.getCurrentUser() != null) {
            AppUser cuser = (AppUser) ParseUser.getCurrentUser();
            List<ParseUser> followUsers = cuser.getFollowUsers();
            if (followUsers != null && followUsers.size() > 0) {
                ParseObject.fetchAllIfNeededInBackground(followUsers, (objects, e) -> {
                    ConcurrentMap<String, Integer> productMap = new ConcurrentHashMap<>();
                    for (ParseUser u1 : objects) {
                        AppUser a1 = (AppUser) u1;
                        List<Product> plist = a1.getShelfProducts();
                        if(plist != null && plist.size() > 0) {
                            for (Product p1 : plist) {
                                if (!productMap.containsKey(p1.getObjectId())) {
                                    productMap.put(p1.getObjectId(), 1);
                                } else {
                                    Integer val = productMap.get(p1.getObjectId());
                                    val++;
                                    productMap.replace(p1.getObjectId(), val);
                                }
                            }
                        }
                    }

                    if(productMap.size() == 0) {
                        return;
                    }

                    tvRecommendedProducts.setVisibility(View.VISIBLE);
                    List<Map.Entry<String, Integer>> listOfentrySet = new ArrayList<>(productMap.entrySet());

                    Collections.sort(listOfentrySet, new SortByValue());

                    for (Map.Entry<String, Integer> map : listOfentrySet) {
                        String id = map.getKey();
                        Integer pCount = map.getValue();
                        ParseQuery<Product> q = ParseQuery.getQuery(Product.class);
                        q.getInBackground(id, (object, e1) -> {
                            object.put("tempCount", pCount);
                            recProducts.add(object);
                            recommendedProductsAdapter.notifyDataSetChanged();
                        });
                    }
                });
            }
        }
    }

    private void setPopularProducts(){
        LinearLayoutManager layoutManagerProducts
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvProducts.setLayoutManager(layoutManagerProducts);
        products = new ArrayList<>();
        productsAdapter = new ProductsAdapter(getActivity(), products, ViewType.HORIZONTAL);
        rvProducts.setAdapter(productsAdapter);

        ItemClickSupport.addTo(rvProducts).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    Intent intent = new Intent(getActivity(), ProductViewActivity.class);
                    intent.putExtra("productId", products.get(position).getObjectId());
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(getActivity(), v.findViewById(R.id.iv_product), "ivProductImage");
                    startActivity(intent, options.toBundle());
                }
        );
        productsAdapter.notifyDataSetChanged();

        ParseQuery<Product> query = ParseQuery.getQuery(Product.class);
        query.addDescendingOrder("views");
        query.setLimit(20);
        query.findInBackground((productList, e) -> {
            products.addAll(productList);
            productsAdapter.notifyDataSetChanged();
            tvPopularProducts.setVisibility(View.VISIBLE);
        });
    }


    private void setBestRatedProducts(){
        LinearLayoutManager layoutManagerReviews
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        ArrayList<Product> productsBestRated = new ArrayList<>();
        ProductsAdapter reviewsAdapter = new ProductsAdapter(getActivity(), productsBestRated, ViewType.HORIZONTAL);
        rvReviews.setAdapter(reviewsAdapter);
        rvReviews.setLayoutManager(layoutManagerReviews);

        ParseQuery<Product> queryByBestRating = ParseQuery.getQuery(Product.class);
        queryByBestRating.addDescendingOrder("averageRating");
        queryByBestRating.setLimit(20);
        queryByBestRating.findInBackground((productList, e) -> {
            productsBestRated.addAll(productList);
            reviewsAdapter.notifyDataSetChanged();
            tvPopularReviews.setVisibility(View.VISIBLE);
        });

        ItemClickSupport.addTo(rvReviews).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    Intent intent = new Intent(getActivity(), ProductViewActivity.class);
                    intent.putExtra("productId", productsBestRated.get(position).getObjectId());
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(getActivity(), v.findViewById(R.id.iv_product), "ivProductImage");
                    startActivity(intent, options.toBundle());
                }
        );
    }

    class SortByValue implements Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare( Map.Entry<String,Integer> entry1, Map.Entry<String,Integer> entry2){
            return (entry2.getValue()).compareTo(entry1.getValue());
        }
    }
}