package com.codepath.project.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.project.android.R;
import com.codepath.project.android.model.ViewType;
import com.codepath.project.android.model.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductsAdapter extends
        RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

    private List<Product> mProducts;
    private Context mContext;
    private ViewType mViewType;

    public ProductsAdapter(Context context, List<Product> products, ViewType viewType) {
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
        public TextView tvRatingCount;
        public TextView tvProductPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            tvProductNAme = (TextView) itemView.findViewById(R.id.tv_product_name);
            ivProductImage = (ImageView) itemView.findViewById(R.id.iv_product);
            tvRatingCount = (TextView) itemView.findViewById(R.id.tv_total_votes);
            ratingBar = (RatingBar) itemView.findViewById(R.id.rating);
            tvProductPrice = (TextView) itemView.findViewById(R.id.tv_product_price);
        }
    }

    @Override
    public ProductsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = null;
        if(mViewType == ViewType.VERTICAL_GRID){
            contactView = inflater.inflate(R.layout.item_product_category, parent, false);
        } else if(mViewType == ViewType.HORIZONTAL){
            contactView = inflater.inflate(R.layout.item_product, parent, false);
        }
        return new ViewHolder(contactView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ProductsAdapter.ViewHolder viewHolder, int position) {
        Product product = mProducts.get(position);

        ImageView ivProductImage = viewHolder.ivProductImage;
        Picasso.with(getContext()).load(product.getImageUrl()).placeholder(R.drawable.placeholder).into(ivProductImage);
        TextView tvRatingCount = viewHolder.tvRatingCount;
        tvRatingCount.setText(" "+product.getRatingCount()+" reviews");


        if(mViewType == ViewType.VERTICAL_GRID){
            RatingBar ratingBar = viewHolder.ratingBar;
            ratingBar.setRating((int)product.getAverageRating());
            TextView tvProductName = viewHolder.tvProductNAme;

            String sourceString = "<font color='black'>"+"<b>" + "$"+product.getPrice() + "</b> "+"</font>"+"&nbsp;"+product.getName();
            tvProductName.setText(Html.fromHtml(sourceString));

        } else{
            TextView tvProductPrice = viewHolder.tvProductPrice;
            tvProductPrice.setText("$"+product.getPrice());

            TextView tvProductName = viewHolder.tvProductNAme;
            tvProductName.setText(product.getName());
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mProducts.size();
    }
}