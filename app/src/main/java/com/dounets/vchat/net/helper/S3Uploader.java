package com.dounets.vchat.net.helper;

import android.net.Uri;

import com.dounets.vchat.net.api.ApiClient;
import com.dounets.vchat.net.api.ApiRequest;
import com.dounets.vchat.net.api.ApiResponse;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import bolts.Continuation;
import bolts.Task;


public class S3Uploader extends BaseHelper {

    public static Task<String> uploadFileToS3InBackground(final Uri uri, final String fileExtension, final String mediaType) {
        final Task<String>.TaskCompletionSource uploadTask = Task.create();

        final AtomicReference<String> pathRef = new AtomicReference<>();
        final AtomicReference<String> urlRef = new AtomicReference<>();

        HashMap<String, String> params = new HashMap<>();
        params.put("extension", fileExtension);
        ApiRequest request = new ApiRequest(ApiRequest.Method.GET, getPrefixUrl() + "video", params);
        ApiClient.callInBackground(request).onSuccessTask(new Continuation<ApiResponse, Task<Void>>() {
            @Override
            public Task<Void> then(Task<ApiResponse> task) throws Exception {
                JSONObject json = new JSONObject(task.getResult().getBody());
                pathRef.set(json.getString("path"));
                urlRef.set(json.getString("url"));
                return uploadFileToS3(urlRef.get(), uri, mediaType);
            }
        }).continueWith(new Continuation<Void, Object>() {
            @Override
            public Object then(Task<Void> task) throws Exception {
                if (task.isFaulted()) {
                    uploadTask.setError(task.getError());
                } else {
                    uploadTask.setResult(pathRef.get());
                }
                return null;
            }
        });
        return uploadTask.getTask();
    }

    public static Task uploadFileToS3(final String presignedUrl, final Uri uri, final String mediaType) {
        final File file = new File(uri.getPath());
        return Task.callInBackground(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Request request = new Request.Builder()
                        .url(presignedUrl)
                        .put(RequestBody.create(MediaType.parse(mediaType), file))
                        .build();
                new OkHttpClient().newCall(request).execute();
                return null;
            }
        });
    }
}

