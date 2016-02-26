package com.dounets.vchat.net.helper;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;

import bolts.Task;


public class S3Uploader extends BaseHelper {

    public static Task<String> uploadFileToS3InBackground(final String filePath) {

        /*{ "accessKeyId": "AKIAJ3IIQ6LBN5ELXH3Q", "secretAccessKey": "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", "region": "ap-southeast-1" }
        bucketname: scsklvchat*/

        final Task<String>.TaskCompletionSource uploadTask = Task.create();

        AmazonS3Client s3Client = new AmazonS3Client(
                new BasicAWSCredentials("AKIAJ3IIQ6LBN5ELXH3Q", "Kc3Dt6UWyjMvYCZyYAQd+R4l9muhlPlQIC/wwoLl"));

        File file = new File(filePath);
        String s3Key = file.getPath();

        return uploadTask.getTask();
    }
}

