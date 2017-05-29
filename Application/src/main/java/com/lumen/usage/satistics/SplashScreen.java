package com.lumen.usage.satistics;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lumen.constants.Keys;
import com.lumen.util.Environments;


public class SplashScreen extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1000;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_screen);

        ActivityCompat.requestPermissions(this, Environments.APPLICATION_PERMISSIONS, PERMISSION_REQUEST_CODE);
    }

    private void splashScreenLogic() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.contains(Keys.DEVICE_ID)) {
            startActivity(new Intent(this, DashboardActivity.class));

        } else {
            ViewGroup container = (ViewGroup) findViewById(R.id.content);
            container.setVisibility(View.VISIBLE);

            Button button = (Button)findViewById(R.id.ok);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText text = (EditText)findViewById(R.id.id);

                    String id = text.getText().toString();

                    if (TextUtils.isEmpty(id)) {
                        Toast.makeText(text.getContext(), "please insert id", Toast.LENGTH_SHORT).show();

                    } else {
                        preferences.edit().putString(Keys.DEVICE_ID, id).apply();
                        startActivity(new Intent(SplashScreen.this, DashboardActivity.class));
                    }
                }
            });
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
