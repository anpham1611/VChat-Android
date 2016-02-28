package com.dounets.vchat.ui.uicontroller;

import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.dounets.vchat.R;
import com.dounets.vchat.ui.activity.ReceiveVideoPlay;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by vinaymaneti on 2/28/16.
 */
public class ReceiveVideoPlayUiController implements View.OnClickListener {

    ReceiveVideoPlay activity;

    @Bind(R.id.video_player_view)
    VideoView videoView;

    @Bind(R.id.progress_view)
    CircularProgressView progressView;

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

        Uri uri = Uri.parse(url);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
        progressView.setVisibility(View.VISIBLE);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.start();
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int arg1,
                                                   int arg2) {
                        // TODO Auto-generated method stub
                        progressView.setVisibility(View.GONE);
                        mp.start();
                    }
                });

            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                activity.callFishReceivedVideo();
            }
        });

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
