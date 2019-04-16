package com.codepath.project.android.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.project.android.R;
import com.like.LikeButton;

public class FeedHolder extends RecyclerView.ViewHolder {

    public ImageView ivProfile;
    public ImageView ivProductImage;
    public TextView tvContent;
    public TextView tvUserName;
    public TextView tvTime;
    public TextView tvDestName;
    public TextView tvUserAction;
    public RatingBar rating;
    public LikeButton likeButton;
    public TextView tvLikeCount;
    public LinearLayout line;

    public FeedHolder(View v) {
        super(v);
        tvContent = (TextView) v.findViewById(R.id.tvContent);
        ivProfile = (ImageView) v.findViewById(R.id.ivProfile);
        tvUserName = (TextView) v.findViewById(R.id.tvUserName);
        rating = (RatingBar) v.findViewById(R.id.rating);
        ivProductImage = (ImageView) v.findViewById(R.id.ivProductImage);
        tvTime = (TextView) v.findViewById(R.id.tvTime);
        tvDestName = (TextView) v.findViewById(R.id.tvDestName);
        tvUserAction  = (TextView) v.findViewById(R.id.tvUserAction);
        likeButton = (LikeButton) v.findViewById(R.id.likeButton);
        tvLikeCount = (TextView) v.findViewById(R.id.tvLikeCount);
        line = (LinearLayout) v.findViewById(R.id.line);
    }
}