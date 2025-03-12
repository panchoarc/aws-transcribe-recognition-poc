package com.panchodev.asr.util;

import java.util.Map;

public class ResponseBuilder {

    private ResponseBuilder() {
    }

    public static <T> ApiResponse<T> success(String message, T data) {

        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setStatus(ApiResponse.Status.SUCCESS);
        apiResponse.setMessage(message);
        apiResponse.setData(data);
        return apiResponse;
    }

    public static <T> ApiResponse<T> error(String message, Map<String, String> errors) {

        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setStatus(ApiResponse.Status.ERROR);
        apiResponse.setMessage(message);
        apiResponse.setErrors(errors);
        return apiResponse;
    }

}