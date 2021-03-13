package com.uvpce.firebasedemo.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.uvpce.firebasedemo.R;

public class SplashActivity extends AppCompatActivity {

    private static int splashTime = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                Intent i = new Intent(SplashActivity.this, SignInActivity.class);
                startActivity(i);
                // close this activity
                finish();
            }
        }, splashTime);
    }
}