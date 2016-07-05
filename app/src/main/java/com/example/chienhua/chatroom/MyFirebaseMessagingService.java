package com.example.chienhua.chatroom;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by chienhua on 2016/7/4.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {        //When recevied message, and Activity still alive, will use 'onMessageReceived'
        super.onMessageReceived(remoteMessage);
        Log.e("FCM", "From: " + remoteMessage.getFrom());
        Log.e("FCM", "Notification Message Body: " + remoteMessage.getNotification().getBody());

    }
}
