package com.dounets.vchat.ui.activity;

import android.os.Bundle;

import com.dounets.vchat.R;
import com.dounets.vchat.ui.uicontroller.ReceiveVideoPlayUiController;

public class ReceiveVideoPlay extends PrimaryActivity {

    ReceiveVideoPlayUiController uiController;
    String videoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_video_play);
        uiController = new ReceiveVideoPlayUiController(this);

        videoUrl = getIntent().getStringExtra("video_url");
        uiController.playVideo(videoUrl);
    }

    public void rePlayVideo() {
        uiController.playVideo(videoUrl);
    }

}
