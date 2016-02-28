package com.dounets.vchat.ui.uicontroller;

import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import com.dounets.vchat.R;
import com.dounets.vchat.ui.activity.ReceiveVideoPlay;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by vinaymaneti on 2/28/16.
 */
public class ReceiveVideoPlayUiController implements View.OnClickListener {

    ReceiveVideoPlay activity;

    @Bind(R.id.video_player_view)
    VideoView videoView;

//    @Bind(R.id.button1)
//    Button button;

    public ReceiveVideoPlayUiController(ReceiveVideoPlay activity) {
        this.activity = activity;
        ButterKnife.bind(this, activity);
        init();
    }

    private void init() {
//        button.setOnClickListener(this);
    }

    public void playVideo(String url) {
        Uri uri2 = Uri.parse(url);
        videoView.setVideoURI(uri2);
        videoView.requestFocus();
        videoView.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.button1:
//                activity.rePlayVideo();
//                break;

            default:
                break;
        }
    }
}
