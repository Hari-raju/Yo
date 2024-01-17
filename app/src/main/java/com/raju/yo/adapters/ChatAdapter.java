package com.raju.yo.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.raju.yo.databinding.ItemReceivedContainerBinding;
import com.raju.yo.databinding.ItemsUserSentContainerBinding;
import com.raju.yo.user_models.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Bitmap receiverProfile;
    private final List<ChatMessage> messages;
    private final String senderId;

    private static final int VIEW_TYPE_SENT=1;
    private static final int VIEW_TYPE_RECEIVE=2;

    public ChatAdapter(Bitmap receiverProfile, List<ChatMessage> messages, String senderId) {
        this.receiverProfile = receiverProfile;
        this.messages = messages;
        this.senderId = senderId;
    }

    public void setReceiverProfile(Bitmap bitmap){
        receiverProfile=bitmap;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==VIEW_TYPE_SENT){
            return new SentMessageViewHolder(ItemsUserSentContainerBinding.inflate(
                    LayoutInflater.from(parent.getContext()),parent,false));
        }
        else{
            return new ReceivedMessageViewHolder(ItemReceivedContainerBinding.inflate(
                    LayoutInflater.from(parent.getContext()),parent,false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position)==VIEW_TYPE_SENT){
            ((SentMessageViewHolder)holder).setData(messages.get(position));
        }
        else{
            ((ReceivedMessageViewHolder)holder).setData(messages.get(position),receiverProfile);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        //If messages sender id is equals to our sender id means we are trying to send message else we will receive message
        if(messages.get(position).senderId.equals(senderId)){
            return VIEW_TYPE_SENT;
        }
        else{
            return VIEW_TYPE_RECEIVE;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemsUserSentContainerBinding sentContainerBinding;
        SentMessageViewHolder(ItemsUserSentContainerBinding itemsUserSentContainerBinding){
            super(itemsUserSentContainerBinding.getRoot());
            sentContainerBinding=itemsUserSentContainerBinding;
        }

        void setData(ChatMessage chatMessage){
            sentContainerBinding.textMessage.setText(chatMessage.message);
            sentContainerBinding.textDateTime.setText(chatMessage.dateTime);
        }
    }



    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemReceivedContainerBinding receivedContainerBinding;
        ReceivedMessageViewHolder(ItemReceivedContainerBinding itemRecievedContainerBinding){
            super(itemRecievedContainerBinding.getRoot());
            receivedContainerBinding=itemRecievedContainerBinding;
        }

        void setData(ChatMessage chatMessage, Bitmap receiverProfileImg){
            receivedContainerBinding.textMessage.setText(chatMessage.message);
            receivedContainerBinding.textDateTime.setText(chatMessage.dateTime);
            if(receiverProfileImg!=null) {
                receivedContainerBinding.imageProfile.setImageBitmap(receiverProfileImg);
            }
        }
    }
}

