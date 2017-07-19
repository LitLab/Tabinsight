package com.lumen.usage.satistics;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lumen.adapter.AppsAdapter;
import com.lumen.constants.Keys;
import com.lumen.model.App;
import com.lumen.usage.satistics.databinding.ActivityAppsBinding;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Apps screen
 */

public class AppsFragment extends Fragment {

    ActivityAppsBinding mBinding;


    public static AppsFragment newInstance(String category) {
        Bundle bundle = new Bundle();
        bundle.putString(Keys.CATEGORY, category);

        AppsFragment fragment = new AppsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.activity_apps, container, false);

        List<App> apps = new ArrayList<>();
        renderApps(apps);

        getApps();


        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RecyclerView appsList = mBinding.apps;
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        appsList.setLayoutManager(layoutManager);
    }

    @Override
    public void onPause() {
        super.onPause();

        showLoading(false);
    }

    private void showLoading(boolean show) {
        int contentVisibility = show ? View.GONE : View.VISIBLE;
        int loadingVisibility = show ? View.VISIBLE : View.GONE;

        mBinding.content.setVisibility(contentVisibility);
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

        String category = getArguments().getString(Keys.CATEGORY);

        Realm realm = Realm.getDefaultInstance();
        RealmResults<App> results = realm.where(App.class).equalTo("category", category).findAllAsync();
        results.addChangeListener(new RealmChangeListener<RealmResults<App>>() {
            @Override
            public void onChange(RealmResults<App> apps) {
                showLoading(false);

                renderApps(apps);
            }
        });

    }


    private void onAppClick(App app) {
        String packageName = app.packageName;
        Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(packageName);

        if (intent == null) {
            String url = "https://play.google.com/store/apps/details?id=" + packageName;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);

            return;
        }

        startActivity(intent);
    }

}
