package com.dounets.vchat.video;

import android.app.Activity;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.dounets.vchat.R;
import com.dounets.vchat.net.api.ApiResponse;
import com.dounets.vchat.net.helper.ApiHelper;
import com.dounets.vchat.net.helper.S3Uploader;
import com.dounets.vchat.ui.activity.PrimaryActivity;

import org.json.JSONObject;

import bolts.Continuation;
import bolts.Task;

public class FFmpegPreviewActivity extends PrimaryActivity implements TextureView.SurfaceTextureListener
        , OnClickListener, OnCompletionListener {

    private String path;
    private TextureView surfaceView;
    private Button cancelBtn;
    private Button finishBtn;
    private MediaPlayer mediaPlayer;
    private ImageView imagePlay;
    private String mListUserIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ffmpeg_preview);

        cancelBtn = (Button) findViewById(R.id.play_cancel);
        cancelBtn.setOnClickListener(this);

        finishBtn = (Button) findViewById(R.id.play_finish);
        finishBtn.setOnClickListener(this);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        surfaceView = (TextureView) findViewById(R.id.preview_video);

        RelativeLayout preview_video_parent = (RelativeLayout) findViewById(R.id.preview_video_parent);
        LayoutParams layoutParams = (LayoutParams) preview_video_parent
                .getLayoutParams();
        layoutParams.width = displaymetrics.widthPixels;
        layoutParams.height = displaymetrics.widthPixels;
        preview_video_parent.setLayoutParams(layoutParams);

        surfaceView.setSurfaceTextureListener(this);
        surfaceView.setOnClickListener(this);

        path = getIntent().getStringExtra("path");
        mListUserIds = getIntent().getStringExtra("list_user_send");

        imagePlay = (ImageView) findViewById(R.id.previre_play);
        imagePlay.setOnClickListener(this);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    protected void onStop() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            imagePlay.setVisibility(View.GONE);
        }
        super.onStop();
    }

    private void prepare(Surface surface) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(path);
            mediaPlayer.setSurface(surface);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.seekTo(0);
        } catch (Exception e) {
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1,
                                          int arg2) {
        prepare(new Surface(arg0));
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
        return false;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1,
                                            int arg2) {

    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture arg0) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_cancel:
                stop();
                break;
            case R.id.play_finish:
//                Toast.makeText(FFmpegPreviewActivity.this, getResources().getString(R.string.video_saved_path) + " " + path, Toast.LENGTH_SHORT).show();

                showLoadingMessage(R.string.sending);
                /*Upload video file to S3*/
                S3Uploader.uploadFileToS3InBackground(path, mListUserIds).onSuccessTask(new Continuation<String, Task<ApiResponse>>() {
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
                                    Toast.makeText(FFmpegPreviewActivity.this, "Send video failed!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Toast.makeText(FFmpegPreviewActivity.this, "Send video successfully!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                        return null;
                    }
                });

                finish();
                break;
            case R.id.previre_play:
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
                imagePlay.setVisibility(View.GONE);
                break;
            case R.id.preview_video:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    imagePlay.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

    private void stop() {
        mediaPlayer.stop();
        Intent intent = new Intent(this, FFmpegRecorderActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        stop();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        imagePlay.setVisibility(View.VISIBLE);
    }
}
