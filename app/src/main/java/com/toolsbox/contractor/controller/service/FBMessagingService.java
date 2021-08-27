package com.toolsbox.contractor.controller.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.TBApplication;
import com.toolsbox.contractor.common.utils.AppPreferenceManager;
import com.toolsbox.contractor.common.utils.StringHelper;
import com.toolsbox.contractor.view.activity.login.LoginActivity;
import com.toolsbox.contractor.view.activity.main.HomeActivity;
import com.toolsbox.contractor.view.activity.main.messages.ChatActivity;
import com.twilio.chat.NotificationPayload;

import java.util.Random;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import static com.toolsbox.contractor.common.Constant.PRE_FCM_TOKEN;

public class FBMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FBMessagingService";

    private NotificationManager notificationManager;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e(TAG, "New FCMToken:" + s);
    //    String token = AppPreferenceManager.getString(PRE_FCM_TOKEN, "");
        AppPreferenceManager.setString(PRE_FCM_TOKEN, s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if message contains a notification payload.
        String title = "";
        String message = "";
        PendingIntent pendingIntent = null;

        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Notification Received notification1");

            // Parse if twilio notification  or not
            if (StringHelper.isEmpty(remoteMessage.getData().get("n_type"))){
                // Twilio notification
                NotificationPayload payload = new NotificationPayload(remoteMessage.getData());
                NotificationPayload.Type type = payload.getType();
                if (type == NotificationPayload.Type.NEW_MESSAGE){
                    title = "New Message";
                }

                message = payload.getBody();
                String cSid = payload.getChannelSid();
                // Set up action Intent
                Intent intent = new Intent(this, ChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("sid", cSid);
                if (TBApplication.getActiveChannelSid().equals(cSid))
                    return;

                pendingIntent =
                        PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            } else {
                // General notification
                Intent intent;
                if (TBApplication.isgLoggedIn()){
                    intent = new Intent(this, HomeActivity.class);
                } else {
                    intent = new Intent(this, LoginActivity.class);
                }

                pendingIntent =
                        PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            }
        }

        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            message = remoteMessage.getNotification().getBody();
        }

        if (StringHelper.isEmpty(title) || StringHelper.isEmpty(message)) {
            return;
        }


        //You should use an actual ID instead, If not, messages are collapsed...
        int notificationId = new Random().nextInt(60000);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(getString(R.string.default_notification_channel_id), "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(R.color.colorBlue);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                        .setContentTitle(title)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentText(message)
                        .setContentIntent(pendingIntent)
                        .setOnlyAlertOnce(false);


        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setColor(ContextCompat.getColor(getBaseContext(), R.color.colorBlue));

        notificationManager.notify(notificationId, notificationBuilder.build());

    }
}

