package com.codepath.project.android.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.project.android.R;
import com.codepath.project.android.activities.FollowActivity;
import com.codepath.project.android.activities.ProductViewActivity;
import com.codepath.project.android.activities.UserDetailActivity;
import com.codepath.project.android.adapter.ComplexRecyclerViewAdapter;
import com.codepath.project.android.helpers.CircleTransform;
import com.codepath.project.android.helpers.ItemClickSupport;
import com.codepath.project.android.model.AppUser;
import com.codepath.project.android.model.Feed;
import com.codepath.project.android.model.Recommend;
import com.codepath.project.android.utils.GeneralUtils;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class UserDetailFragment extends Fragment {

    @BindView(R.id.ivBackgroundImage) ImageView ivBackgroundImage;
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.tvUserFirstName) TextView tvUserFirstName;
    @BindView(R.id.tvFollowing) TextView tvFollowing;
    @BindView(R.id.tvFollowingLabel) TextView tvFollowingLabel;
    @BindView(R.id.rvUserTimeline) RecyclerView rvFeeds;
    @BindView(R.id.followUser) TextView followUser;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    ArrayList<Feed> feeds = new ArrayList<>();
    ComplexRecyclerViewAdapter feedsAdapter;

    String userId;

    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        userId = getArguments().getString("USER_ID");

        setupToolbar();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(userId, (object, e) -> {
            if (e == null) {
                if (ParseUser.getCurrentUser() == null || ParseUser.getCurrentUser().getObjectId().equals(object.getObjectId())) {
                    followUser.setVisibility(View.GONE);
                } else {
                    if(ParseUser.getCurrentUser() != null) {
                        AppUser currentUser = (AppUser) ParseUser.getCurrentUser();
                        List<ParseUser> followUsers = currentUser.getFollowUsers();
                        if (ifListContains(followUsers, object)) {
                            followUser.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                            DrawableCompat.setTint(followUser.getCompoundDrawables()[1], ContextCompat.getColor(getContext(), R.color.colorPrimary));
                            followUser.setText("Following");
                        }
                        followUser.setOnClickListener(v -> {
                            if (ifListContains(currentUser.getFollowUsers(), object)) {
                                currentUser.removeFollowUser(object);
                                followUser.setText("Follow");
                                followUser.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray));
                                DrawableCompat.setTint(followUser.getCompoundDrawables()[1], ContextCompat.getColor(getContext(), R.color.colorGray));
                            } else {
                                Feed feed = new Feed();
                                feed.setType("followUser");
                                feed.setFromUser(ParseUser.getCurrentUser());
                                feed.setToUser(object);
                                feed.saveInBackground();
                                AppUser otherUser = (AppUser) object;
                                ParseQuery<Recommend> recQuery = ParseQuery.getQuery(Recommend.class);
                                recQuery.whereEqualTo("user", otherUser);
                                recQuery.getFirstInBackground((object1, e1) -> {
                                    if(object1 == null) {
                                        Recommend r = new Recommend();
                                        r.setUser(otherUser);
                                        r.setFollowingUsers(currentUser);
                                        r.saveInBackground();
                                    } else {
                                        object1.setFollowingUsers(currentUser);
                                        object1.saveInBackground();
                                    }
                                });
                                currentUser.setFollowUsers(object);
                                followUser.setText("Following");
                                followUser.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                                DrawableCompat.setTint(followUser.getCompoundDrawables()[1], ContextCompat.getColor(getContext(), R.color.colorPrimary));
                            }
                            currentUser.saveInBackground();
                        });
                    }
                }

                if(!TextUtils.isEmpty(object.getString("pictureUrl"))) {
                    Picasso.with(getContext())
                            .load(object.getString("pictureUrl")).transform(new CircleTransform())
                            .into(ivProfileImage);
                } else {
                    Picasso.with(getContext())
                            .load(GeneralUtils.getProfileUrl(object.getObjectId())).transform(new CircleTransform())
                            .into(ivProfileImage);
                }

                if(!TextUtils.isEmpty(object.getString("coverUrl"))) {
                    Picasso.with(getContext())
                            .load(object.getString("coverUrl")).transform(new CircleTransform())
                            .into(ivBackgroundImage);
                }
                String upperString = object.getString("firstName").substring(0,1).toUpperCase() + object.getString("firstName").substring(1);
                tvUserFirstName.setText(upperString);
                AppUser currentUser = (AppUser) object;
                if(currentUser.getFollowUsers() != null && currentUser.getFollowUsers().size() > 0) {
                    tvFollowing.setText(currentUser.getFollowUsers().size() + "");
                    tvFollowing.setOnClickListener(v -> {
                        Intent intent = new Intent(getActivity(), FollowActivity.class);
                        intent.putExtra("USER_ID", currentUser.getObjectId());
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                    });

                    tvFollowingLabel.setOnClickListener(v -> {
                        Intent intent = new Intent(getActivity(), FollowActivity.class);
                        intent.putExtra("USER_ID", currentUser.getObjectId());
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                    });
                } else {
                    tvFollowing.setText("0");
                }

                collapsingToolbar.setTitle(upperString);
                collapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);
                setUpRecyclerView(object);
            }
        });

        ItemClickSupport.addTo(rvFeeds).setOnItemClickListener(
                (rview, position, v) -> {
                    Intent intent;
                    if(position > 0) {
                        position--;
                    }
                    if(feeds.get(position).getType().equals("followUser")) {
                        intent = new Intent(getActivity(), UserDetailActivity.class);
                        intent.putExtra("USER_ID", feeds.get(position).getToUser().getObjectId());
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                    } else {
                        intent = new Intent(getActivity(), ProductViewActivity.class);
                        intent.putExtra("productId", feeds.get(position).getToProduct().getObjectId());
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(getActivity(), v.findViewById(R.id.ivProductImage), "ivProductImage");
                        startActivity(intent, options.toBundle());
                    }
                }
        );

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setUpRecyclerView(ParseUser parseUser) {
        feeds = new ArrayList<>();
        feedsAdapter = new ComplexRecyclerViewAdapter(getContext(), feeds, null);
        //rvFeeds.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext()).sizeResId(R.dimen.feed_divider).build());
        rvFeeds.setAdapter(feedsAdapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mLayoutManager.scrollToPosition(0);
        rvFeeds.setLayoutManager(mLayoutManager);
        rvFeeds.setNestedScrollingEnabled(false);

        ParseQuery<Feed> query = ParseQuery.getQuery(Feed.class);
        query.addDescendingOrder("updatedAt");
        query.whereEqualTo("fromUser", parseUser);
        query.include("fromUser");
        query.include("toUser");
        query.include("toProduct");
        query.include("review");
        query.setLimit(10);
        query.findInBackground((feedsList, err) -> {
            if (err == null) {
                feeds.addAll(feedsList);
                feedsAdapter.notifyDataSetChanged();
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

    private void setupToolbar(){
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setElevation(5);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

}
