package com.example.apple.nearbyapi;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.apple.nearbyapi.databinding.ActivityMainBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.MessagesOptions;
import com.google.android.gms.nearby.messages.NearbyPermissions;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeOptions;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private ActivityMainBinding binding;
    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_RESOLVE_ERROR = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        Intent serviceIntent = new Intent(this, BeaconService1.class);
        // startService(serviceIntent);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API, new MessagesOptions.Builder().setPermissions(NearbyPermissions.BLE).build())
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .build();

    }

    private MessageListener mMessageListener = new MessageListener() {
        @Override
        public void onFound(Message message) {
            String mesageAsString = new String(message.getContent());
            Log.d("TAG", "BEacon message received " + mesageAsString);
        }

        @Override
        public void onLost(Message message) {
            super.onLost(message);
            Log.d("TAG", "BACKGROUND MESSAGE LOST = " + message.getContent());
        }
    };

    private void subscribe() {
        SubscribeOptions options = new SubscribeOptions.Builder().setStrategy(Strategy.BLE_ONLY).build();
        Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener, options);

        ResultCallback<Status> callback = new BackgroundRegisterCallback();
        Intent serviceIntent = new Intent(this, BeaconService1.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Nearby.Messages.subscribe(mGoogleApiClient, pendingIntent, options).setResultCallback(callback);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        Log.e("TAG", "GoogleApiClient connectioin start");
    }

    private class BackgroundRegisterCallback implements ResultCallback<Status> {

        @Override
        public void onResult(@NonNull Status status) {
            if (status.isSuccess()) {
                Log.d("TAG", "Background Register Success");
            } else {
                Log.d("TAG", "Background Register ERROR" + status.getStatusCode());
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        subscribe();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("TAG", "GoogleApiClient connectioin failed");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            if (requestCode == RESULT_OK) {
                mGoogleApiClient.connect();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onStop() {
        SubscribeOptions options = new SubscribeOptions.Builder().setStrategy(Strategy.BLE_ONLY).build();
        ResultCallback<Status> callback = new BackgroundRegisterCallback();
        Intent serviceIntent = new Intent(this, BeaconService1.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Nearby.Messages.subscribe(mGoogleApiClient, pendingIntent, options).setResultCallback(callback);
        if (mGoogleApiClient.isConnected()) {
            unSubscribe();
            mGoogleApiClient.disconnect();
            Log.e("TAG", "GoogleApiClient connectioin stop");
        }
        super.onStop();
    }

    private void unSubscribe() {
        Nearby.Messages.unsubscribe(mGoogleApiClient, mMessageListener);
    }


}
