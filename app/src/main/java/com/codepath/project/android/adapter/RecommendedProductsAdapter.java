package com.codepath.project.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.project.android.R;
import com.codepath.project.android.model.Product;
import com.codepath.project.android.model.ViewType;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecommendedProductsAdapter extends
        RecyclerView.Adapter<RecommendedProductsAdapter.ViewHolder> {

    private List<Product> mProducts;
    private Context mContext;
    private ViewType mViewType;

    public RecommendedProductsAdapter(Context context, List<Product> products, ViewType viewType) {
        mProducts = products;
        mContext = context;
        mViewType =  viewType;
    }

    private Context getContext() {
        return mContext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvProductNAme;
        public ImageView ivProductImage;
        public RatingBar ratingBar;
        public TextView tvFriendsCount;

        public ViewHolder(View itemView) {
            super(itemView);
            tvProductNAme = (TextView) itemView.findViewById(R.id.tv_product_name);
            ivProductImage = (ImageView) itemView.findViewById(R.id.iv_product);
            ratingBar = (RatingBar) itemView.findViewById(R.id.rating);
            tvFriendsCount = (TextView) itemView.findViewById(R.id.tv_friends_count);
        }
    }

    @Override
    public RecommendedProductsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_recommended_products, parent, false);
        return new ViewHolder(contactView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(RecommendedProductsAdapter.ViewHolder viewHolder, int position) {
        Product product = mProducts.get(position);
        ImageView ivProductImage = viewHolder.ivProductImage;
        Picasso.with(getContext()).load(product.getImageUrl()).placeholder(R.drawable.placeholder).into(ivProductImage);

        TextView tvProductName = viewHolder.tvProductNAme;
        tvProductName.setText(product.getName());

        if(product.getInt("tempCount") > 1) {
            viewHolder.tvFriendsCount.setText(product.getInt("tempCount") + " friends own this");
        } else  {
            viewHolder.tvFriendsCount.setText(product.getInt("tempCount") + " friend owns this");
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mProducts.size();
    }
}