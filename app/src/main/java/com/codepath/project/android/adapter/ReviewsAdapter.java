package com.codepath.project.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.project.android.R;
import com.codepath.project.android.activities.UserDetailActivity;
import com.codepath.project.android.helpers.CircleTransform;
import com.codepath.project.android.model.AppUser;
import com.codepath.project.android.model.Review;
import com.codepath.project.android.utils.GeneralUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewsAdapter extends
        RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private List<Review> mReviews;
    private Context mContext;

    public ReviewsAdapter(Context context, List<Review> reviews) {
        mReviews = reviews;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivProfile) ImageView ivProfile;
        @BindView(R.id.tvReview) TextView tvReview;
        @BindView(R.id.tvUserName) TextView tvUserName;
        @BindView(R.id.rating) RatingBar rating;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_review, parent, false);
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapter.ViewHolder viewHolder, int position) {
        Review review = mReviews.get(position);
        AppUser user = (AppUser) review.getUser();
        if(user != null) {
            String upperString = user.getString("firstName").substring(0,1).toUpperCase() + user.getString("firstName").substring(1);
            viewHolder.tvUserName.setText(upperString);
            if(!TextUtils.isEmpty(user.getImage())) {
                Picasso.with(getContext()).load(user.getImage()).transform(new CircleTransform()).into(viewHolder.ivProfile);
            } else {
                Picasso.with(getContext()).load(GeneralUtils.getProfileUrl(user.getObjectId())).transform(new CircleTransform()).into(viewHolder.ivProfile);
            }
            viewHolder.ivProfile.setOnClickListener(v -> startUserDetailActivity(user.getObjectId()));
        }
        viewHolder.tvReview.setText(review.getText());
        viewHolder.rating.setRating(review.getRating());
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    private void startUserDetailActivity(String userId) {
        Intent intent = new Intent(getContext(), UserDetailActivity.class);
        intent.putExtra("USER_ID", userId);
        getContext().startActivity(intent);
    }

}
