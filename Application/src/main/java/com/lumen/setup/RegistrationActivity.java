package com.lumen.setup;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.lumen.database.LocalRepo;
import com.lumen.usage.satistics.AppsActivity;
import com.lumen.usage.satistics.R;
import com.lumen.usage.satistics.databinding.ActivityRegistrationBinding;

/**
 * Registration screen
 */

public class RegistrationActivity extends AppCompatActivity {

    ActivityRegistrationBinding mBinding;

    private boolean mFirstName;
    public boolean mLastName;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_registration);

        mBinding.firstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mFirstName = !TextUtils.isEmpty(s);
                checkButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mBinding.lastName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mLastName = !TextUtils.isEmpty(s);
                checkButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mBinding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formValid()) {
                    String id = mBinding.firstName.getText().toString() + mBinding.lastName.getText().toString();
                    LocalRepo.saveId(id);

                    Intent intent = new Intent(RegistrationActivity.this, AppsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

    }


    private void checkButtonState() {
        boolean condition = formValid();

        int bgColor = condition ? R.color.colorPrimary : R.color.grey;
        int textColor = condition ? android.R.color.white : android.R.color.black;

        mBinding.register.setBackgroundColor(ContextCompat.getColor(this, bgColor));
        mBinding.register.setTextColor(ContextCompat.getColor(this, textColor));
    }

    private boolean formValid() {

        return mFirstName && mLastName;
    }
}
