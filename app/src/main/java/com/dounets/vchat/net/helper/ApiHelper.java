package com.dounets.vchat.net.helper;

import com.dounets.vchat.net.api.ApiClient;
import com.dounets.vchat.net.api.ApiRequest;
import com.dounets.vchat.net.api.ApiResponse;

import java.util.HashMap;

import bolts.Task;

public class ApiHelper extends BaseHelper {

    public static final Task<ApiResponse> doRegister(final String name) {
        HashMap<String, String> params = new HashMap<>();
        params.put("name", name);
        ApiRequest request = new ApiRequest(ApiRequest.Method.POST, getPrefixUrl() + "register", params);
        return ApiClient.callInBackground(request);
    }

}
