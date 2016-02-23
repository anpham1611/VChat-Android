
package com.dounets.vchat.net.api;

import com.squareup.okhttp.Response;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;


public class ApiResponse {
    private static byte[] buffer = new byte[1024];

    Map<String, List<String>> headers;
    String body;

    ApiResponse(Response response) {
        headers = response.headers().toMultimap();
        body = getUngzippedBody(response);
    }

    public String getBody() {
        return body;
    }

    private String getUngzippedBody(Response response) {
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
        } catch (IOException e) {
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
        return null;
    }
}
