package com.dounets.vchat.gcm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dounets.vchat.ui.activity.ReceiveVideoPlay;
import com.google.android.gms.gcm.GcmListenerService;

import java.util.Date;

/**
 * Created by vinaymaneti on 2/25/16.
 */
public class MyGcmPushReceiver extends GcmListenerService {

    private static final String TAG = MyGcmPushReceiver.class.getSimpleName();

    private NotificationUtils notificationUtils;

    /**
     * Called when message is received.
     *
     * @param from   SenderID of the sender.
     * @param bundle Data bundle containing message data as key/value pairs.
     *               For Set of keys use data.keySet().
     */

    @Override
    public void onMessageReceived(String from, Bundle bundle) {
        boolean isBackground = Boolean.valueOf(bundle.getString("is_background"));
        String sendUserId = bundle.getString("send_user_id");
        String notificationBody = bundle.getString("gcm.notification.body");
        String notificationTitle = bundle.getString("gcm.notification.title");
        String videoId = bundle.getString("video_id");
        String videoUrl = bundle.getString("url");
        String notificationE = bundle.getString("gcm.notification.e");

        processUserMessage(notificationTitle, notificationBody, isBackground, videoUrl);
    }

    /**
     * Processing user specific push message
     * It will be displayed with / without image in push notification tray
     */
    private void processUserMessage(String title, String message, boolean isBackground, String data) {
        if (!isBackground) {

            // verifying whether the app is in background or foreground
//            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
//
//                // app is in foreground, broadcast the push message
//                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
//                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//
//                // play notification sound
//                NotificationUtils notificationUtils = new NotificationUtils();
//                notificationUtils.playNotificationSound();
//            } else {
//
//                // app is in background. show the message in notification try
//                Intent resultIntent = new Intent(getApplicationContext(), ReceiveVideo.class);
//
//                // check for push notification image attachment
//                if (TextUtils.isEmpty(imageUrl)) {
//                    showNotificationMessage(getApplicationContext(), title, user.getName() + " : " + message.getMessage(), message.getCreatedAt(), resultIntent);
//                } else {
//                    // push notification contains image
//                    // show it with the image
//                    showNotificationMessageWithBigImage(getApplicationContext(), title, message.getMessage(), message.getCreatedAt(), resultIntent, imageUrl);
//                }
//            }

        } else {
            // the push notification is silent, may be other operations needed
            // like inserting it in to SQLite
        }

        Intent resultIntent = new Intent(getApplicationContext(), ReceiveVideoPlay.class);
        resultIntent.putExtra("video_url", data);
        showNotificationMessage(getApplicationContext(), title, message, String.valueOf(new Date().getTime()), resultIntent);
    }


    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}