package com.dounets.vchat.net.helper;

import android.net.Uri;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;

import bolts.Task;


public class S3Uploader extends BaseHelper {

    public static Task uploadVideoFileToS3(String filePath) throws UnsupportedEncodingException {

        /*{ "accessKeyId": "AKIAJ3IIQ6LBN5ELXH3Q", "secretAccessKey": "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", "region": "ap-southeast-1" }
        bucketname: scsklvchat*/

        String accessKeyId = "AKIAJ3IIQ6LBN5ELXH3Q";
        String secretAccessKey = URLEncoder.encode("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", "UTF-8");
        String fileName = new Date().getTime() + ".mp4";
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 1);
        long secondsSinceEpoch = cal.getTimeInMillis() / 1000L;
        final String ENDPOINT = "https://scsklvchat.s3-ap-southeast-1.amazonaws.com/" + fileName + "?AWSAccessKeyId=" + accessKeyId + "&Content-Type=" + URLEncoder.encode("video/mp4", "UTF-8") +"&Signature=" + secretAccessKey + "&x-amz-acl=public-read&Expires=" + secondsSinceEpoch;

        final File file = new File(filePath);
        return Task.callInBackground(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Request request = new Request.Builder()
                        .url(ENDPOINT)
                        .put(RequestBody.create(MediaType.parse("video/mp4"), file))
                        .build();
                new OkHttpClient().newCall(request).execute();
                return null;
            }
        });
    }
}

