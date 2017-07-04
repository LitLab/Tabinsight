package com.lumen.usage.satistics;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.lumen.adapter.AppsAdapter;
import com.lumen.model.App;
import com.lumen.rest.ObservableCron;
import com.lumen.usage.satistics.databinding.ActivityAppsBinding;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Apps screen
 */

public class AppsActivity extends AppCompatActivity {

    private ActivityAppsBinding mBinding;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_apps);

        RecyclerView appsList = mBinding.apps;
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        appsList.setLayoutManager(layoutManager);

        String imageUrl = "https://lh3.googleusercontent.com/s08dlQpwZppwLbrf2lA7HRlX780n4FXLoZ2aDbrnWI42adkoaLkJ1hV18U1lITyUl_M=w300-rw";
        String name = "Animal Sounds";
        String packageName = "com.premiumsoftware.animalsoundsandphotos";

        App app = new App(name, packageName, imageUrl);
        List<App> apps = new ArrayList<>();
//        List<App> apps = Arrays.asList(app, app, app, app);

        renderApps(apps);

        getApps();
    }

    private void showLoading(boolean show) {
        int contentVisibiliy = show ? View.GONE : View.VISIBLE;
        int loadingVisibility = show ? View.VISIBLE : View.GONE;

        mBinding.content.setVisibility(contentVisibiliy);
        mBinding.loading.setVisibility(loadingVisibility);
    }

    private void renderApps(List<App> apps) {
        AppsAdapter adapter = new AppsAdapter(apps, new AppsAdapter.ItemClickListener() {
            @Override
            public void onItemClick(App app) {
                onAppClick(app);
            }
        });

        mBinding.apps.setAdapter(adapter);
    }

    private void getApps() {
        showLoading(true);

        ObservableCron.getApps(this)
                .subscribe(new Action1<List<App>>() {
                    @Override
                    public void call(List<App> apps) {
                        showLoading(false);

                        renderApps(apps);
                    }

                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        showLoading(false);

                        Toast.makeText(AppsActivity.this, "There's been an error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();

        showLoading(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, DashboardActivity.class);
                startActivity(intent);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onAppClick(App app) {
        String packageName = app.packageName;
        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);

        if (intent == null) {
            String url = "https://play.google.com/store/apps/details?id=" + packageName;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);

            return;
        }

        startActivity(intent);
    }
}
