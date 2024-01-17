package com.raju.yo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.raju.yo.adapters.UsersAdapter;
import com.raju.yo.connectivity.Constants;
import com.raju.yo.connectivity.PreferenceManager;
import com.raju.yo.databinding.ActivitySearchUsersBinding;
import com.raju.yo.listeners.UserListeners;
import com.raju.yo.user_models.Users;
import com.raju.yo.utils.AndroidUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchUsersActivity extends BaseActivity implements UserListeners {

    private ActivitySearchUsersBinding searchUsersBinding;
    private FirebaseFirestore database;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchUsersBinding = ActivitySearchUsersBinding.inflate(getLayoutInflater());
        setContentView(searchUsersBinding.getRoot());
        database = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        Listeners();
    }

    private void Listeners() {
        searchUsersBinding.searchBack.setOnClickListener(v -> {
            super.onBackPressed();
        });

        searchUsersBinding.search.setOnClickListener(v -> {
            searchUsersBinding.userLists.setVisibility(View.GONE);
            String res = searchUsersBinding.searchedName.getText().toString();
            if (res.isEmpty()) {
                searchUsersBinding.searchedName.setError("Enter User name");
            } else {
                searchUsersBinding.searchedName.setError(null);
                searchUsersBinding.searchProgress.setVisibility(View.VISIBLE);
                listUsers(res);
            }
        });
    }

    private void listUsers(String userName) {
        database.collection(Constants.KEY_COLLECTION_USERS).whereEqualTo(Constants.KEY_USER_NAME, userName)
                .get()
                .addOnCompleteListener(task -> {
                    searchUsersBinding.searchProgress.setVisibility(View.GONE);
                    //getting current id
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.getResult() != null && task.isSuccessful()) {
                        List<Users> usersList = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currentUserId.equals(queryDocumentSnapshot.getId())) {
                                //ignoring current users id
                                continue;
                            }
                            //other than current users id we are adding all users details into the list
                            Users user = new Users();
                            user.user_name = queryDocumentSnapshot.getString(Constants.KEY_USER_NAME);
                            user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            user.phone = queryDocumentSnapshot.getString(Constants.KEY_PHONE);
                            user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            user.id = queryDocumentSnapshot.getId();
                            usersList.add(user);
                        }
                        //After loop ends we are gonna display those details if its > 0
                        if (usersList.size() > 0) {
                            //Creating instance of our customized adapter
                            UsersAdapter usersAdapter = new UsersAdapter(usersList, this);
                            //setting up or connecting our layout adapter to customized adapter
                            searchUsersBinding.userLists.setAdapter(usersAdapter);
                            searchUsersBinding.userLists.setVisibility(View.VISIBLE);
                            searchUsersBinding.textErrorMsg.setVisibility(View.GONE);
                        } else {
                            showErrorMsg();
                        }
                    } else {
                        showErrorMsg();
                    }
                })
                .addOnFailureListener(fail -> {
                    AndroidUtils.showToast(getApplicationContext(), "Server Issues");
                    Log.d("issue", fail.getMessage());
                });
    }

    private void showErrorMsg() {
        searchUsersBinding.userLists.setVisibility(View.GONE);
        searchUsersBinding.textErrorMsg.setText(String.format("%s", "No users found"));
        searchUsersBinding.textErrorMsg.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUserClicked(Users user) {
        try {
            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            intent.putExtra(Constants.KEY_USER, user);
            startActivity(intent);
        } catch (Exception e) {
            AndroidUtils.showToast(this, e.getMessage());
        }
    }
}