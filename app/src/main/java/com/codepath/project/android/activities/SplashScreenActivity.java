package com.codepath.project.android.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.codepath.project.android.R;
import com.codepath.project.android.network.ParseHelper;
import com.parse.ParseUser;

public class SplashScreenActivity extends AppCompatActivity {

    int SPLASH_TIME = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(() -> {
            if (ParseUser.getCurrentUser() == null) {
                startLoginActivity();
            } else {
                startHomeActivity();
            }
        }, SPLASH_TIME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (LoginActivity.REQUEST_CODE_LOGIN) :
                if (resultCode == LoginActivity.RESULT_OK) {
                    startHomeActivity();
                }
                break;
        }
    }

    public void startLoginActivity() {
        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
        startActivityForResult(intent, LoginActivity.REQUEST_CODE_LOGIN);
    }

    public void startHomeActivity() {
        //Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
        if(ParseHelper.getProductList() != null){
            Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
            startActivity(intent);
        } else{
            Intent intent = new Intent(SplashScreenActivity.this, LoadingActivity.class);
            startActivity(intent);
        }
    }
}
