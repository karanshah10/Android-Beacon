package com.example.apple.beaconnearby;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

/**
 * Created by Apple on 8/16/2017.
 */

public class NearbyBackgroundService extends IntentService {

    private static final int MESSAGES_NOTIFICATION_ID = 1;

    public NearbyBackgroundService() {
        super(NearbyBackgroundService.class.getName());
    }

    public NearbyBackgroundService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Nearby.Messages.handleIntent(intent, new MessageListener() {
                @Override
                public void onFound(Message message) {
                    Log.i("", "found message = " + message.toString());
                }

                @Override
                public void onLost(Message message) {
                    Log.i("", "lost message = " + message);
                }
            });
        }
    }

}
