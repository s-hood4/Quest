package com.codepath.project.android.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by anmallya on 12/10/2016.
 */

public class QuestTextView extends TextView {
    public QuestTextView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/lineto-circular-pro-book.ttf"));
    }
}
