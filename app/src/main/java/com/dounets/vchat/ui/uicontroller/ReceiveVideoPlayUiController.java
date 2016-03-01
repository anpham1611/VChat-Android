package com.dounets.vchat.ui.uicontroller;

import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.dounets.vchat.R;
import com.dounets.vchat.net.api.ApiResponse;
import com.dounets.vchat.net.helper.ApiHelper;
import com.dounets.vchat.ui.activity.ReceiveVideoPlay;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import java.io.File;

import bolts.Continuation;
import bolts.Task;
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

//    @Bind(R.id.btnBack)
//    Button btnBack;

//    @Bind(R.id.btnReply)
//    Button btnReply;

    public ReceiveVideoPlayUiController(ReceiveVideoPlay activity) {
        this.activity = activity;
        ButterKnife.bind(this, activity);
        init();
    }

    private void init() {
//        btnBack.setOnClickListener(this);
//        btnReply.setOnClickListener(this);
    }

    public void playVideo(String url) {

        progressView.setVisibility(View.GONE);
        ApiHelper.doRequestDownloadVideo(activity, url).continueWith(new Continuation<ApiResponse, Object>() {
            @Override
            public Object then(final Task<ApiResponse> task) throws Exception {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (task.isFaulted()) {
                            Toast.makeText(activity, "Connect server failed. Please try again!", Toast.LENGTH_LONG).show();
                            return;
                        }
//                        Toast.makeText(activity, "Success!", Toast.LENGTH_LONG).show();

                        Uri uri = Uri.parse(task.getResult().getBody());
                        videoView.setVideoURI(uri);
                        videoView.requestFocus();
                        videoView.start();
                        progressView.setVisibility(View.GONE);
                        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {

                                // Delete video
                                File video = new File(task.getResult().getBody());
                                video.delete();

                                // Back to main screen
                                activity.callFishReceivedVideo();
                            }
                        });
                    }
                });
                return null;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

//            case R.id.btnBack:
//                activity.finish();
//                break;
//
//            case R.id.btnReply:
//                activity.doRequestReply();
//                break;

            default:
                break;
        }
    }
}
