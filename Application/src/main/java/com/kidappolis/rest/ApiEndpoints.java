package com.kidappolis.rest;

import com.kidappolis.database.AppUseInfo;
import com.kidappolis.database.DeviceUseInfo;
import com.kidappolis.model.App;
import com.kidappolis.model.LoginResponse;

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
 * Api calls
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
    @POST("/users")
    @FormUrlEncoded
    Completable register(@FieldMap Map<String, Object> params);


    @POST("/users/login")
    @FormUrlEncoded
    Observable<LoginResponse> login(@FieldMap Map<String, Object> params);
}