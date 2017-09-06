package com.kidappolis.rest;

import android.content.Context;
import android.util.Log;

import com.kidappolis.database.AppUseInfo;
import com.kidappolis.database.DeviceUseInfo;
import com.kidappolis.model.App;
import com.kidappolis.model.LoginResponse;
import com.kidappolis.usage.satistics.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Completable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Observables factory
 */
public final class RemoteRepo {

    public static int PAGE_COUNT = 20;
    public static int DEVICE_PAGE_COUNT = 24;
    private static OkHttpClient.Builder httpClient;

    private static Retrofit.Builder builder =
            new Retrofit.Builder().
                    addCallAdapterFactory(RxJavaCallAdapterFactory.create()).
                    addConverterFactory(GsonConverterFactory.create());

    public static Observable getAppDataObservable(List<AppUseInfo> records, final Context context) {

        ArrayList<ArrayList<AppUseInfo>> data = new ArrayList<>();

        int j = -1;
        ArrayList<AppUseInfo> newList = new ArrayList<>();
        for (int i = 0; i < records.size(); i++) {
            newList.add(records.get(i));
            if (i != 0 && i % PAGE_COUNT == 0 || i == records.size() - 1) {
                j++;
                data.add(j, new ArrayList<>(newList));
                newList.clear();
            }
        }

        return Observable
                .just(data).flatMap(new Func1<ArrayList<ArrayList<AppUseInfo>>, Observable<ArrayList<AppUseInfo>>>() {
                    @Override
                    public Observable<ArrayList<AppUseInfo>> call(ArrayList<ArrayList<AppUseInfo>> arrayLists) {
                        Log.d("RX size", arrayLists.size() + "");
                        return Observable.from(arrayLists);
                    }
                }).flatMap(new Func1<ArrayList<AppUseInfo>, Observable<Object>>() {
                    @Override
                    public Observable<Object> call(ArrayList<AppUseInfo> appUseInfos) {
                        Log.i("sync lumen", "app use info sent to server " + appUseInfos.size());
                        return getService(context).syncAppDataToApi(appUseInfos);
                    }
                });
    }

    public static Observable getDeviceDataObservable(List<DeviceUseInfo> records, final Context context) {

        ArrayList<ArrayList<DeviceUseInfo>> data = new ArrayList<>();
        int j = -1;
        ArrayList<DeviceUseInfo> newList = new ArrayList<>();
        for (int i = 0; i < records.size(); i++) {
            newList.add(records.get(i));
            if (i != 0 && i % DEVICE_PAGE_COUNT == 0 || i == records.size() - 1) {
                j++;
                data.add(j, new ArrayList<>(newList));
                newList.clear();
            }
        }

        return Observable
                .just(data).flatMap(new Func1<ArrayList<ArrayList<DeviceUseInfo>>, Observable<ArrayList<DeviceUseInfo>>>() {
                    @Override
                    public Observable<ArrayList<DeviceUseInfo>> call(ArrayList<ArrayList<DeviceUseInfo>> arrayLists) {
                        Log.d("RX size", arrayLists.size() + "");
                        return Observable.from(arrayLists);
                    }
                }).flatMap(new Func1<ArrayList<DeviceUseInfo>, Observable<Object>>() {
                    @Override
                    public Observable<Object> call(ArrayList<DeviceUseInfo> deviceUseInfos) {
                        Log.i("sync lumen", "device info sent to server size " + deviceUseInfos.size());

                        return getService(context).syncDeviceDataToApi(deviceUseInfos);
                    }
                });
    }

    public static Observable<List<App>> getApps(Context context) {
        return getService(context).getApps()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Completable register(Context context, Map<String, Object> params) {
        return getService(context).register(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<LoginResponse> login(Context context, Map<String, Object> params) {
        return getService(context).login(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public static ApiEndpoints getService(Context context) {
        httpClient = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.readTimeout(30, TimeUnit.SECONDS);
        httpClient.connectTimeout(30, TimeUnit.SECONDS);
        httpClient.addInterceptor(logging);

        Retrofit retrofit = builder
                .baseUrl("http://" + "services.lumen.com:3000/")
                .client(httpClient.build())
                .build();
        return retrofit.create(ApiEndpoints.class);
    }
}
