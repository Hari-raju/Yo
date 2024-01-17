package com.raju.yo.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.raju.yo.databinding.SearchedUserListBinding;
import com.raju.yo.listeners.UserListeners;
import com.raju.yo.user_models.Users;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    private final List<Users> list;
    private final UserListeners userListeners;
    public UsersAdapter(List<Users> users,UserListeners userListeners){
        this.list=users;
        this.userListeners=userListeners;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SearchedUserListBinding itemContainerBinding = SearchedUserListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new UserViewHolder(itemContainerBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUsersData(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{

        //We are binding our user container with recycler view holder
        SearchedUserListBinding containerBinding;
        UserViewHolder(SearchedUserListBinding userItemContainerBinding){
            super(userItemContainerBinding.getRoot());
            containerBinding=userItemContainerBinding;
        }

        //Setting users data on layout

        void setUsersData(Users user){
            containerBinding.textPhone.setText(user.phone);
            containerBinding.textName.setText(user.user_name);
            containerBinding.userListsProfile.setImageBitmap(getUserImage(user.image));
            containerBinding.getRoot().setOnClickListener(view -> userListeners.onUserClicked(user));

        }

        //Getting an image by decrypting it
        public Bitmap getUserImage(String encryptedImage){
            byte[] arr= Base64.decode(encryptedImage,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(arr,0, arr.length);
        }
    }
}
