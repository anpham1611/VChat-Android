package com.dounets.vchat.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dounets.vchat.R;
import com.dounets.vchat.app.Config;
import com.dounets.vchat.data.model.Contact;
import com.dounets.vchat.gcm.GcmIntentService;
import com.dounets.vchat.gcm.NotificationUtils;
import com.dounets.vchat.helper.SharedPreferenceUtils;
import com.dounets.vchat.net.api.ApiResponse;
import com.dounets.vchat.net.helper.ApiHelper;
import com.dounets.vchat.net.helper.S3Uploader;
import com.dounets.vchat.ui.adapter.ContactAdapter;
import com.dounets.vchat.ui.uicontroller.MainActivityUiController;
import com.dounets.vchat.video.FFmpegRecorderActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bolts.Continuation;
import bolts.Task;

public class MainActivity extends PrimaryActivity {

    private MainActivityUiController uiController;
    private String TAG = MainActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int RECORD_REQUEST = 10000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private List<Contact> mData;
    private ContactAdapter mAdapter;
    private String mListUserIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mData = new ArrayList<>();
        mAdapter = new ContactAdapter(this, mData);
        uiController = new MainActivityUiController(this, mAdapter);

        init();
        asyncRequestGetListUsers();
    }

    private void init() {
        /*GCM*/
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    String token = intent.getStringExtra("token");
                    //Toast.makeText(getApplicationContext(), "GCM registration token: " + token, Toast.LENGTH_LONG).show();

                    // now subscribe to `global` topic to receive app wide notifications
                    subscribeToGlobalTopic();


                } else if (intent.getAction().equals(Config.SENT_TOKEN_TO_SERVER)) {
                    // gcm registration id is stored in our server's MySQL

                    //Toast.makeText(getApplicationContext(), "GCM registration token is stored in server!", Toast.LENGTH_LONG).show();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    handlePushNotification(intent);
                    //Toast.makeText(getApplicationContext(), "Push notification is received!", Toast.LENGTH_LONG).show();
                }
            }
        };

        if (checkPlayServices()) {
            registerGCM();
        }

    }

    /**
     * Handles new push notification
     */
    private void handlePushNotification(Intent intent) {

    }

    // subscribing to global topic
    private void subscribeToGlobalTopic() {
        Intent intent = new Intent(this, GcmIntentService.class);
        intent.putExtra(GcmIntentService.KEY, GcmIntentService.SUBSCRIBE);
        intent.putExtra(GcmIntentService.TOPIC, Config.TOPIC_GLOBAL);
        startService(intent);
    }

    private void asyncRequestGetListUsers() {

        showLoadingMessage(R.string.processing);

        ApiHelper.doGetListUsers(SharedPreferenceUtils.getString("user_id")).continueWith(new Continuation<ApiResponse, Object>() {
            @Override
            public Object then(final Task<ApiResponse> task) throws Exception {
                dismissLoadingMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (task.isFaulted()) {
                            Toast.makeText(getBaseContext(), "Failed.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            JSONArray objArr = new JSONArray(task.getResult().getBody());

                            mData.clear();
                            for (int i = 0; i < objArr.length(); i++) {
                                JSONObject obj = objArr.getJSONObject(i);
                                Contact contact = new Contact();
                                contact.setId(obj.getLong("id"));
                                contact.setName(obj.getString("name"));
                                contact.setMobile_id(obj.getString("token"));
                                mData.add(contact);
                            }
                            uiController.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
                return null;
            }
        });
    }

    public void onClickRecord(long id) {
        mListUserIds = "" + id;
        if (id == 0) {
            // All users
            List<String> inputArray = new ArrayList<>();
            for (int i=0; i<mData.size(); i++) {
                inputArray.add(mData.get(i).getId().toString());
            }
            if(inputArray.size() > 0) {
                mListUserIds = implodeArray(inputArray, ",");
            }
        }
        Intent i = new Intent(MainActivity.this, FFmpegRecorderActivity.class);
        i.putExtra("list_user_send", mListUserIds);
        startActivityForResult(i, RECORD_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RECORD_REQUEST && resultCode == RESULT_OK) {
            final String videoPath = data.getStringExtra("video_path");
            showLoadingMessage(R.string.sending);
            S3Uploader.uploadFileToS3InBackground(videoPath, mListUserIds).onSuccessTask(new Continuation<String, Task<ApiResponse>>() {
                @Override
                public Task<ApiResponse> then(Task<String> task) throws Exception {
                    JSONObject objectRes = new JSONObject(String.valueOf(task.getResult()));

                    return ApiHelper.doRequestSendPush(objectRes.getString("name"), objectRes.getString("users"));

                }

            }).continueWith(new Continuation<ApiResponse, Void>() {
                @Override
                public Void then(final Task<ApiResponse> task) throws Exception {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissLoadingMessage();
                            if (task.isFaulted()) {
                                Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT).show();

                            // Delete file
                            File file = new File(videoPath);
                            boolean deleted = file.delete();
                        }
                    });
                    return null;
                }
            });
        }
    }

    private static String implodeArray(List<String> inputArray, String glueString) {

        /** Output variable */
        String output = "";

        if (inputArray.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(inputArray.get(0));

            for (int i=1; i<inputArray.size(); i++) {
                sb.append(glueString);
                sb.append(inputArray.get(i));
            }

            output = sb.toString();
        }

        return output;
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


        // clearing the notification tray
        NotificationUtils.clearNotifications();
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
}
