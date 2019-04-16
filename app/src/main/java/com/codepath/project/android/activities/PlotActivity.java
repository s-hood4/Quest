package com.codepath.project.android.activities;



import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.codepath.project.android.R;
import com.codepath.project.android.data.TestData;
import com.codepath.project.android.helpers.Constants;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PlotActivity  extends AppCompatActivity {

    @BindView(R.id.graph)
    GraphView graph;
    @BindView(R.id.percent_change)
    TextView tvPercentChange;
    @BindView(R.id.current_price)
    TextView tvCurrentPrice;
    @BindView(R.id.lowest_price)
    TextView tvLowestPrice;
    @BindView(R.id.highest_price)
    TextView tvHighestPrice;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);
        ButterKnife.bind(this);

        setupToolbar();

        String productName = getIntent().getStringExtra(Constants.PRODUCT_NAME);
        String productPrice = getIntent().getStringExtra(Constants.PRODUCT_PRICE);
        getSupportActionBar().setTitle(productName);
        tvCurrentPrice.setText(productPrice);
        tvHighestPrice.setText(TestData.getMaxMinPrice(productPrice,"MAX"));
        tvLowestPrice.setText(TestData.getMaxMinPrice(productPrice,"MIN"));

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(TestData.getDataPoint());
        series.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        series.setColor(getResources().getColor(R.color.white));
        series.setThickness(10);
        series.setDataPointsRadius(2);
        graph.addSeries(series);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        graph.getGridLabelRenderer().setHighlightZeroLines(true);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(true);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(getResources().getColor(R.color.action_gray));
        graph.getGridLabelRenderer().setVerticalLabelsVisible(false);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"Jan", "Mar","May","Jul","Oct","Dec"});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
    }

    @Override
    public void onBackPressed() {
        // finish() is called in super: we only override this method to be able to override the transition
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    private void setupToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(5);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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