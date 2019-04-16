package com.codepath.project.android.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.project.android.R;
import com.squareup.picasso.Picasso;

public class SearchResultsAdapter extends SimpleCursorAdapter {
    private static final String tag = SearchResultsAdapter.class.getName();
    private Context context = null;
    public SearchResultsAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.context=context;
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        TextView textView=(TextView)view.findViewById(R.id.name);
        ImageView ivProduct = (ImageView) view.findViewById(R.id.ivProduct);
        textView.setText(cursor.getString(2));
        Picasso.with(context).load(cursor.getString(3)).into(ivProduct);
    }
}
