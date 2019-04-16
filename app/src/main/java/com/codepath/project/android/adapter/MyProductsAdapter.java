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
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class MyProductsAdapter extends
        RecyclerView.Adapter<MyProductsAdapter.ViewHolder> {

    private List<Product> mProducts;
    private Context mContext;

    public MyProductsAdapter(Context context, List<Product> products) {
        mProducts = products;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvProductName;
        public ImageView ivProductImage;
        public TextView tvPrice;
        public RatingBar rating;
        public TextView tvRatingCount;

        public ViewHolder(View itemView) {
            super(itemView);
            tvProductName = (TextView) itemView.findViewById(R.id.tvProductName);
            ivProductImage = (ImageView) itemView.findViewById(R.id.ivProduct);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            rating = (RatingBar) itemView.findViewById(R.id.rating);
            tvRatingCount = (TextView) itemView.findViewById(R.id.tvRatingCount);
        }
    }

    @Override
    public MyProductsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_my_product, parent, false);
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(MyProductsAdapter.ViewHolder viewHolder, int position) {
        Product product = mProducts.get(position);
            product.fetchIfNeededInBackground((object, e) -> {
                if(e == null) {
                    Product temp = (Product) object;
                    viewHolder.tvProductName.setText(temp.getName());
                    viewHolder.tvPrice.setText("$" + temp.getPrice());
                    viewHolder.rating.setRating((float) temp.getAverageRating());
                    viewHolder.tvRatingCount.setText("("+temp.getRatingCount()+")");
                    ImageView ivProductImage = viewHolder.ivProductImage;
                    Picasso.with(getContext()).load(temp.getImageUrl()).transform(new RoundedCornersTransformation(10, 10)).into(ivProductImage);
                }
            });
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mProducts.size();
    }
}