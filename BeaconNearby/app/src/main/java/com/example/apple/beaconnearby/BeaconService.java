package com.example.apple.beaconnearby;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.MessagesOptions;
import com.google.android.gms.nearby.messages.NearbyPermissions;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeOptions;

/**
 * Created by Apple on 8/16/2017.
 */

public class BeaconService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient = null;
    private static final int REQUEST_RESOLVE_ERROR = 1001;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setUpNearbyClientIfNeeded();
    }

    protected synchronized void buildGoogleApiClient() {
        this.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.MESSAGES_API, new MessagesOptions.Builder().setPermissions(NearbyPermissions.BLE).build())
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("TAG", "onStartCommand");
        setUpNearbyClientIfNeeded();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        return START_STICKY;
    }

    private void setUpNearbyClientIfNeeded() {
        if (mGoogleApiClient == null) {
            buildGoogleApiClient();
        }
    }

    @Override
    public void onDestroy() {

        Log.d("TAG", "onDestroy");
        if (this.mGoogleApiClient != null) {
            this.mGoogleApiClient = null;
        }
        super.onDestroy();
    }

    private void subscrib() {
        Log.d("TAG", "subscrib");
        SubscribeOptions options = new SubscribeOptions.Builder().setStrategy(Strategy.BLE_ONLY).build();

        Intent intent = new Intent(this, MessageRecevier.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Nearby.Messages.subscribe(mGoogleApiClient, pendingIntent, options);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("TAG", "onConnected");
        subscrib();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("TAG", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("TAG", "onConnectionFailed" + connectionResult.getErrorCode());

        setUpNearbyClientIfNeeded();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("TAG", "onTaskRemoved");
        super.onTaskRemoved(rootIntent);
    }
}
