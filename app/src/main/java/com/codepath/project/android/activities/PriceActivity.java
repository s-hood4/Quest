package com.codepath.project.android.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.project.android.R;
import com.codepath.project.android.helpers.Constants;
import com.codepath.project.android.helpers.ThemeUtils;
import com.codepath.project.android.model.Feed;
import com.codepath.project.android.model.Product;
import com.parse.ParseCloud;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PriceActivity extends AppCompatActivity {

    @BindView(R.id.tvProductName)
    TextView tvProductName;
    @BindView(R.id.tvProductPrice)
    TextView tvProductPrice;
    @BindView(R.id.etPrice)
    EditText etPrice;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.etStore)
    EditText etStore;

    private String productPrice;
    private String productId;

    private Product prod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_price);
        ButterKnife.bind(this);
        setupToolbar();
        String productName = getIntent().getStringExtra(Constants.PRODUCT_NAME);
        productPrice = getIntent().getStringExtra(Constants.PRODUCT_PRICE);
        productId = getIntent().getStringExtra(Constants.PRODUCT_ID);
        tvProductName.setText(productName);
        tvProductPrice.setText("$"+productPrice);
    }

    private void setupToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.report_prices);
        getSupportActionBar().setElevation(5);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    private void updatePrice(String newPrice, String store){
        ParseQuery<Product> query = ParseQuery.getQuery(Product.class);
        query.getInBackground(productId, (product, e) -> {
            if (e == null) {
                prod = product;
                product.setPrice(newPrice);
                product.saveInBackground();
                Feed feed = new Feed();
                feed.setType("addPrice");
                feed.setContent(newPrice);
                feed.setFromUser(ParseUser.getCurrentUser());
                feed.setToProduct(product);
                feed.saveInBackground();

                Map<String, String> parameters = new HashMap<>();
                parameters.put("price", "$" + newPrice);
                parameters.put("store", store);
                parameters.put("name", product.getName());
                parameters.put("productId", productId);
                ParseCloud.callFunctionInBackground("pricePush", parameters, (mapObject, e1) -> {
                    if (e1 == null){
                        System.out.println("sent");
                    }
                });

                finish();
            } else {
                Toast.makeText(PriceActivity.this, "parse error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void onSubmitPrice(View view){
        String price = etPrice.getText().toString();
        String store = etStore.getText().toString();
        if(price.length() == 0){
            Toast.makeText(PriceActivity.this, "Price cannot be null", Toast.LENGTH_SHORT).show();
            return;
        }
        if(new Double(price) < new Double(productPrice)){
            updatePrice(price, store);
        }
    }

    @Override
    public void onBackPressed() {
        // finish() is called in super: we only override this method to be able to override the transition
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
