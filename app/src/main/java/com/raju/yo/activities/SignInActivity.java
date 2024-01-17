package com.raju.yo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.raju.yo.connectivity.Connection;
import com.raju.yo.connectivity.Constants;
import com.raju.yo.connectivity.PreferenceManager;
import com.raju.yo.databinding.ActivitySignInBinding;
import com.raju.yo.utils.AndroidUtils;

public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding signInBinding;
    private Connection connection;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signInBinding=ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(signInBinding.getRoot());
        preferenceManager=new PreferenceManager(getApplicationContext());
        connection= new Connection();
        if(connection.isConnected(getApplicationContext())){
            signInBinding.buttonSignIn.setVisibility(View.VISIBLE);
            signInBinding.progressSignin.setVisibility(View.GONE);
            if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
            Listeners();
        }
        else{
            signInBinding.buttonSignIn.setVisibility(View.GONE);
            signInBinding.progressSignin.setVisibility(View.VISIBLE);
            AndroidUtils.showToast(getApplicationContext(),"Turn on Internet");
        }
    }
    private void Listeners(){
        signInBinding.textCreateNewAccountSignIn.setOnClickListener(v-> {
            Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
            intent.putExtra("whatToDo","create");
            startActivity(intent);
        });
        signInBinding.buttonSignIn.setOnClickListener(v->{
            signInBinding.buttonSignIn.setVisibility(View.GONE);
            signInBinding.progressSignin.setVisibility(View.VISIBLE);
            signInBinding.countryCodePicker.registerCarrierNumberEditText(signInBinding.userPhoneSignin.getEditText());

            if(isDetailsValid()){
                logIn();
            }
            else{
                signInBinding.buttonSignIn.setVisibility(View.VISIBLE);
                signInBinding.progressSignin.setVisibility(View.GONE);
                AndroidUtils.showToast(getApplicationContext(),"Incorrect Credentials");
            }
        });
    }

    //Checking whether the details are valid or not
    private boolean isDetailsValid(){
        return isPhoneValid() && isPassValid();
    }

    //Checking user name and password
    private boolean isPhoneValid(){
        if(signInBinding.countryCodePicker.isValidFullNumber()){
            signInBinding.userPhoneSignin.setError(null);
            return true;
        }
        else{
            signInBinding.userPhoneSignin.setError("Invalid Number");
            return false;
        }
    }

    private boolean isPassValid(){
        String pass = signInBinding.passwordSignin.getEditText().getText().toString();
        String pattern = "^" + "(?=.*[a-zA-Z])" + "(?=\\S+$)" + ".{6,}" + "$";
        if(pass.isEmpty()||!pass.matches(pattern)){
            signInBinding.passwordSignin.setError("Invalid password");
            return false;
        }
        else{
            signInBinding.passwordSignin.setError(null);
            return true;
        }
    }

    private void logIn(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_PHONE,signInBinding.countryCodePicker.getFullNumberWithPlus())
                .whereEqualTo(Constants.KEY_PASSWORD,signInBinding.passwordSignin.getEditText().getText().toString()).get()
                .addOnCompleteListener(task ->{
                    if(task.isSuccessful() && task.getResult()!=null && task.getResult().getDocuments().size()>0){
                        DocumentSnapshot documents = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                        preferenceManager.putString(Constants.KEY_PHONE,documents.getString(Constants.KEY_PHONE));
                        preferenceManager.putString(Constants.KEY_IMAGE,documents.getString(Constants.KEY_IMAGE));
                        preferenceManager.putString(Constants.KEY_USER_ID,documents.getId());
                        preferenceManager.putString(Constants.KEY_USER_NAME,documents.getString(Constants.KEY_USER_NAME));
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else{
                        AndroidUtils.showToast(getApplicationContext(),"Account not found");
                        signInBinding.buttonSignIn.setVisibility(View.VISIBLE);
                        signInBinding.progressSignin.setVisibility(View.GONE);
                    }
                });
    }
}