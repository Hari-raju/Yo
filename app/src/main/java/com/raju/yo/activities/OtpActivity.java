package com.raju.yo.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.raju.yo.connectivity.Connection;
import com.raju.yo.utils.AndroidUtils;
import com.raju.yo.databinding.ActivityOtpBinding;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    private String phone, codeSent;
    private ActivityOtpBinding otpActivity;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private Long timeoutSeconds = 60L;
    private Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phone = getIntent().getStringExtra("phone");
        otpActivity = ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(otpActivity.getRoot());
        connection=new Connection();
        if(connection.isConnected(getApplicationContext())){
            otpActivity.buttonOtp.setVisibility(View.VISIBLE);
            otpActivity.progressOtp.setVisibility(View.GONE);
            Listeners();
        }
        else{
            otpActivity.buttonOtp.setVisibility(View.GONE);
            otpActivity.progressOtp.setVisibility(View.VISIBLE);
            AndroidUtils.showToast(getApplicationContext(),"Turn on Internet");
        }
    }

    //Setting up Listeners
    private void Listeners() {
        otpActivity.otpNumberOtp.setText(String.format("Please enter the otp sent to your\nnumber %s", phone));
        //When it enters activity otp will generate otp automatically
        generateOtp(phone,false);
        //Setting up resend listener to resend otp
        otpActivity.resendOtp.setOnClickListener(v -> {
            generateOtp(phone,true);
        });
        //Setting listener for button to verify otp
        otpActivity.buttonOtp.setOnClickListener(v -> {
            String codeByUser = otpActivity.otp.getText().toString();
            if (codeByUser.isEmpty() || codeByUser.length() < 6) {
                otpActivity.otp.setError("Incorrect Otp");
            } else {
                otpActivity.buttonOtp.setVisibility(View.GONE);
                otpActivity.progressOtp.setVisibility(View.VISIBLE);
                verifyCode(codeByUser);
            }
        });
    }

    //Sending Otp
    private void generateOtp(String phone,boolean resend) {
        startResendTimer();
        //Generating otp by setting number,time out,from which activoty it calls,call back => which contains functions like complete,faile...etc
        PhoneAuthOptions.Builder options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phone)
                        .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                AndroidUtils.showToast(getApplicationContext(), "Otp Sent Successfully");
                                codeSent = s;
                                resendToken = forceResendingToken;
                            }

                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                AndroidUtils.showToast(getApplicationContext(), "Otp Sent Successfully");
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                AndroidUtils.showToast(getApplicationContext(), e.getMessage());
                            }
                        });
        if (resend) {
            PhoneAuthProvider.verifyPhoneNumber(options.setForceResendingToken(resendToken).build());
        } else {
            PhoneAuthProvider.verifyPhoneNumber(options.build());
        }
    }


    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        signUp(credential);
    }

    private void signUp(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    AndroidUtils.showToast(getApplicationContext(), "Verified successfully");
                    Intent intent = new Intent(OtpActivity.this, CreateUserActivity.class);
                    intent.putExtra("phone",phone);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    otpActivity.otp.setError("Incorrect Otp");
                }
            }
        });
    }

    private void startResendTimer() {
        otpActivity.resendOtp.setText("Resend otp in 60 seconds");
        otpActivity.resendOtp.setEnabled(false);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                otpActivity.resendOtp.setText("Resend");
                otpActivity.resendOtp.setEnabled(true);
            }
        }, 60000);
    }
}