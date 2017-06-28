package com.lumen.setup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.lumen.database.LocalRepo;
import com.lumen.usage.satistics.AppsActivity;
import com.lumen.usage.satistics.R;
import com.lumen.util.Environments;
import com.lumen.util.Permissions;


public class SplashScreen extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1000;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_screen);

        ActivityCompat.requestPermissions(this, Environments.APPLICATION_PERMISSIONS, PERMISSION_REQUEST_CODE);
    }

    private void splashScreenLogic() {
        if (!Permissions.isPermissionGranted(this)) {
            startActivity(new Intent(this, PermissionsActivity.class));
            return;
        }

        if (LocalRepo.isIdExist()) {
            startActivity(new Intent(this, AppsActivity.class));

        } else {
            startActivity(new Intent(this, RegistrationActivity.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (Environments.arePermitted(this, permissions)) {
                splashScreenLogic();

            } else {
                Toast.makeText(this, "need to grant permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
