package com.example.kristijan.opg_webshop.Common;

import com.example.kristijan.opg_webshop.Remote.APIService;
import com.example.kristijan.opg_webshop.Remote.RetrofitClient;

public class Common {

    private static final String BASE_URL="https://fcm.googleapis.com";

    public static String getBaseUrl() {
        return BASE_URL;
    }
    public static APIService getFCMService()
    {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
