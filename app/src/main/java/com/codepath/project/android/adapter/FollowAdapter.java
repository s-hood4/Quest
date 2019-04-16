package com.codepath.project.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.project.android.R;
import com.codepath.project.android.activities.UserDetailActivity;
import com.codepath.project.android.helpers.CircleTransform;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.ViewHolder> {

    private List<ParseUser> mUsers;
    private Context mContext;

    public FollowAdapter(Context context, List<ParseUser> users) {
        mUsers = users;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public FollowAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View tweetView = inflater.inflate(R.layout.item_follow, parent, false);
        return new ViewHolder(tweetView);
    }

    @Override
    public void onBindViewHolder(FollowAdapter.ViewHolder viewHolder, int position) {
        final ParseUser user = mUsers.get(position);
        viewHolder.ivProfileImg.setImageResource(android.R.color.transparent);
        user.fetchIfNeededInBackground((object, e) -> {
            viewHolder.tvUserName.setText(object.getString("firstName"));
            Picasso.with(getContext()).load(object.getString("pictureUrl")).transform(new CircleTransform()).into(viewHolder.ivProfileImg);
        });

        viewHolder.rlFollowItem.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), UserDetailActivity.class);
            intent.putExtra("USER_ID", user.getObjectId());
            getContext().startActivity(intent);
            ((Activity)mContext).overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvUserName)
        TextView tvUserName;
        @BindView(R.id.ivProfileImg)
        ImageView ivProfileImg;
        @BindView(R.id.rlFollowItem)
        RelativeLayout rlFollowItem;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}