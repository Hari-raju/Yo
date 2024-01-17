package com.raju.yo.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.firestore.FirebaseFirestore;
import com.raju.yo.R;
import com.raju.yo.connectivity.Connection;
import com.raju.yo.connectivity.Constants;
import com.raju.yo.connectivity.PreferenceManager;
import com.raju.yo.databinding.ActivityCreateUserBinding;
import com.raju.yo.utils.AndroidUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class CreateUserActivity extends AppCompatActivity {

    private ActivityCreateUserBinding createUser;
    //Connectivity checking
    private Connection connection;
    //Storing the encrypted user image
    private String encodeImage;
    //Preference manager is used to store the cache like logged in users data so we can keep them logged in until they log out intentionally
    private PreferenceManager preferenceManager;
    private String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createUser=ActivityCreateUserBinding.inflate(getLayoutInflater());
        setContentView(createUser.getRoot());
        phone=getIntent().getStringExtra("phone");
        preferenceManager=new PreferenceManager(getApplicationContext());
        connection=new Connection();
        if(connection.isConnected(getApplicationContext())){
            createUser.buttonCreateUser.setVisibility(View.VISIBLE);
            createUser.progressCreateUser.setVisibility(View.GONE);
            Listeners();
        }
        else{
            createUser.buttonCreateUser.setVisibility(View.GONE);
            createUser.progressCreateUser.setVisibility(View.VISIBLE);
            AndroidUtils.showToast(getApplicationContext(),"Turn on Internet");
        }
    }

    private void Listeners(){
        createUser.backCreateUser.setOnClickListener(v->onBackPressed());
        createUser.userProfile.setOnClickListener(v->{
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512).createIntent(new Function1<Intent, Unit>() {
                @Override
                public Unit invoke(Intent intent) {
                    pickImage.launch(intent);
                    return null;
                }
            });
        });
        createUser.buttonCreateUser.setOnClickListener(v->{
            createUser.buttonCreateUser.setVisibility(View.GONE);
            createUser.progressCreateUser.setVisibility(View.VISIBLE);
            if(isDetailsValid()){
                if(encodeImage==null){
                    Uri image_uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.default_dp);
                    try{
                        //contentResolver interacts with content provider(user) and open the input stream of file image url
                        InputStream input = getContentResolver().openInputStream(image_uri);
                        //converting into bitmap
                        Bitmap bitmap = BitmapFactory.decodeStream(input);
                        //Setting the image on layout
                        createUser.userProfile.setImageBitmap(bitmap);
                        createUser.camIconSignUP.setVisibility(View.GONE);
                        //calling function to encode bitmap
                        encodeImage = encodedImage(bitmap);
                    }
                    catch (Exception e){
                        Log.d("error",e.getMessage());
                    }
                }
                createAcc();
            }
            else{
                createUser.buttonCreateUser.setVisibility(View.VISIBLE);
                createUser.progressCreateUser.setVisibility(View.GONE);
            }
        });
    }

    private void createAcc(){
        //Getting the reference or instances of firebasestore to store values
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        //Creating an hashmap so that our user values will be mapped exactly into already our mentioned values easily
        HashMap<String,String> user_vals = new HashMap<>();
        user_vals.put(Constants.KEY_USER_NAME,createUser.usernameCreateUser.getEditText().getText().toString());
        user_vals.put(Constants.KEY_PHONE,phone);
        user_vals.put(Constants.KEY_PASSWORD,createUser.userpasswordCreateUser.getEditText().getText().toString());
        user_vals.put(Constants.KEY_IMAGE,encodeImage);
        database.collection(Constants.KEY_COLLECTION_USERS).add(user_vals)
                .addOnSuccessListener(documentReference -> {
                    //Storingser name,image,is signed in = true in preference manager class
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                    preferenceManager.putString(Constants.KEY_PHONE,phone);
                    preferenceManager.putString(Constants.KEY_USER_ID,documentReference.getId());
                    preferenceManager.putString(Constants.KEY_USER_NAME,createUser.usernameCreateUser.getEditText().getText().toString());
                    preferenceManager.putString(Constants.KEY_IMAGE,encodeImage);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    createUser.buttonCreateUser.setVisibility(View.VISIBLE);
                    createUser.progressCreateUser.setVisibility(View.GONE);
                    AndroidUtils.showToast(getApplicationContext(),e.getMessage());
                });


    }

    private boolean isDetailsValid(){
        if(validateUserName()&&validate_userPassword()&&validate_userConfirmPassword()){
            return true;
        }
        return false;
    }

    private boolean validateUserName(){
        String user_name=createUser.usernameCreateUser.getEditText().getText().toString();
        if(user_name.isEmpty()||user_name.length()<4){
            createUser.usernameCreateUser.setError("Username must contains atleast 4 letters");
            return false;
        }
        else{
            createUser.usernameCreateUser.setError(null);
            return true;
        }
    }

    private boolean validate_userPassword(){
        String pattern = "^" + "(?=.*[a-zA-Z])" + "(?=\\S+$)" + ".{6,}" + "$";
        String pass = createUser.userpasswordCreateUser.getEditText().getText().toString();
        if(pass.isEmpty()||!pass.matches(pattern)){
            createUser.userpasswordCreateUser.setError("Enter a Strong password");
            return false;
        }
        else{
            createUser.userpasswordCreateUser.setError(null);
            return true;
        }
    }

    private boolean validate_userConfirmPassword(){
        String pattern = "^" + "(?=.*[a-zA-Z])" + "(?=\\S+$)" + ".{6,}" + "$";
        String pass=createUser.userConfirmpasswordCreateUser.getEditText().getText().toString();
        if(pass.isEmpty()||!pass.matches(pattern)){
            createUser.userConfirmpasswordCreateUser.setError("Enter a Strong passord");
            return false;
        }
        else{
            createUser.userConfirmpasswordCreateUser.setError(null);
            return true;
        }
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result->{
        //Checking that the result we got is ok or not
                if(result.getResultCode()==RESULT_OK){
                    //Ensuring its not null
                    if(result.getData()!=null){
                        //Storing the data's url to image_url
                        Uri image_url = result.getData().getData();
                        try{
                            //contentResolver interacts with content provider(user) and open the input stream of file image url
                            InputStream input = getContentResolver().openInputStream(image_url);
                            //converting into bitmap
                            Bitmap bitmap = BitmapFactory.decodeStream(input);
                            //Setting the image on layout
                            createUser.userProfile.setImageBitmap(bitmap);
                            createUser.camIconSignUP.setVisibility(View.GONE);
                            //calling function to encode bitmap
                            encodeImage = encodedImage(bitmap);
                        }
                        catch (Exception e){
                            Log.d("error",e.getMessage());
                        }
                    }
                }
            });

    //Encoding the image
    private String encodedImage(Bitmap bitmap) {
        //Setting width
        int previewWidth = 512;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        //Creating a image with customized height and width
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //Compressing our image into jpeg format then putting it inside ByteArrayOutputStream
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        //then we converting it into byteArray
        byte[] bytes = byteArrayOutputStream.toByteArray();
        //Encrypting it
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
}