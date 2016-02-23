package com.dounets.vchat.net.api;

import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class ApiError extends Exception {
    private static byte[] buffer = new byte[1024];

    int status = 0;
    String apiMessage;
    Exception exception;

    ApiError(Response response) {
        super("ApiError");
        try {
            this.status = response.code();
            String body = getUnzippedBody(response);
            JSONObject json = new JSONObject(body);
            if (!json.isNull("message")) {
                apiMessage = json.getString("message");
            }
        } catch (Exception e) {
            this.exception = e;
        }
    }

    private String getUnzippedBody(Response response) throws IOException {
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try {
            is = new BufferedInputStream(new GZIPInputStream(response.body().byteStream()));
            synchronized (buffer) {
                int numRead = 0;
                baos = new ByteArrayOutputStream();
                while ((numRead = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, numRead);
                }
                return new String(baos.toByteArray());
            }
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (Exception e) {
            }
        }
    }


    public ApiError(Exception exception) {
        super("ApiError");
        this.exception = exception;
    }

    public String getApiMessage() {
        return apiMessage;
    }
}
