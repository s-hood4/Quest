package com.codepath.project.android.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.project.android.R;
import com.codepath.project.android.helpers.CircleTransform;
import com.codepath.project.android.helpers.ThemeUtils;
import com.codepath.project.android.model.AppUser;
import com.codepath.project.android.model.Feed;
import com.codepath.project.android.model.Review;
import com.codepath.project.android.utils.GeneralUtils;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class DetailedReviewActivity extends AppCompatActivity implements OnLikeListener {

    @BindView(R.id.ivProfile)
    ImageView ivProfile;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.tvReview)
    TextView tvReview;
    @BindView(R.id.rvReviewImage)
    RecyclerView rvReviewImage;
    @BindView(R.id.rating)
    RatingBar rating;
    @BindView(R.id.button)
    LikeButton likeButton;
    @BindView(R.id.tvLikeCount)
    TextView tvLikeCount;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    ImageAdapter imageAdapter;
    List<String> imageUrl;

    Review review;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_detailed_review);
        ButterKnife.bind(this);
        setToolbar();
        likeButton.setOnLikeListener(this);

        imageUrl = new ArrayList<>();
        imageAdapter = new ImageAdapter(this, imageUrl);
        rvReviewImage.setLayoutManager(new GridLayoutManager(this, 2));
        rvReviewImage.setAdapter(imageAdapter);
        String reviewId = getIntent().getStringExtra("reviewId");
        ParseQuery<Review> query = ParseQuery.getQuery(Review.class);
        query.include("user");
        query.include("product");
        query.getInBackground(reviewId, (r, e) -> {
            if (e == null) {
                review = r;
                TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
                mTitle.setText(review.getProduct().getName());
                if(ParseUser.getCurrentUser() != null) {
                    List<ParseUser> likedUsers = r.getLikedUsers();
                    if(likedUsers != null && ifListContains(likedUsers, ParseUser.getCurrentUser())) {
                        likeButton.setLiked(true);
                    } else {
                        likeButton.setLiked(false);
                    }
                } else {
                    likeButton.setLiked(false);
                }
                if(r.getLikesCount() > 0) {
                    tvLikeCount.setText(Integer.toString(r.getLikesCount()) + " people like this");
                } else {
                    tvLikeCount.setText("");
                }
                AppUser user = (AppUser) r.getUser();
                setListenerOnProfilePic(user.getObjectId());

                tvReview.setText(r.getText());
                String upperString = user.getString("firstName").substring(0,1).toUpperCase() + user.getString("firstName").substring(1);
                tvUserName.setText(upperString);
                rating.setRating(r.getRating());

                if(!TextUtils.isEmpty(user.getImage())) {
                    Picasso.with(this).load(user.getImage()).transform(new CircleTransform()).into(ivProfile);
                } else {
                    Picasso.with(this).load(GeneralUtils.getProfileUrl(user.getObjectId())).transform(new CircleTransform()).into(ivProfile);
                }
                if(r.getImages() != null && r.getImages().size() > 0) {
                    for(ParseFile pf: r.getImages()) {
                        imageUrl.add(pf.getUrl());
                    }
                }
                imageAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(DetailedReviewActivity.this, "parse error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setListenerOnProfilePic(String userId) {
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailedReviewActivity.this, UserDetailActivity.class);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        });
    }

    private void setToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setElevation(5);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public void liked(LikeButton likeButton) {
        if(ParseUser.getCurrentUser() == null) {
            Intent intent = new Intent(this, SplashScreenActivity.class);
            startActivity(intent);
        } else {
            int likes = review.getLikesCount();
            likes++;
            review.increment("likes");
            review.setLikedUsers(ParseUser.getCurrentUser());
            tvLikeCount.setText(Integer.toString(likes)+ " people like this");
            try {
                review.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Feed feed = new Feed();
            feed.setReview(review);
            feed.setContent(review.getText());
            feed.setRating(Math.round(review.getRating()));
            feed.setToProduct(review.getProduct());
            feed.setFromUser(ParseUser.getCurrentUser());
            feed.setType("likeReview");
            feed.saveInBackground();
        }
    }

    @Override
    public void unLiked(LikeButton likeButton) {
        if(ParseUser.getCurrentUser() == null) {
            Intent intent = new Intent(this, SplashScreenActivity.class);
            startActivity(intent);
        } else {
            int likes = review.getLikesCount();
            likes--;
            review.removeLikedUsers(ParseUser.getCurrentUser());
            if(likes > 0) {
                tvLikeCount.setText(Integer.toString(likes)+ " people like this");
            } else {
                tvLikeCount.setText("");
            }
            review.increment("likes", -1);
            review.saveInBackground();
        }
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

    public class ImageAdapter  extends
            RecyclerView.Adapter<ImageAdapter.ViewHolder> {

        private List<String> mImages;
        private Context mContext;

        public ImageAdapter(Context context, List<String> images) {
            mContext = context;
            mImages = images;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.ivReviewImage)
            ImageView ivReviewImage;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        @Override
        public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View contactView = inflater.inflate(R.layout.item_review_image, parent, false);
            return new ViewHolder(contactView);
        }

        @Override
        public void onBindViewHolder(ImageAdapter.ViewHolder viewHolder, int position) {
            String image = mImages.get(position);
            Picasso.with(mContext).load(image).placeholder(R.drawable.placeholder).transform(new RoundedCornersTransformation(10, 10)).into(viewHolder.ivReviewImage);

            viewHolder.ivReviewImage.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, ImageFullscreenActivity.class);
                String[] images = mImages.toArray(new String[mImages.size()]);
                intent.putExtra("imageSet", images);
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return mImages.size();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }
}
