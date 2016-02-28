package com.dounets.vchat.ui.activity;

import android.os.Bundle;
import android.widget.Toast;

import com.dounets.vchat.R;
import com.dounets.vchat.net.api.ApiResponse;
import com.dounets.vchat.net.helper.ApiHelper;
import com.dounets.vchat.ui.uicontroller.ReceiveVideoPlayUiController;

import bolts.Continuation;
import bolts.Task;

public class ReceiveVideoPlay extends PrimaryActivity {

    ReceiveVideoPlayUiController uiController;
    String videoUrl, sendUserId, videoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_video_play);
        uiController = new ReceiveVideoPlayUiController(this);

        videoUrl = getIntent().getStringExtra("video_url");
        sendUserId = getIntent().getStringExtra("send_user_id");
        videoId = getIntent().getStringExtra("video_id");
        uiController.playVideo(videoUrl);
    }

    public void callFishReceivedVideo() {
        ApiHelper.doRequestReceivedVideo(videoId, sendUserId).continueWith(new Continuation<ApiResponse, Object>() {
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
                        finish();
                    }
                });
                return null;
            }
        });
    }

    public void doRequestReply() {
//        Toast.makeText(this, "Coming soon!", Toast.LENGTH_LONG).show();
    }
}
