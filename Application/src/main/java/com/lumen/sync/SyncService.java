package com.lumen.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.lumen.model.App;
import com.lumen.rest.RemoteRepo;

import java.util.List;

import io.realm.Realm;
import rx.functions.Action1;

/**
 * Sync apps service
 */

public class SyncService extends Service {

    private static final String TAG = SyncService.class.getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {

        RemoteRepo.getApps(this)
                .subscribe(new Action1<List<App>>() {
                    @Override
                    public void call(final List<App> apps) {
                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.copyToRealmOrUpdate(apps);
                            }

                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                Log.i(TAG, "syncing apps... SUCCESS");
                                stopSelf();
                            }

                        }, new Realm.Transaction.OnError() {
                            @Override
                            public void onError(Throwable error) {
                                Log.i(TAG, "syncing apps... ERROR " + error.getMessage());
                                stopSelf();
                            }
                        });
                    }

                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                        if (isAppsEmpty()) {
                            Toast.makeText(SyncService.this, "There's been an error", Toast.LENGTH_SHORT).show();
                        }

                        stopSelf();
                    }
                });

        return START_NOT_STICKY;
    }

    private boolean isAppsEmpty() {
        return Realm.getDefaultInstance().where(App.class).findFirst() == null;
    }
}
