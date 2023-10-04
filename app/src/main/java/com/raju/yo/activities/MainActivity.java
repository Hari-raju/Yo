package com.raju.yo.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.raju.yo.R;
import com.raju.yo.connectivity.Constants;
import com.raju.yo.connectivity.PreferenceManager;
import com.raju.yo.databinding.ActivityMainBinding;
import com.raju.yo.utils.AndroidUtils;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private PreferenceManager preferenceManager;
    private Bitmap bitmap;
    private ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        init();
        Listeners();
    }

    private void Listeners() {
        mainBinding.profile.setOnClickListener(v -> {
            mainBinding.drawerLayout.openDrawer(GravityCompat.START);
        });
    }

    @Override
    public void onBackPressed() {
        if (mainBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mainBinding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void init() {
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        mainBinding.profile.setImageBitmap(bitmap);
        View header = mainBinding.navView.getHeaderView(0);
        TextView userName = (TextView) header.findViewById(R.id.header_userName);
        userName.setText(preferenceManager.getString(Constants.KEY_USER_NAME));
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id =item.getItemId();
        if(id==R.id.nav_home){
            AndroidUtils.showToast(getApplicationContext(),"home");
        } else if (id==R.id.nav_edit) {
            AndroidUtils.showToast(getApplicationContext(),"edit");
        }
        return false;
    }
}