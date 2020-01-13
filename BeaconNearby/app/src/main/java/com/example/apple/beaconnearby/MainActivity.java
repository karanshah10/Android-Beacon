package com.example.apple.beaconnearby;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeOptions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    Button start, end;
    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_RESOLVE_ERROR = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // startService(new Intent(this, BeaconService.class));
       /* mGoogleApiClient = new GoogleApiClient.Builder(this)
               .addConnectionCallbacks(this)
               .addOnConnectionFailedListener(this)
               .addApi(Nearby.MESSAGES_API, new MessagesOptions.Builder().setPermissions(NearbyPermissions.BLE).build())
               .build();*/

        start = (Button) findViewById(R.id.startService);
        end = (Button) findViewById(R.id.endService);
        start.setOnClickListener(this);
        end.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == start) {
            startActivity(new Intent(this, NewClass.class));
        } else {
            stopService(new Intent(this, BeaconService.class));
        }
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
    protected void onStart() {
        super.onStart();
//        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        SubscribeOptions options = new SubscribeOptions.Builder().setStrategy(Strategy.BLE_ONLY).build();
        ResultCallback<Status> callback = new BackgroundRegisterCallback();
        Intent serviceIntent = new Intent(this, BeaconService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Nearby.Messages.subscribe(mGoogleApiClient, pendingIntent, options).setResultCallback(callback);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("TAG", "onConnectionFailed");
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
}
