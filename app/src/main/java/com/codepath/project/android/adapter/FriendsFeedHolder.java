package com.codepath.project.android.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.codepath.project.android.R;

public class FriendsFeedHolder extends RecyclerView.ViewHolder {

    public RecyclerView rvFriends;
    public TextView tvTitle;
    public View primerdivisor;

    public FriendsFeedHolder(View v) {
        super(v);
        rvFriends = (RecyclerView) v.findViewById(R.id.rvFriends);
        tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        primerdivisor = (View) v.findViewById(R.id.primerdivisor);
    }
}