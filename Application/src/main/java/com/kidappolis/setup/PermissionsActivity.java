package com.kidappolis.setup;

import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kidappolis.database.LocalRepo;
import com.kidappolis.usage.satistics.SettingsActivity;
import com.kidappolis.usage.satistics.R;
import com.kidappolis.util.Permissions;

/**
 * First screen to give permission to lumen
 */

public class PermissionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_permission);

        Button permission = findViewById(R.id.enable_permission);
        permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));

                } catch (ActivityNotFoundException e) {
                    Toast.makeText(PermissionsActivity.this, "error in settings permission", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setPermissionListener();
    }

    private void setPermissionListener() {
        final AppOpsManager appOpsManager = ((AppOpsManager) getSystemService(Context.APP_OPS_SERVICE));
        appOpsManager.startWatchingMode(AppOpsManager.OPSTR_GET_USAGE_STATS,
                getApplicationContext().getPackageName(), new AppOpsManager.OnOpChangedListener() {
                    @Override
                    public void onOpChanged(String op, String packageName) {
                        if (!Permissions.isPermissionGranted(PermissionsActivity.this)) {
                            return;
                        }

                        appOpsManager.stopWatchingMode(this);

                        if (LocalRepo.isIdExist()) {
                            Intent intent = new Intent(PermissionsActivity.this, SettingsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        } else {
                            Intent intent = new Intent(PermissionsActivity.this, RegistrationActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }

                    }
                });
    }
}
