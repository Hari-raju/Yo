package com.raju.yo.connectivity;

import java.util.HashMap;

public class Constants {
    public static final String KEY_COLLECTION_USERS="users";
    public static final String KEY_PHONE="phone_number";
    public static final String KEY_USER_NAME="user_name";
    public static final String KEY_PASSWORD="password";
    public static final String KEY_PREFERENCE_NAME="yoPreference";
    public static final String KEY_IS_SIGNED_IN="isSignedIn";
    public static final String KEY_USER_ID="userId";
    public static final String KEY_IMAGE="image";
    public static final String KEY_FCM_TOKEN="fcmToken";
    public static final String KEY_USER="user";
    public static final String KEY_COLLECTION_CHAT="chat";
    public static final String KEY_SENDER_ID="senderId";
    public static final String KEY_RECEIVER_ID="receiverId";
    public static final String KEY_MESSAGE="message";
    public static final String KEY_TIMESTAMP="timestamp";
    public static final String KEY_SENDER_NAME="senderName";
    public static final String KEY_RECEIVER_NAME="receiverName";
    public static final String KEY_SENDER_IMAGE="senderImage";
    public static final String KEY_RECEIVER_IMAGE="receiverImage";
    public static final String KEY_LAST_MESSAGE="lastMessage";
    public static final String KEY_COLLECTION_CONVERSATIONS="conversations";
    public static final String KEY_AVAILABILITY ="availability";
    public static final String KEY_LAST_SEEN="lastSeen";
    public static final String REMOTE_MSG_AUTHORIZATION="Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE="Content-Type";
    public static final String REMOTE_MSG_DATA="data";
    public static final String REMOTE_MSG_REGISTRATION_IDS="registration_ids";
    public static HashMap<String,String> remoteMsgHeaders=null;
    public static HashMap<String,String> getRemoteMsgHeaders(){
        if(remoteMsgHeaders==null){
            remoteMsgHeaders=new HashMap<>();
            remoteMsgHeaders.put(
                    Constants.REMOTE_MSG_AUTHORIZATION,
                    "key=AAAAyLsOjCA:APA91bHc23rakn_ifMYeqYiPF3rdGbbmUwWlyVC-7bvFwmfY6nJLHo8lEP8mRXWrGQgitoZuXUUz7zk8RS1XAv4s0NOvGNvoWI2KeTdjUk71nPiyDkav9O2NOSscMLyOHzJ9GV6RuP4a"
            );
            remoteMsgHeaders.put(
                    Constants.REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
            );
        }
        return remoteMsgHeaders;
    }
}
