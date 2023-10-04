package com.raju.yo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.raju.yo.R;
import com.raju.yo.connectivity.Constants;
import com.raju.yo.connectivity.PreferenceManager;

public class SplashActivity extends AppCompatActivity {
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preferenceManager=new PreferenceManager(getApplicationContext());
        new Handler().postDelayed(() -> {
            if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
            else{
                startActivity(new Intent(SplashActivity.this, SignInActivity.class));
            }
            finish();
        },1000);
    }
}