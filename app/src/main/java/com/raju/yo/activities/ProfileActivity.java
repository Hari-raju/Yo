package com.raju.yo.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.raju.yo.R;
import com.raju.yo.connectivity.Constants;
import com.raju.yo.connectivity.PreferenceManager;
import com.raju.yo.databinding.ActivityProfileBinding;
import com.raju.yo.utils.AndroidUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ProfileActivity extends BaseActivity {

    private ActivityProfileBinding profileBinding;
    private PreferenceManager preferenceManager;
    private ActivityResultLauncher<Intent> pickImage;
    private String encoded;
    private FirebaseFirestore database;
    private DocumentReference documentReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileBinding=ActivityProfileBinding.inflate(getLayoutInflater());
        preferenceManager=new PreferenceManager(getApplicationContext());
        setContentView(profileBinding.getRoot());
        database = FirebaseFirestore.getInstance();
        documentReference=
                database.collection(Constants.KEY_COLLECTION_USERS)
                        .document(preferenceManager.getString(Constants.KEY_USER_ID));
        init();
        Listeners();
    }

    private void Listeners(){
        profileBinding.addNewPic.setOnClickListener(image->{
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            pickImage.launch(intent);
                            return null;
                        }
                    });
        });

        profileBinding.profileBack.setOnClickListener(v->{
            super.onBackPressed();
        });

        profileBinding.profileEditName.setOnClickListener(v->{
           try{
               final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ProfileActivity.this,R.style.BottomSheetDialogTheme);
               View container = LayoutInflater.from(getApplicationContext())
                       .inflate(
                               R.layout.bottom_sheet,(RelativeLayout)findViewById(R.id.container_sheet)
                       );
               EditText newName = container.findViewById(R.id.newName);
               newName.requestFocus();
               container.findViewById(R.id.cancel).setOnClickListener(r->bottomSheetDialog.dismiss());
               container.findViewById(R.id.save).setOnClickListener(res->{
                   String name = newName.getText().toString();
                   if(name.isEmpty()||name.length()<4){
                       newName.setError("Invalid Name");
                   }
                   else{
                       profileBinding.profileProgressBar.setVisibility(View.VISIBLE);
                       profileBinding.profileEditName.setVisibility(View.GONE);
                       newName.setError(null);
                       documentReference.update(Constants.KEY_USER_NAME,name)
                               .addOnCompleteListener(complete->{
                                   profileBinding.profileProgressBar.setVisibility(View.GONE);
                                   profileBinding.profileEditName.setVisibility(View.VISIBLE);
                                   preferenceManager.putString(Constants.KEY_USER_NAME,name);
                                   profileBinding.profileUserName.setText(preferenceManager.getString(Constants.KEY_USER_NAME));
                               })
                               .addOnFailureListener(result ->{
                                   profileBinding.profileProgressBar.setVisibility(View.GONE);
                                   profileBinding.profileEditName.setVisibility(View.VISIBLE);
                                   AndroidUtils.showToast(getApplicationContext(),"Upload Failed");
                                   Log.d("checking",result.getMessage());
                               });
                       bottomSheetDialog.dismiss();
                   }
               });

               bottomSheetDialog.setContentView(container);
               bottomSheetDialog.show();


           }
           catch(Exception e){
               AndroidUtils.showToast(getApplicationContext(),e.getMessage());
               Log.d("checks",e.getMessage());
           }
        });
    }

    private void init(){
        Bitmap bitmap = decodeImage(preferenceManager.getString(Constants.KEY_IMAGE));
        profileBinding.profileUserProfile.setImageBitmap(bitmap);
        profileBinding.profileUserName.setText(preferenceManager.getString(Constants.KEY_USER_NAME));
        profileBinding.profileUserPhone.setText(preferenceManager.getString(Constants.KEY_PHONE));

        //Setting result launcher to pick image
        pickImage=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result->{
                    profileBinding.profileProgressBar.setVisibility(View.VISIBLE);
                    profileBinding.addNewPic.setVisibility(View.GONE);
                    if(result.getResultCode()== Activity.RESULT_OK){
                        if(result.getData()!=null && result.getData().getData()!=null){
                            try{
                                Uri imageUri = result.getData().getData();
                                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                                Bitmap bitmap1 = BitmapFactory.decodeStream(inputStream);
                                encoded=encodeImage(bitmap1);
                                documentReference.update(Constants.KEY_IMAGE,encoded)
                                        .addOnSuccessListener(comp->{
                                            profileBinding.profileProgressBar.setVisibility(View.GONE);
                                            profileBinding.addNewPic.setVisibility(View.VISIBLE);
                                            preferenceManager.putString(Constants.KEY_IMAGE,encoded);
                                            profileBinding.profileUserProfile.setImageBitmap(bitmap1);
                                            AndroidUtils.showToast(getApplicationContext(),"Updated");
                                        })
                                        .addOnFailureListener(fail->{
                                            profileBinding.profileProgressBar.setVisibility(View.GONE);
                                            profileBinding.addNewPic.setVisibility(View.VISIBLE);
                                            AndroidUtils.showToast(getApplicationContext(),"Failed to update");
                                        });
                            }
                            catch (Exception e){
                                Log.d("errors",e.getMessage());
                            }
                        }
                    }
                });
    }

    private Bitmap decodeImage(String decoded) {
        byte[] bytes = Base64.decode(decoded,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
    }



    private String encodeImage(Bitmap bitmap){
        //Setting width
        int previewWidth = 512;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        //Creating a image with customized height and width
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //Compressing our image into jpeg format then putting it inside ByteArrayOutputStream
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        //then we converting it into byteArrray
        byte[] bytes = byteArrayOutputStream.toByteArray();
        //Encrypting it
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
}