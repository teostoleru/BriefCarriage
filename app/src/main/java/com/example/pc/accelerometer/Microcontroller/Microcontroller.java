package com.example.pc.accelerometer.Microcontroller;

import com.squareup.okhttp.ResponseBody;

import retrofit.client.Response;
import retrofit.http.*;
import retrofit.*;

public interface Microcontroller {
    @FormUrlEncoded
    @POST("/v1/devices/2d0047001047343339383037/Stop?access_token=d2e16d62d96dc77cde4e77ce31950e9a6650541d")
    public void sendCommand (
            @Field("arg") String arg,
            Callback<Response> callback
    );
}
