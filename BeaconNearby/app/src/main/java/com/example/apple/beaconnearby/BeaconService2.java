package com.example.apple.beaconnearby;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

/**
 * Created by Apple on 8/17/2017.
 */

public class BeaconService2 extends IntentService {
    MediaPlayer player;

    public BeaconService2() {
        super("BeaconService2");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d("TAG", "onHandleIntent");
        if (intent != null) {
            player = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
            player.setLooping(true);
            player.start();
            Nearby.Messages.handleIntent(intent, new MessageListener() {
                @Override
                public void onFound(Message message) {
                    String beaconMessage = new String(message.getContent());
                    Log.d("TAG", "onFound" + beaconMessage);
                    showFoundNotification(beaconMessage);
                }

                @Override
                public void onLost(Message message) {
                    Log.d("TAG", "onLost" + message.toString());
                    showLostNotification(message);
                }
            });
        }
    }

    private void showLostNotification(Message message) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1, new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.star_on)
                .setContentTitle("Lost beacon")
                .setContentText(message.toString()).build());
    }

    private void showFoundNotification(String message) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1, new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.star_on)
                .setContentTitle("Found beacon")
                .setContentText(message.toString())
                .setStyle(
                        new android.support.v4.app.NotificationCompat.BigTextStyle().bigText("ID:" + message))
                .build());
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_REDELIVER_INTENT;
    }
}
