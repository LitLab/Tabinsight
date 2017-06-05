package com.lumen.rest;

import com.lumen.database.AppUseInfo;
import com.lumen.database.DeviceUseInfo;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;
import rx.Single;

/**
 * Created by Menil on 7/21/2016.
 */
public interface ApiEndpoints {

    @POST("/publish/log")// path after "/"
    Observable<Object> syncAppDataToApi(@Body ArrayList<AppUseInfo> appUseInfos);

    @GET("/pingtest")
    Call<Object> pingServer();

    @POST("/publish/uptime")
    Observable<Object> syncDeviceDataToApi(@Body List<DeviceUseInfo> deviceUseInfos);
}