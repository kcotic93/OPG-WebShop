package com.example.kristijan.opg_webshop.Remote;

import com.example.kristijan.opg_webshop.Model.DataMessage;
import com.example.kristijan.opg_webshop.Model.MyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers
            (
                    {
                            "Content-Type:application/json",
                            "Authorization:key=AAAADPN5kYw:APA91bG95VcgetcD-wtF_ZSx7pQxMSyl1y6fJpW5ciKw9IUu35JtuwlQJQTAE6MXrRS4zXAfU9pfoCtlUCZd_xXzvREKYgYPlYFSBs9tTdn2ITrK4Y5T9rq7caMKa6Vqjs0gezYcZnlP"
                    }
            )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body DataMessage body);
}
