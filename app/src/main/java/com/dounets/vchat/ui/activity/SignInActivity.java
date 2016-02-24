package com.dounets.vchat.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.dounets.vchat.App;
import com.dounets.vchat.R;
import com.dounets.vchat.net.api.ApiError;
import com.dounets.vchat.net.api.ApiResponse;
import com.dounets.vchat.net.helper.ApiHelper;
import com.dounets.vchat.ui.uicontroller.SignInActivityUiController;

import bolts.Continuation;
import bolts.Task;

public class SignInActivity extends PrimaryActivity {

    private SignInActivityUiController uiController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        uiController = new SignInActivityUiController(this);
    }

    public void onClickNext(String name) {
        uiController.getTvName().setError(null);
        if (uiController.getName().equals("")) {
            uiController.getTvName().setError(getString(R.string.error_invalid_name));

        } else {
            asyncRequestRegister(name, "");
        }
    }

    private void asyncRequestRegister(String phoneNumber, String deviceId) {
        showLoadingMessage(R.string.processing);

//        ApiHelper.doRegister(phoneNumber, deviceId).continueWith(new Continuation<ApiResponse, Object>() {
//            @Override
//            public Object then(final Task<ApiResponse> task) throws Exception {
//                dismissLoadingMessage();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (task.isFaulted()) {
//                            Toast.makeText(getBaseContext(), "Failed.", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
//                        startActivity(intent);
//                    }
//                });
//                return null;
//            }
//        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                dismissLoadingMessage();
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }, 3000);
    }

}
