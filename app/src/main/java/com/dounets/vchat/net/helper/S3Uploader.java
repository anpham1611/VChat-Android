package com.dounets.vchat.net.helper;

import com.dounets.vchat.net.api.ApiClient;
import com.dounets.vchat.net.api.ApiRequest;
import com.dounets.vchat.net.api.ApiResponse;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import bolts.Continuation;
import bolts.Task;

public class S3Uploader extends BaseHelper {

    public static Task<String> uploadFileToS3InBackground(final String filePath, final String listUserIds) {
        final Task<String>.TaskCompletionSource uploadTask = Task.create();

        final AtomicReference<String> pathRef = new AtomicReference<>();
        final AtomicReference<String> urlRef = new AtomicReference<>();

        HashMap<String, String> params = new HashMap<>();
        ApiRequest request = new ApiRequest(ApiRequest.Method.GET, getPrefixUrl() + "pre_signed_url", params);
        ApiClient.callInBackground(request).onSuccessTask(new Continuation<ApiResponse, Task<Void>>() {
            @Override
            public Task<Void> then(Task<ApiResponse> task) throws Exception {
                JSONObject json = new JSONObject(task.getResult().getBody());
                pathRef.set(json.getString("name"));
                urlRef.set(json.getString("url"));
                return uploadVideoFileToS3(urlRef.get(), filePath);

            }
        }).continueWith(new Continuation<Void, Object>() {
            @Override
            public Object then(Task<Void> task) throws Exception {
                if (task.isFaulted()) {
                    uploadTask.setError(task.getError());
                } else {

                    JSONObject objectRes = new JSONObject();
                    objectRes.put("name", pathRef.get());
                    objectRes.put("users", listUserIds);

                    uploadTask.setResult(objectRes.toString());
                }
                return null;
            }
        });
        return uploadTask.getTask();
    }

    private static Task uploadVideoFileToS3(final String presignedUrl, final String filePath) {
        final File file = new File(filePath);
        if(file.exists()) {
            return Task.callInBackground(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    Request request = new Request.Builder()
                            .url(presignedUrl)
                            .put(RequestBody.create(MediaType.parse("application/octet-stream"), file))
                            .build();

                    Response response = new OkHttpClient().newCall(request).execute();
                    return null;
                }
            });
        }
        return null;
    }
}
