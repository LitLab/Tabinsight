package com.kidappolis.usage.satistics;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kidappolis.database.LocalRepo;
import com.kidappolis.mapper.Mapper;
import com.kidappolis.model.LoginResponse;
import com.kidappolis.rest.RemoteRepo;
import com.kidappolis.usage.satistics.databinding.ActivityLoginBinding;
import com.kidappolis.util.TextUtils2;

import rx.functions.Action1;

/**
 * Login screen
 */

public class LoginActivity extends BackArrowActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private ActivityLoginBinding mBinding;

    private boolean mLogin;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        mBinding.userId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mLogin = !TextUtils.isEmpty(s);
                checkButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formValid()) {
                    final String userid = TextUtils2.getString(mBinding.userId);

                    showLoading(true);

                    RemoteRepo.login(LoginActivity.this, Mapper.toLoginParams(userid))
                            .subscribe(new Action1<LoginResponse>() {
                                @Override
                                public void call(LoginResponse response) {
                                    showLoading(false);

                                    if (response.result.rows > 0) {
                                        Log.i(TAG, "success");

                                        LocalRepo.saveId(userid);

                                        Intent intent = new Intent(LoginActivity.this, AppsActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                    } else {
                                        Toast.makeText(LoginActivity.this, "Phone/Id is incorrect", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    showLoading(false);

                                    Log.e(TAG, "logging in... ERROR " + throwable.getMessage());
                                }
                            });
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        showLoading(false);
    }

    private void showLoading(boolean show) {
        if (show) {
            mBinding.login.setVisibility(View.INVISIBLE);
            mBinding.idContainer.setVisibility(View.INVISIBLE);
            mBinding.logo.setVisibility(View.INVISIBLE);
            mBinding.loading.setVisibility(View.VISIBLE);

        } else {
            mBinding.login.setVisibility(View.VISIBLE);
            mBinding.idContainer.setVisibility(View.VISIBLE);
            mBinding.logo.setVisibility(View.VISIBLE);
            mBinding.loading.setVisibility(View.GONE);
        }
    }

    private void checkButtonState() {
        boolean condition = formValid();

        int bgColor = condition ? R.color.colorPrimary : R.color.grey;
        int textColor = condition ? android.R.color.white : android.R.color.black;

        mBinding.login.setBackgroundColor(ContextCompat.getColor(this, bgColor));
        mBinding.login.setTextColor(ContextCompat.getColor(this, textColor));
    }

    private boolean formValid() {
        return mLogin;
    }
}
