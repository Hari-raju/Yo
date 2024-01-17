package com.raju.yo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.raju.yo.connectivity.Connection;
import com.raju.yo.utils.AndroidUtils;
import com.raju.yo.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding signUpBinding;
    private Connection connection;
    private String whatToDo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signUpBinding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(signUpBinding.getRoot());
        connection=new Connection();
        whatToDo=getIntent().getStringExtra("whatToDo");
        if(connection.isConnected(getApplicationContext())){
            signUpBinding.buttonSignUp.setVisibility(View.VISIBLE);
            signUpBinding.progressSignup.setVisibility(View.GONE);
            Listeners();
        }
        else{
            signUpBinding.buttonSignUp.setVisibility(View.GONE);
            signUpBinding.progressSignup.setVisibility(View.VISIBLE);
            AndroidUtils.showToast(getApplicationContext(),"Turn on Internet");
        }
    }

    private void Listeners() {
        signUpBinding.countryCodePickerSignUp.registerCarrierNumberEditText(signUpBinding.phoneSignUp);
        signUpBinding.backSignUp.setOnClickListener(v -> onBackPressed());
        signUpBinding.buttonSignUp.setOnClickListener(v -> {
            if (!signUpBinding.countryCodePickerSignUp.isValidFullNumber()) {
                signUpBinding.phoneSignUp.setError("Enter valid phone number!");
                return;
            }
            try {
                Intent intent = new Intent(SignUpActivity.this, OtpActivity.class);
                intent.putExtra("phone",signUpBinding.countryCodePickerSignUp.getFullNumberWithPlus());
                intent.putExtra("whatToDo",whatToDo);
                startActivity(intent);
                if(whatToDo.equals("reset"))
                    finish();
            } catch (Exception e) {
                AndroidUtils.showToast(getApplicationContext(), e.getMessage());
            }
        });
    }
}
