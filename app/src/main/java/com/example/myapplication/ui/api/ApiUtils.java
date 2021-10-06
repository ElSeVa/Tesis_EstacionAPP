package com.example.myapplication.ui.api;

public class ApiUtils {

    private ApiUtils() {}

    public static final String BASE_URL = "http://192.168.0.76:8080/";

    public static APIService getAPIService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
