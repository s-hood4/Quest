package com.codepath.project.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.project.android.R;
import com.codepath.project.android.activities.LoginActivity;
import com.codepath.project.android.activities.ProductViewActivity;
import com.codepath.project.android.activities.UserDetailActivity;
import com.codepath.project.android.adapter.ComplexRecyclerViewAdapter;
import com.codepath.project.android.helpers.EndlessRecyclerViewScrollListener;
import com.codepath.project.android.helpers.ItemClickSupport;
import com.codepath.project.android.model.AppUser;
import com.codepath.project.android.model.Feed;
import com.codepath.project.android.model.Recommend;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FeedFragment extends Fragment {

    @BindView(R.id.rvFeeds)
    RecyclerView rvFeeds;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    ArrayList<Feed> feeds = new ArrayList<>();
    ComplexRecyclerViewAdapter feedsAdapter;

    ArrayList<AppUser> friendsToFollow = new ArrayList<>();

    private Unbinder unbinder;

    public static FeedFragment newInstance() {
        return new FeedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        unbinder = ButterKnife.bind(this, view);
        if(ParseUser.getCurrentUser() != null) {
            setUpRecyclerView();
        } else {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setUpRecyclerView() {
        feeds = new ArrayList<>();
        feedsAdapter = new ComplexRecyclerViewAdapter(getContext(), feeds, friendsToFollow);
        rvFeeds.setAdapter(feedsAdapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mLayoutManager.scrollToPosition(0);
        rvFeeds.setNestedScrollingEnabled(false);
        rvFeeds.setLayoutManager(mLayoutManager);

        swipeContainer.setOnRefreshListener(() -> fetchFeeds(0));
        swipeContainer.setColorSchemeResources(android.R.color.holo_green_light);

        ParseUser currentUser = ParseUser.getCurrentUser();
        try {
            currentUser.fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        fetchFeeds(0);

        rvFeeds.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                page++;
                fetchFeeds(page*10);
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

        setUpFriends();
    }

    public void setUpFriends() {
        ParseQuery<Recommend> recQuery = ParseQuery.getQuery(Recommend.class);
        recQuery.whereEqualTo("user", ParseUser.getCurrentUser());
        recQuery.getFirstInBackground((object1, e1) -> {
            if(e1 == null) {
                if (object1 != null) {
                    if(object1.getFollowingUsers() != null && object1.getFollowingUsers().size() > 0) {
                        for (ParseUser puser : object1.getFollowingUsers()) {
                            friendsToFollow.add((AppUser) puser);
                        }
                        feedsAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void fetchFeeds(int skip) {
        AppUser user = (AppUser) ParseUser.getCurrentUser();

        List<ParseQuery<Feed>> queries = new ArrayList<>();

        if(user.getFollowUsers() != null) {
            ParseQuery<Feed> followsUserQuery = ParseQuery.getQuery(Feed.class);
            followsUserQuery.whereContainedIn("fromUser", user.getFollowUsers());
            queries.add(followsUserQuery);
        }

        if(user.getFollowProducts() != null) {
            ParseQuery<Feed> followsProductQuery = ParseQuery.getQuery(Feed.class);
            followsProductQuery.whereContainedIn("toProduct", user.getFollowProducts());
            followsProductQuery.whereNotContainedIn("fromUser", Collections.singletonList(ParseUser.getCurrentUser()));
            queries.add(followsProductQuery);
        }

        if(queries.size() > 0) {
            ParseQuery<Feed> mainQuery = ParseQuery.or(queries);
            mainQuery.include("fromUser");
            mainQuery.include("toUser");
            mainQuery.include("toProduct");
            mainQuery.include("review");
            mainQuery.setLimit(10);
            mainQuery.addDescendingOrder("createdAt");
            if(skip > 0) {
                mainQuery.setSkip(skip);
            }

            mainQuery.findInBackground((feedsList, err) -> {
                if (err == null) {
                    feeds.addAll(feedsList);
                    feedsAdapter.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                }
            });
        }
    }
}