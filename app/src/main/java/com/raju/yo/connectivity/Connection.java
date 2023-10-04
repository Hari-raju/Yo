package com.raju.yo.connectivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Connection {
    public boolean isConnected(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo=manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo=manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return wifiInfo != null && wifiInfo.isConnected() || mobileInfo != null && mobileInfo.isConnected();
    }
}
