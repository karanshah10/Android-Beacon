package com.example.apple.beaconnearby;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

/**
 * Created by Apple on 8/16/2017.
 */

public class MessageRecevier extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d("TAG", "onReceive");
        Nearby.Messages.handleIntent(intent, new MessageListener() {
            @Override
            public void onFound(Message message) {
                super.onFound(message);
                String messageFromBeacon = new String(message.getContent());
                Log.d("TAG", "BACKGROUND MESSAGE ONFOUND" + messageFromBeacon);
                context.stopService(new Intent(context, BeaconService.class));
                context.startService(new Intent(context, BeaconService.class));
            }

            @Override
            public void onLost(Message message) {
                Log.d("TAG", "BACKGROUND MESSAGE ONLOST");
                context.stopService(new Intent(context, BeaconService.class));
                context.startService(new Intent(context, BeaconService.class));
                super.onLost(message);
            }
        });
    }
}
