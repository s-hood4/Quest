package com.codepath.project.android.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.codepath.project.android.R;
import com.codepath.project.android.fragments.UserDetailFragment;
import com.codepath.project.android.helpers.ThemeUtils;

public class UserDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_user_detail);
        String userId = getIntent().getStringExtra("USER_ID");
        Bundle bundle = new Bundle();
        bundle.putString("USER_ID", userId);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = new UserDetailFragment();
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.fragmentUserDetail, fragment).commit();
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

