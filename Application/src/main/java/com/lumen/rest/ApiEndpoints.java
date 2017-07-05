package com.lumen.rest;

import com.lumen.database.AppUseInfo;
import com.lumen.database.DeviceUseInfo;
import com.lumen.model.App;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Completable;
import rx.Observable;

/**
 * Api service
 */
public interface ApiEndpoints {

    @POST("/publish/log")// path after "/"
    Observable<Object> syncAppDataToApi(@Body ArrayList<AppUseInfo> appUseInfos);

    @GET("/pingtest")
    Call<Object> pingServer();

    @POST("/publish/uptime")
    Observable<Object> syncDeviceDataToApi(@Body List<DeviceUseInfo> deviceUseInfos);

    /**
     * get al white listed applications information (name, package ...)
     * @return {@link Observable} contains all the apps
     */
    @GET("/apps")
    Observable<List<App>> getApps();

    /**
     * register new user
     * @param params user information
     * @return {@link Completable} of the response status
     */
    @POST("/publish/user")
    @FormUrlEncoded
    Completable register(@FieldMap Map<String, Object> params);
}