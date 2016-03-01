package com.dounets.vchat.net.api;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import bolts.Task;
import okio.BufferedSink;
import okio.Okio;

public class ApiClient {
    private static OkHttpClient client = new OkHttpClient();

    public static Task<ApiResponse> callInBackground(final ApiRequest request) {
        return Task.callInBackground(new Callable<ApiResponse>() {
            @Override
            public ApiResponse call() throws Exception {
                return ApiClient.call(request);
            }
        });
    }

    public static ApiResponse call(ApiRequest request) throws ApiError {
        try {
            Response response = client.newCall(request.getRequest()).execute();
            if (response.isSuccessful()) {
                return new ApiResponse(response);
            } else {
                ApiResponse apiResponse = new ApiResponse(response);
                String body = apiResponse.getBody();
                if (body != null) {

                }
                throw new ApiError(response); /* Throw an error so caller can handle it */
            }
        } catch (IOException e) {
            throw new ApiError(e);
        }
    }

    public static Task<ApiResponse> callInBackgroundDownloadVideo(final ApiRequest request, final Context context, final String url) {
        return Task.callInBackground(new Callable<ApiResponse>() {
            @Override
            public ApiResponse call() throws Exception {
                return ApiClient.callDownloadVideo(request, context, url);
            }
        });
    }

    public static ApiResponse callDownloadVideo(ApiRequest request, Context context, String url) throws ApiError {
        try {
            Response response = client.newCall(request.getDownloadS3Request(url)).execute();
            if (response.isSuccessful()) {

                File downloadedFile = new File(context.getCacheDir(), (new Date().getTime()) + ".mp4");

                BufferedSink sink = Okio.buffer(Okio.sink(downloadedFile));
                sink.writeAll(response.body().source());
                sink.close();

                ApiResponse apiResponse = new ApiResponse(response);
                apiResponse.setBody(downloadedFile.getPath());
                return apiResponse;

            } else {
                ApiResponse apiResponse = new ApiResponse(response);
                String body = apiResponse.getBody();
                if (body != null) {

                }
                throw new ApiError(response); /* Throw an error so caller can handle it */
            }
        } catch (IOException e) {
            throw new ApiError(e);
        }
    }

    public static <T extends BaseModel> Task<T> callInBackground(final ApiRequest request, final Class<T> model) {
        return Task.callInBackground(new Callable<T>() {
            @Override
            public T call() throws Exception {
                return ApiClient.call(request, model);
            }
        });
    }

    public static <T extends BaseModel> T call(final ApiRequest request, final Class<T> model) throws ApiError {
        try {
            ApiResponse apiResponse = ApiClient.call(request);
            T result = new ObjectMapper().readValue(apiResponse.getBody(), model);
            result.save();
            return result;
        } catch (IOException e) {
            throw new ApiError(e);
        }
    }

    public static <T extends BaseModel> Task<List<T>> callListInBackground(final ApiRequest request, final Class<T> model) {
        return Task.callInBackground(new Callable<List<T>>() {
            @Override
            public List<T> call() throws Exception {
                return ApiClient.callList(request, model);
            }
        });
    }

    public static <T extends BaseModel> List<T> callList(final ApiRequest request, final Class<T> model) throws ApiError {
        try {
            ApiResponse apiResponse = ApiClient.call(request);
            List<T> result = new ObjectMapper().readValue(
                    apiResponse.getBody(),
                    TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, model)
            );
            for (int i = 0; i < result.size(); i++) {
                result.get(i).save();
            }
            return result;
        } catch (IOException e) {
            throw new ApiError(e);
        }
    }

}