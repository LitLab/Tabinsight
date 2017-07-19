package com.lumen.usage.satistics;

import android.app.Application;

import com.bugfender.sdk.Bugfender;
import com.lumen.database.LocalRepo;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class LumenApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

        LocalRepo.init(this);

        // if there is need, activate it to store the logs in the cloud

        // used a service that uploads the logs to the cloud
//        Bugfender.init(this, "1Ttq5KcodJ9hn7uCUg5Nin1YCOynEqL0", BuildConfig.DEBUG);
//        Bugfender.enableLogcatLogging();
    }
}
