/*
* Copyright 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.kidappolis.usage.satistics;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.kidappolis.util.DialogCallback;
import com.kidappolis.util.LumenApplicationContext;

/**
 * Launcher Activity to show all the device app usage statistics
 */
public class AppUsageStatisticsActivity extends AppCompatActivity implements DialogCallback {

    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LumenApplicationContext.setBroadcastRecieverClient(LumenApplicationContext.BroadcastRecieverClients.UI_ACTIVITY);
        startDialog();

        setContentView(R.layout.activity_app_usage_statistics);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, AppUsageStatisticsFragment.newInstance())
                    .commit();
        }

        LumenApplicationContext.setBroadcastRecieverClient(LumenApplicationContext.BroadcastRecieverClients.SERVICE);
    }

    @Override
    public void startDialog() {
        loadingDialog = new AlertDialog.Builder(AppUsageStatisticsActivity.this)
                .setMessage(R.string.loading_data_message)
                .setCancelable(false)
                .show();
    }

    @Override
    public void finishDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.hide();
            loadingDialog.dismiss();
            loadingDialog.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SettingsActivity.class));
        finish();
    }
}