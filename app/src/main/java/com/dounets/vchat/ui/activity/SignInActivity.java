package com.dounets.vchat.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.dounets.vchat.R;
import com.dounets.vchat.app.Config;
import com.dounets.vchat.gcm.GcmIntentService;
import com.dounets.vchat.helper.SharedPreferenceUtils;
import com.dounets.vchat.net.api.ApiClient;
import com.dounets.vchat.net.api.ApiResponse;
import com.dounets.vchat.net.helper.ApiHelper;
import com.dounets.vchat.ui.uicontroller.SignInActivityUiController;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;

import bolts.Continuation;
import bolts.Task;

public class SignInActivity extends PrimaryActivity {

    private SignInActivityUiController uiController;
    private String TAG = MainActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String mToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        uiController = new SignInActivityUiController(this);

        /*GCM*/
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    String token = intent.getStringExtra("token");

                    //Toast.makeText(getApplicationContext(), "GCM registration token: " + token, Toast.LENGTH_LONG).show();
                    mToken = token;
                    SharedPreferenceUtils.saveString("token", mToken);

                } else if (intent.getAction().equals(Config.SENT_TOKEN_TO_SERVER)) {
                    // gcm registration id is stored in our server's MySQL

                    //Toast.makeText(getApplicationContext(), "GCM registration token is stored in server!", Toast.LENGTH_LONG).show();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    //Toast.makeText(getApplicationContext(), "Push notification is received!", Toast.LENGTH_LONG).show();
                }
            }
        };

        if (checkPlayServices()) {
            registerGCM();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    // starting the service to register with GCM
    private void registerGCM() {
        Intent intent = new Intent(this, GcmIntentService.class);
        intent.putExtra("key", "register");
        startService(intent);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported. Google Play Services not installed!");
                //Toast.makeText(getApplicationContext(), "This device is not supported. Google Play Services not installed!", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    public void onClickNext(String name) {
        uiController.getTvName().setError(null);
        if (uiController.getName().equals("")) {
            uiController.getTvName().setError(getString(R.string.error_invalid_name));

        } else {
            asyncRequestRegister(name, mToken);
        }
    }

    private void asyncRequestRegister(String name, String deviceId) {

        SharedPreferenceUtils.saveString("user_name", name);

        showLoadingMessage(R.string.processing);

        ApiHelper.doRegister(name, deviceId).continueWith(new Continuation<ApiResponse, Object>() {
            @Override
            public Object then(final Task<ApiResponse> task) throws Exception {
                dismissLoadingMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (task.isFaulted()) {
                            Toast.makeText(getBaseContext(), "Connect server failed. Please try again!", Toast.LENGTH_LONG).show();
                            return;
                        }

                        try {
                            JSONObject obj = new JSONObject(task.getResult().getBody());
                            String id = obj.getString("id");
                            SharedPreferenceUtils.saveString("user_id", id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
                return null;
            }
        });

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                dismissLoadingMessage();
//                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        }, 1000);

    }

}
