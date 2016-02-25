package com.dounets.vchat.net.helper;

import com.dounets.vchat.net.api.ApiClient;
import com.dounets.vchat.net.api.ApiRequest;
import com.dounets.vchat.net.api.ApiResponse;

import java.util.HashMap;

import bolts.Task;

public class ApiHelper extends BaseHelper {

    public static final Task<ApiResponse> doRegister(String name, String deviceId) {
        HashMap<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("token", deviceId);
        ApiRequest request = new ApiRequest(ApiRequest.Method.POST, getPrefixUrl() + "register", params);
        return ApiClient.callInBackground(request);
    }

    public static final Task<ApiResponse> doGetListUsers(String exceptId) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", exceptId);
        ApiRequest request = new ApiRequest(ApiRequest.Method.POST, getPrefixUrl() + "user_list", params);
        return ApiClient.callInBackground(request);
    }

}
