
package com.dounets.vchat.net.api;

import com.dounets.vchat.data.Meta;
import com.dounets.vchat.data.model.Contact;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


public class ApiRequest {
    public enum Method {
        GET,
        POST,
        PUT,
        DELETE
    }

    private static StringBuilder stringBuilder = new StringBuilder();

    Method method;
    String path;
    HashMap<String, String> params;

    public ApiRequest(Method method, String path) {
        this(method, path, null);
    }

    public ApiRequest(Method method, String path, HashMap<String, String> params) {
        this.method = method;
        this.path = path;
        this.params = params;
    }

    public Request getRequest() {
        switch (method) {
            case GET:
                return buildGetRequest();
            case POST:
                return buildPostRequest();
            case PUT:
                return buildPutRequest();
            case DELETE:
                return buildDeleteRequest();
            default:
                return null;
        }
    }

    private Request buildGetRequest() {
        return new Request.Builder()
                .headers(buildRequestHeaders())
                .url(buildUrl())
                .build();
    }

    private Request buildPostRequest() {
        return new Request.Builder()
                .headers(buildRequestHeaders())
                .url(buildUrl())
                .post(buildRequestBody())
                .build();
    }

    private Request buildPutRequest() {
        return new Request.Builder()
                .headers(buildRequestHeaders())
                .url(buildUrl())
                .put(buildRequestBody())
                .build();
    }

    private Request buildDeleteRequest() {
        return new Request.Builder()
                .headers(buildRequestHeaders())
                .url(buildUrl())
                .delete()
                .build();
    }

    private Headers buildRequestHeaders() {
        String acceptLanguage = Locale.getDefault().toString().replace("_", "-");
        String acceptEncoding = "gzip";
        String timeZone = TimeZone.getDefault().getID();

        Headers.Builder builder = new Headers.Builder();
        if (acceptLanguage != null) {
            builder.add("Accept-Language", acceptLanguage);
        }
        if (acceptEncoding != null) {
            builder.add("Accept-Encoding", acceptEncoding);
        }
        if (timeZone != null) {
            builder.add("Time-Zone", timeZone);
        }
        if (Contact.currentUser() != null && Contact.currentUser().getApi_token() != null) {
            builder.add("Api-Token", Contact.currentUser().getApi_token());
        }
        return builder.build();
    }

    private String buildUrl() {
        try {
            synchronized (stringBuilder) {
                stringBuilder.setLength(0);
                stringBuilder.append(Meta.getInstance().getApiScheme());
                stringBuilder.append("://");
                stringBuilder.append(Meta.getInstance().getApiDomain());
                stringBuilder.append(path);
                if (isParamAppendable()) {
                    /**
                     * Get/Delete parameter
                     */
                    stringBuilder.append("?");
                    for (Map.Entry<String, String> e : params.entrySet()) {
                        stringBuilder.append(URLEncoder.encode(e.getKey(), "utf-8"));
                        stringBuilder.append("=");
                        stringBuilder.append(URLEncoder.encode(e.getValue(), "utf-8"));
                        stringBuilder.append("&");
                    }
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                }
            }
            return stringBuilder.toString();
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }

    private boolean isParamAppendable() {
        return (method == Method.GET || method == Method.DELETE) && params != null && params.size() > 0;
    }

    private RequestBody buildRequestBody() {
        FormEncodingBuilder fe = new FormEncodingBuilder();
        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                fe.add(param.getKey(), param.getValue());
            }
        }
        return fe.build();
    }
}
