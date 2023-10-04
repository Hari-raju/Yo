package com.raju.yo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.raju.yo.connectivity.Connection;
import com.raju.yo.databinding.ActivitySignInBinding;
import com.raju.yo.utils.AndroidUtils;

public class SignInActivity extends AppCompatActivity {
    ActivitySignInBinding signInBinding;
    Connection connection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signInBinding=ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(signInBinding.getRoot());
        connection= new Connection();
        if(connection.isConnected(getApplicationContext())){
            signInBinding.buttonSignIn.setVisibility(View.VISIBLE);
            signInBinding.progressSignin.setVisibility(View.GONE);
            Listeners();
        }
        else{
            signInBinding.buttonSignIn.setVisibility(View.GONE);
            signInBinding.progressSignin.setVisibility(View.VISIBLE);
            AndroidUtils.showToast(getApplicationContext(),"Turn on Internet");
        }
    }
    private void Listeners(){
        signInBinding.textCreateNewAccountSignIn.setOnClickListener(v-> startActivity(new Intent(SignInActivity.this,SignUpActivity.class)));
    }
}