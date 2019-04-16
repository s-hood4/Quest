package com.codepath.project.android.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.project.android.R;
import com.codepath.project.android.adapter.ContactFriendsAdapter;
import com.codepath.project.android.adapter.ReviewsAdapter;
import com.codepath.project.android.adapter.VideoAdapter;
import com.codepath.project.android.fragments.ComposeFragment;
import com.codepath.project.android.helpers.Constants;
import com.codepath.project.android.helpers.ItemClickSupport;
import com.codepath.project.android.helpers.ThemeUtils;
import com.codepath.project.android.model.AppUser;
import com.codepath.project.android.model.Feed;
import com.codepath.project.android.model.Product;
import com.codepath.project.android.model.Review;
import com.codepath.project.android.model.Video;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static java.lang.Math.min;

public class ProductViewActivity extends AppCompatActivity {

    @BindView(R.id.ivProductImage)
    ImageView ivProductImage;
    @BindView(R.id.tvProductName)
    TextView tvProductName;
    @BindView(R.id.tvBrandName)
    TextView tvBrandName;
    @BindView(R.id.rbAverageRating)
    RatingBar rbAverageRating;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rvReviews)
    RecyclerView rvReviews;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.tvShelf)
    TextView tvShelf;
    @BindView(R.id.tvWatch)
    TextView tvWatch;
    @BindView(R.id.tvFollow)
    TextView tvFollow;
    @BindView(R.id.tvPrice)
    TextView tvPrice;
    @BindView(R.id.tvReviewCount)
    TextView tvReviewCount;
    @BindView(R.id.rvFriends)
    RecyclerView rvFriends;
    @BindView(R.id.tvFriendsTitle)
    TextView tvFriendsTitle;
    @BindView(R.id.rvVideo)
    RecyclerView rvVideo;
    @BindView(R.id.lineFriends)
    LinearLayout lineFriends;

    ReviewsAdapter reviewsAdapter;
    List<Review> reviews;
    Product product;
    AppUser user;

    private static final int REVIEWS_TO_SHOW_COUNT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_product_view);
        ButterKnife.bind(this);
        user = (AppUser) ParseUser.getCurrentUser();
        setUpToolbar();
        if(savedInstanceState == null) {
            setUpRecyclerView();
            String productId = getIntent().getStringExtra("productId");
            ParseQuery<Product> query = ParseQuery.getQuery(Product.class);
            query.getInBackground(productId, (p, e) -> {
                if (e == null) {
                    product = p;
                    setCollapsedToolbar();
                    setProductImage();
                    setVideos();
                    setProductAttributes();
                    fetchFirstNReviews();
                    product.incrementViews();
                    product.saveInBackground();
                    setUpShelfWishClickListener();
                    if(ParseUser.getCurrentUser() != null) {
                        setFriends();
                    }
                } else {
                    Toast.makeText(ProductViewActivity.this, "parse error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void fetchFirstNReviews(){
        ParseQuery<Review> reviewQuery = ParseQuery.getQuery(Review.class);
        reviewQuery.include("user");
        reviewQuery.whereEqualTo("product", product);
        reviewQuery.addDescendingOrder("updatedAt");
        reviewQuery.findInBackground((reviewList, err) -> {
            if (err == null) {
                List<Review> firstNReviews = reviewList.subList(0, min(reviewList.size(), REVIEWS_TO_SHOW_COUNT));
                reviews.addAll(firstNReviews);
                reviewsAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(ProductViewActivity.this, "parse error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setProductAttributes(){
        tvProductName.setText(product.getName());
        tvBrandName.setText(product.getBrand());
        rbAverageRating.setRating((float) product.getAverageRating());
        tvReviewCount.setText(""+product.getRatingCount());
        tvPrice.setText("$"+product.getPrice());
    }

    private void setCollapsedToolbar(){
        collapsingToolbar.setTitle(product.getName());
        collapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);
    }

    public static int darker (int color, float factor) {
        int a = Color.alpha( color );
        int r = Color.red( color );
        int g = Color.green( color );
        int b = Color.blue( color );

        return Color.argb( a,
                Math.max( (int)(r * factor), 0 ),
                Math.max( (int)(g * factor), 0 ),
                Math.max( (int)(b * factor), 0 ) );
    }

    private void setProductImage() {
        Picasso.with(this).load(product.getImageUrl()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                ivProductImage.setImageBitmap(bitmap);
                Palette.from(bitmap).maximumColorCount(25).generate(palette -> {
                    Palette.Swatch vibrant = palette.getVibrantSwatch();
                    if (vibrant != null) {
                        Window window = getWindow();
                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(darker(palette.getMutedColor(vibrant.getRgb()), 0.8f));
                        collapsingToolbar.setContentScrimColor(palette.getMutedColor(vibrant.getRgb()));
                    }
                });
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
        ivProductImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, ImageFullscreenActivity.class);
            String image = product.getImageUrl();
            intent.putExtra("image", image);
            JSONArray imagesArray = product.getImageSetUrls();
            String[] images = new String[imagesArray.length()];
            for(int i = 0; i < imagesArray.length(); i++){
                try {
                    images[i] = imagesArray.getString(i);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
            intent.putExtra("imageSet", images);
            startActivity(intent);
        });
    }

    private void setUpShelfWishClickListener() {

        List<Product> productList;

        if(user != null) {
            productList = user.getShelfProducts();
            if(ifListContains(productList)) {
                tvShelf.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                DrawableCompat.setTint(tvShelf.getCompoundDrawables()[1], ContextCompat.getColor(this, R.color.colorPrimary));
                tvShelf.setText("In shelf");
            }
            productList = user.getWishListProducts();
            if(ifListContains(productList)) {
                tvWatch.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                DrawableCompat.setTint(tvWatch.getCompoundDrawables()[1], ContextCompat.getColor(this, R.color.colorPrimary));
                tvWatch.setText("Watching");
            }
            productList = user.getFollowProducts();
            if(ifListContains(productList)) {
                tvFollow.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                DrawableCompat.setTint(tvFollow.getCompoundDrawables()[1], ContextCompat.getColor(this, R.color.colorPrimary));
                tvFollow.setText("Following");
            }
        }

        tvShelf.setOnClickListener(v -> {
            if(ParseUser.getCurrentUser() != null) {
                List<Product> productShelfList = user.getShelfProducts();
                if(ifListContains(productShelfList)) {
                    user.removeShelfProduct(product);
                    tvShelf.setTextColor(ContextCompat.getColor(this, R.color.colorLightGray3));
                    DrawableCompat.setTint(tvShelf.getCompoundDrawables()[1], ContextCompat.getColor(this, R.color.colorLightGray3));
                    tvShelf.setText("Shelf");
                } else {
                    user.addShelfProduct(product);
                    tvShelf.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                    DrawableCompat.setTint(tvShelf.getCompoundDrawables()[1], ContextCompat.getColor(this, R.color.colorPrimary));
                    tvShelf.setText("In shelf");
                }
                try {
                    user.save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
        });

        tvWatch.setOnClickListener(v -> {
            if(user != null) {
                List<Product> productWishList = user.getWishListProducts();
                if(ifListContains(productWishList)) {
                    user.removeWishListProduct(product);
                    tvWatch.setText("Watch");
                    tvWatch.setTextColor(ContextCompat.getColor(this, R.color.colorLightGray3));
                    DrawableCompat.setTint(tvWatch.getCompoundDrawables()[1], ContextCompat.getColor(this, R.color.colorLightGray3));
                } else {
                    user.addWishListProduct(product);
                    tvWatch.setText("Watching");
                    tvWatch.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                    DrawableCompat.setTint(tvWatch.getCompoundDrawables()[1], ContextCompat.getColor(this, R.color.colorPrimary));
                }
                try {
                    user.save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
        });

        tvFollow.setOnClickListener(v -> {
            if(user != null) {
                List<Product> followProducts = user.getFollowProducts();
                if(ifListContains(followProducts)) {
                    user.removeFollowProduct(product);
                    tvFollow.setText("Follow");
                    tvFollow.setTextColor(ContextCompat.getColor(this, R.color.colorLightGray3));
                    DrawableCompat.setTint(tvFollow.getCompoundDrawables()[1], ContextCompat.getColor(this, R.color.colorLightGray3));
                } else {
                    Feed feed = new Feed();
                    feed.setType("followProduct");
                    feed.setFromUser(ParseUser.getCurrentUser());
                    feed.setToProduct(product);
                    feed.saveInBackground();
                    user.setFollowProducts(product);
                    tvFollow.setText("Following");
                    tvFollow.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                    DrawableCompat.setTint(tvFollow.getCompoundDrawables()[1], ContextCompat.getColor(this, R.color.colorPrimary));
                }
                try {
                    user.save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean ifListContains(List<Product> productList) {
        if(productList != null) {
            for (Product prod : productList) {
                if (prod.getObjectId().equals(product.getObjectId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setUpRecyclerView() {
        reviews = new ArrayList<>();
        reviewsAdapter = new ReviewsAdapter(this, reviews);
        rvReviews.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        rvReviews.setAdapter(reviewsAdapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mLayoutManager.scrollToPosition(0);
        rvReviews.setLayoutManager(mLayoutManager);
        rvReviews.setNestedScrollingEnabled(false);

        ItemClickSupport.addTo(rvReviews).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    Intent intent = new Intent(ProductViewActivity.this, DetailedReviewActivity.class);
                    intent.putExtra("reviewId", reviews.get(position).getObjectId());
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                }
        );
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    public void onShowPlot(View view){
        Intent i = new Intent(this, PlotActivity.class);
        i.putExtra(Constants.PRODUCT_NAME, product.getName());
        i.putExtra(Constants.PRODUCT_PRICE, product.getPrice());
        startActivity(i);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    public void addPrice(View view){
        Intent i = new Intent(this, PriceActivity.class);
        i.putExtra(Constants.PRODUCT_NAME, product.getName());
        i.putExtra(Constants.PRODUCT_PRICE, product.getPrice());
        i.putExtra(Constants.PRODUCT_ID, product.getObjectId());
        startActivity(i);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onAddReview(View view) {
        ParseUser user = ParseUser.getCurrentUser();
        if(user != null) {
            Bundle bundle = new Bundle();
            bundle.putString("productId", product.getObjectId());
            FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
            ComposeFragment composeFragment = ComposeFragment.newInstance("Add review");
            composeFragment.setArguments(bundle);
            composeFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
            composeFragment.show(fm, "fragment_compose");
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void onNewReviewAdded(){
        reviews.clear();
        fetchFirstNReviews();
    }

    private void setVideos(){
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        VideoAdapter videoAdapter = new VideoAdapter(this, Video.geVideoArray(product.getVideoSetUrls()));
        rvVideo.setAdapter(videoAdapter);
        rvVideo.setLayoutManager(layoutManager);
    }

    private void setFriends(){
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        ArrayList<AppUser> friends = new ArrayList<>();
        ContactFriendsAdapter friendsAdapter = new ContactFriendsAdapter(this, friends);
        rvFriends.setAdapter(friendsAdapter);
        rvFriends.setLayoutManager(layoutManager);
        AppUser u1 = (AppUser) ParseUser.getCurrentUser();
        List<ParseUser> contactFriends = u1.getFollowUsers();

        ArrayList<String> idList = new ArrayList<>();
        if(contactFriends != null && contactFriends.size() > 0) {
            for (ParseUser tempuser : contactFriends) {
                idList.add(tempuser.getObjectId());
            }
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereContainedIn("objectId", idList);
            query.whereEqualTo("shelfProducts", product);
            query.findInBackground((objects, e1) -> {
                if (objects != null && objects.size() > 0) {
                    ArrayList<AppUser> contacts = new ArrayList<>();
                    for (ParseUser puser : objects) {
                        contacts.add((AppUser) puser);
                    }
                    friends.addAll(contacts);
                    friendsAdapter.notifyDataSetChanged();
                    lineFriends.setVisibility(View.VISIBLE);
                    rvFriends.setVisibility(View.VISIBLE);
                    tvFriendsTitle.setVisibility(View.VISIBLE);

                }
            });
        }
    }

    public void onShowAllReviews(View view){
        Intent i = new Intent(this, ReviewsActivity.class);
        i.putExtra(Constants.PRODUCT_ID, getIntent().getStringExtra("productId"));
        startActivity(i);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }


}
