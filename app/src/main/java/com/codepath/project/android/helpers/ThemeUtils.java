package com.codepath.project.android.helpers;

import android.app.Activity;
import android.content.Intent;

import com.codepath.project.android.R;

public class ThemeUtils {
    private static int sTheme;

    public final static int THEME_MATERIAL_LIGHT = 0;
    public final static int NIGHT_MODE = 1;

    public static void changeToTheme(Activity activity, int theme) {
        sTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
        activity.overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
    }

    public static void onActivityCreateSetTheme(Activity activity) {
        switch (sTheme) {
            case THEME_MATERIAL_LIGHT:
                activity.setTheme(R.style.Theme_Material_Light);
                break;
            case NIGHT_MODE:
                activity.setTheme(R.style.Theme_Night_Mode);
                break;
            default: activity.setTheme(R.style.Theme_Material_Light);
        }
    }
}
