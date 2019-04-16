package com.codepath.project.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.project.android.R;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewLoginActivity extends AppCompatActivity {

    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.btnSubmit)
    TextView btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_login);
        ButterKnife.bind(this);

        btnSubmit.setOnClickListener(v -> {
            ParseUser.logInInBackground(etEmail.getText().toString().trim(),
                    etPassword.getText().toString().trim(),
                    (user, e) -> {
                        if (user != null) {
                            startNextActivity();
                        } else {
                            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT);
                        }
                    });
        });
    }

    private void startNextActivity() {
        Intent intent = new Intent(this, LoadingActivity.class);
        startActivity(intent);
        finish();
    }
}
