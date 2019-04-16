package com.codepath.project.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.codepath.project.android.R;
import com.parse.ParseFacebookUtils;

public class LoginActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_LOGIN = 100;
    public static final int RESULT_CODE_LOGIN_SUCCESS = 0;
    public static final int RESULT_CODE_LOGIN_FAILED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    /*@Override
    protected void onPause() {
        super.onPause();
        finish();
    }*/
}
