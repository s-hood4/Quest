package com.codepath.project.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.project.android.R;
import com.codepath.project.android.activities.DetailedReviewActivity;
import com.codepath.project.android.activities.ProductViewActivity;
import com.codepath.project.android.helpers.ItemClickSupport;
import com.codepath.project.android.model.Review;
import com.codepath.project.android.utils.GeneralUtils;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserTimelineAdapter extends
        RecyclerView.Adapter<UserTimelineAdapter.ViewHolder> {

    private List<Review> mReviews;
    private Context mContext;

    public UserTimelineAdapter(Context context, List<Review> reviews) {
        mReviews = reviews;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivProfile) ImageView ivProfile;
        @BindView(R.id.tvReview) TextView tvReview;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public UserTimelineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_review_timeline, parent, false);
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(UserTimelineAdapter.ViewHolder viewHolder, int position) {
        Review review = mReviews.get(position);
        TextView tvReview = viewHolder.tvReview;
        ParseUser user = review.getUser();
        String formattedText = "";
        if(user != null) {
            Picasso.with(getContext()).load(GeneralUtils.getProfileUrl(user.getObjectId())).into(viewHolder.ivProfile);
        }
        formattedText += review.getText();
        tvReview.setText(Html.fromHtml(formattedText));
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }



}
