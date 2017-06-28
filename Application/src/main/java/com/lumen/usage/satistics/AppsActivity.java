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
import android.widget.Toast;

import com.lumen.adapter.AppsAdapter;
import com.lumen.model.App;
import com.lumen.usage.satistics.databinding.ActivityAppsBinding;

import java.util.Arrays;
import java.util.List;

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

        String imageUrl = "https://lh3.googleusercontent.com/kaK6tiElkhevaBKXjvgvtTAWEiUCrXwW1ktqYhET0KJV26w9WpFRI5L_Uvpb7iQivQ=w300-rw";
        String name = "Peppa Pig: Activity Maker";
        String packageName = "air.com.peppapig.activitymaker";

        App app = new App(name, packageName, imageUrl);
        List<App> apps = Arrays.asList(app, app, app, app);

        AppsAdapter adapter = new AppsAdapter(apps, new AppsAdapter.ItemClickListener() {
            @Override
            public void onItemClick(App app) {
                onAppClick(app);
            }
        });

        appsList.setAdapter(adapter);
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
