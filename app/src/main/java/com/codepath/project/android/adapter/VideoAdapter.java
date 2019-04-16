package com.codepath.project.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.project.android.R;
import com.codepath.project.android.activities.YoutubeActivity;
import com.codepath.project.android.helpers.Constants;
import com.codepath.project.android.model.Product;
import com.codepath.project.android.model.Video;
import com.codepath.project.android.utils.GeneralUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created by anmallya on 12/4/2016.
 */
public class VideoAdapter extends
            RecyclerView.Adapter<VideoAdapter.ViewHolder> {

        private List<Video> mVideo;
        private Context mContext;

        public VideoAdapter(Context context, List<Video> video) {
            mVideo = video;
            mContext = context;
        }

        private Context getContext() {
            return mContext;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView ivThumbnail;

            public ViewHolder(View itemView) {
                super(itemView);
                ivThumbnail = (ImageView) itemView.findViewById(R.id.iv_video_thumbnail);
            }
        }

        @Override
        public VideoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View contactView = inflater.inflate(R.layout.item_video_thumbnail, parent, false);
            return new VideoAdapter.ViewHolder(contactView);
        }

        @Override
        public void onBindViewHolder(VideoAdapter.ViewHolder viewHolder, int position) {
            Object videoObject = mVideo.get(position);
            Video video = (Video) videoObject;
            ImageView ivVideoThumbnail = viewHolder.ivThumbnail;
            //Picasso.with(getContext()).load(video.getThumbnail()).transform(new RoundedCornersTransformation(25, 25)).into(ivVideoThumbnail);
            Picasso.with(getContext()).load(video.getThumbnail()).transform(new RoundedCornersTransformation(5, 5)).into(ivVideoThumbnail);
            ivVideoThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(), YoutubeActivity.class);
                    i.putExtra(Constants.YOUTUBE_VIDEO_KEY, GeneralUtils.getYoutubeKey(video.getUrl()));
                    getContext().startActivity(i);
                }
            });
        }

        // Returns the total count of items in the list
        @Override
        public int getItemCount() {
            return mVideo.size();
        }
    }