package com.example.myapplication.ui.api;

public class ApiUtils {

    private ApiUtils() {}
    //                                    "http://192.168.0.76:8080/"
    public static final String BASE_URL = "https://apirest-tesis.rj.r.appspot.com/";

    public static APIService getAPIService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
