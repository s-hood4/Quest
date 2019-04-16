package com.codepath.project.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.project.android.R;
import com.codepath.project.android.activities.DetailedReviewActivity;
import com.codepath.project.android.activities.SplashScreenActivity;
import com.codepath.project.android.activities.UserDetailActivity;
import com.codepath.project.android.helpers.CircleTransform;
import com.codepath.project.android.model.AppUser;
import com.codepath.project.android.model.Feed;
import com.codepath.project.android.model.Product;
import com.codepath.project.android.utils.GeneralUtils;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class ComplexRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Feed> items;
    private List<AppUser> mUsers;
    private Context mContext;

    private final int USER = 0, FEED = 1;

    public ComplexRecyclerViewAdapter(Context context, List<Feed> items, List<AppUser> users) {
        this.items = items;
        mContext = context;
        mUsers = users;
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 1) {
            return USER;
        } else {
            return FEED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case USER:
                View v1 = inflater.inflate(R.layout.horizonal_recommended_friends, viewGroup, false);
                viewHolder = new FriendsFeedHolder(v1);
                break;
            case FEED:
                View v2 = inflater.inflate(R.layout.item_feed, viewGroup, false);
                viewHolder = new FeedHolder(v2);
                break;
            default:
                View v3 = inflater.inflate(R.layout.item_feed, viewGroup, false);
                viewHolder = new FeedHolder(v3);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case USER:
                FriendsFeedHolder vh1 = (FriendsFeedHolder) viewHolder;
                configureFriendsFeedHolder(vh1);
                break;
            case FEED:
                FeedHolder vh2 = (FeedHolder) viewHolder;
                configureFeedHolder(vh2, position);
                break;
            default:
                FeedHolder vh3 = (FeedHolder) viewHolder;
                configureFeedHolder(vh3, position);
                break;
        }
    }

    private void configureFriendsFeedHolder(FriendsFeedHolder vh1) {
        if (mUsers != null && mUsers.size() > 0) {
            RecommendedFriendsAdapter friendsAdapter = new RecommendedFriendsAdapter(mContext, mUsers);
            vh1.rvFriends.setAdapter(friendsAdapter);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            mLayoutManager.scrollToPosition(0);
            //vh1.rvFriends.setNestedScrollingEnabled(false);
            vh1.rvFriends.setLayoutManager(mLayoutManager);
            vh1.rvFriends.setItemAnimator(new SlideInLeftAnimator());
            friendsAdapter.notifyDataSetChanged();
        } else {
            vh1.tvTitle.setVisibility(View.GONE);
            vh1.rvFriends.setVisibility(View.GONE);
            vh1.primerdivisor.setVisibility(View.GONE);
        }
    }

    private void configureFeedHolder(FeedHolder viewHolder, int position) {
        if(position > 0) {
            position--;
        }
        Feed feed = items.get(position);
        TextView tvContent = viewHolder.tvContent;
        AppUser fromUser = (AppUser) feed.getFromUser();
        Product toProduct = feed.getToProduct();
        AppUser toUser = (AppUser) feed.getToUser();
        viewHolder.rating.setVisibility(View.GONE);
        tvContent.setVisibility(View.GONE);
        viewHolder.line.setVisibility(View.GONE);
        String fromUserName;
        if(fromUser != null) {
            fromUserName = fromUser.getString("firstName").substring(0, 1).toUpperCase() + fromUser.getString("firstName").substring(1);
        } else {
            fromUserName = "Batman";
        }
        if(feed.getType().equals("followUser")) {
            viewHolder.tvUserAction.setText(" followed a user");
            String upperString = toUser.getString("firstName").substring(0,1).toUpperCase() + toUser.getString("firstName").substring(1);
            viewHolder.tvDestName.setText(upperString);
            Picasso.with(mContext).load(toUser.getImage()).into(viewHolder.ivProductImage);
        } else if(feed.getType().equals("followProduct")) {
            viewHolder.tvUserAction.setText(" followed a product");
            viewHolder.tvDestName.setText(toProduct.getName());
            Picasso.with(mContext).load(toProduct.getImageUrl()).into(viewHolder.ivProductImage);
        } else if(feed.getType().equals("likeReview")) {
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setVisibility(View.VISIBLE);
            viewHolder.line.setVisibility(View.VISIBLE);
            tvContent.setText(feed.getContent());
            viewHolder.tvUserAction.setText(" liked a review");
            viewHolder.rating.setVisibility(View.VISIBLE);
            viewHolder.rating.setRating(feed.getRating());
            viewHolder.tvDestName.setText(toProduct.getName());
            Picasso.with(mContext).load(toProduct.getImageUrl()).into(viewHolder.ivProductImage);
            tvContent.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, DetailedReviewActivity.class);
                intent.putExtra("reviewId", feed.getReview().getObjectId());
                mContext.startActivity(intent);
                ((Activity)mContext).overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            });
        } else if(feed.getType().equals("addPrice")) {
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText("Product price: $" + feed.getContent());
            viewHolder.tvUserAction.setText(" reported a price");
            viewHolder.tvDestName.setText(toProduct.getName());
            Picasso.with(mContext).load(toProduct.getImageUrl()).into(viewHolder.ivProductImage);
        } else if(feed.getType().equals("addReview")) {
            tvContent.setText(feed.getContent());
            tvContent.setVisibility(View.VISIBLE);
            viewHolder.line.setVisibility(View.VISIBLE);
            viewHolder.tvUserAction.setText(" reviewed a product");
            viewHolder.rating.setVisibility(View.VISIBLE);
            viewHolder.rating.setRating(feed.getRating());
            viewHolder.tvDestName.setText(toProduct.getName());
            Picasso.with(mContext).load(toProduct.getImageUrl()).into(viewHolder.ivProductImage);
            tvContent.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, DetailedReviewActivity.class);
                intent.putExtra("reviewId", feed.getReview().getObjectId());
                mContext.startActivity(intent);
                ((Activity)mContext).overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            });
        }
        viewHolder.tvUserName.setText(fromUserName);
        viewHolder.tvTime.setText(GeneralUtils.getRelativeTimeAgo(feed.getCreatedAt().toString()));
        setImage(fromUser, viewHolder);
        setUpOnLikeClickListener(viewHolder, feed);
    }

    private void setUpOnLikeClickListener(FeedHolder viewHolder, Feed feed) {

        if(feed.getType().equals("followProduct") || feed.getType().equals("followUser") || feed.getType().equals("addPrice")) {
            viewHolder.likeButton.setVisibility(View.GONE);
            viewHolder.tvLikeCount.setVisibility(View.GONE);
            return;
        }

        viewHolder.likeButton.setVisibility(View.VISIBLE);
        viewHolder.tvLikeCount.setVisibility(View.VISIBLE);

        if(ParseUser.getCurrentUser() != null) {
            List<ParseUser> likedUsers = feed.getReview().getLikedUsers();
            if(likedUsers != null && ifListContains(likedUsers, ParseUser.getCurrentUser())) {
                viewHolder.likeButton.setLiked(true);
            } else {
                viewHolder.likeButton.setLiked(false);
            }
        } else {
            viewHolder.likeButton.setLiked(false);
        }
        if(feed.getReview().getLikesCount() > 0) {
            viewHolder.tvLikeCount.setText(Integer.toString(feed.getReview().getLikesCount()) + " people like this");
        } else {
            viewHolder.tvLikeCount.setText("");
        }

        viewHolder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if(ParseUser.getCurrentUser() == null) {
                    Intent intent = new Intent(mContext, SplashScreenActivity.class);
                    mContext.startActivity(intent);
                } else {
                    int likes = feed.getReview().getLikesCount();
                    likes++;
                    feed.getReview().increment("likes");
                    feed.getReview().setLikedUsers(ParseUser.getCurrentUser());
                    viewHolder.tvLikeCount.setText(Integer.toString(likes)+ " people like this");
                    feed.getReview().saveInBackground();
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                if(ParseUser.getCurrentUser() == null) {
                    Intent intent = new Intent(mContext, SplashScreenActivity.class);
                    mContext.startActivity(intent);
                } else {
                    int likes = feed.getReview().getLikesCount();
                    likes--;
                    feed.getReview().increment("likes", -1);
                    feed.getReview().removeLikedUsers(ParseUser.getCurrentUser());
                    if(likes > 0) {
                        viewHolder.tvLikeCount.setText(Integer.toString(likes)+ " people like this");
                    } else {
                        viewHolder.tvLikeCount.setText("");
                    }
                    feed.getReview().saveInBackground();
                }
            }
        });
    }

    private boolean ifListContains(List<ParseUser> userList, ParseUser user) {
        if(userList != null) {
            for (ParseUser u1 : userList) {
                if (u1.getObjectId().equals(user.getObjectId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setImage(AppUser fromUser, FeedHolder viewHolder){
        if(fromUser != null && !TextUtils.isEmpty(fromUser.getImage())) {
            Picasso.with(mContext).load(fromUser.getImage()).transform(new CircleTransform()).into(viewHolder.ivProfile);
        } else {
            Picasso.with(mContext).load(GeneralUtils.getProfileUrl(fromUser.getObjectId())).transform(new CircleTransform()).into(viewHolder.ivProfile);
        }
        viewHolder.ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, UserDetailActivity.class);
            intent.putExtra("USER_ID", fromUser.getObjectId());
            mContext.startActivity(intent);
            ((Activity)mContext).overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        });
    }
}
