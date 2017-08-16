package com.lumen.setup;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.lumen.database.LocalRepo;
import com.lumen.mapper.Mapper;
import com.lumen.rest.RemoteRepo;
import com.lumen.usage.satistics.AppsActivity;
import com.lumen.usage.satistics.LoginActivity;
import com.lumen.usage.satistics.R;
import com.lumen.usage.satistics.databinding.ActivityRegistrationBinding;
import com.lumen.util.DateUtils;
import com.lumen.util.TextUtils2;

import java.util.Calendar;
import java.util.Date;

import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Registration screen
 */

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = RegistrationActivity.class.getSimpleName();

    ActivityRegistrationBinding mBinding;

    private boolean mFirstName;
    public boolean mLastName;
    public boolean mChildFirst;
    public boolean mChildLast;
    public boolean mChildBirth;
    public boolean mZip;
    public boolean mPhone;

    private Date mDate = new Date();
    private DateListener mDateListener = new DateListener();
    private long mTime;
    private DateClickListener mListener = new DateClickListener();
    private Subscription mSubscription;
    private boolean mAgree;
    private boolean mSchoolName;
    private boolean mSchoolDistrict;
    private boolean mTeacherName;
    private String mTimeString;


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

        mBinding.teacherName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTeacherName = !TextUtils.isEmpty(s);
                checkButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mBinding.zip.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mZip = !TextUtils.isEmpty(s);
                checkButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mBinding.phone.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPhone = !TextUtils.isEmpty(s);
                checkButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mBinding.childFirstName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mChildFirst = !TextUtils.isEmpty(s);
                checkButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mBinding.childLastName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mChildLast = !TextUtils.isEmpty(s);
                checkButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mBinding.birthDate.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mChildBirth = !TextUtils.isEmpty(s);
                checkButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mBinding.agree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mAgree = isChecked;
                checkButtonState();
            }
        });

        mBinding.school.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSchoolName = !TextUtils.isEmpty(s);
                checkButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mBinding.schoolDistrict.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSchoolDistrict = !TextUtils.isEmpty(s);
                checkButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mBinding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            }
        });

        mBinding.birthDate.setOnClickListener(mListener);

        mBinding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formValid()) {
                    showLoading(true);

                    String firstName = TextUtils2.getString(mBinding.firstName);
                    String lastName = TextUtils2.getString(mBinding.lastName);
                    String zip = TextUtils2.getString(mBinding.zip);
                    String phone = TextUtils2.getString(mBinding.phone);
                    String childFirst = TextUtils2.getString(mBinding.childFirstName);
                    String childLast = TextUtils2.getString(mBinding.childLastName);
                    String childBirth = TextUtils2.getString(mBinding.birthDate);
                    String schoolDistrict = TextUtils2.getString(mBinding.schoolDistrict);
                    String schoolName = TextUtils2.getString(mBinding.school);
                    String teacherName = TextUtils2.getString(mBinding.teacherName);
                    String parentEmail = TextUtils2.getString(mBinding.parentEmail);
                    long registerTime =  new Date().getTime() / 1000;


                    mSubscription = RemoteRepo.register(RegistrationActivity.this,
                            Mapper.toRegisterParams(firstName, lastName, zip, phone,
                                    childFirst, childLast, childBirth, parentEmail,
                                    schoolName, schoolDistrict, teacherName, mTimeString, registerTime))
                            .subscribe(new Action0() {
                                @Override
                                public void call() {
                                    showLoading(false);

                                    Log.i(TAG, "registering... SUCCESS");

                                    String id = TextUtils2.getString(mBinding.phone);
                                    LocalRepo.saveId(id);

                                    Intent intent = new Intent(RegistrationActivity.this, AppsActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);

                                }

                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    showLoading(false);

                                    Crashlytics.logException(throwable);

                                    Log.i(TAG, "registering... ERROR: " + throwable.getMessage());
                                    Toast.makeText(RegistrationActivity.this, "error " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
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

        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    private void showLoading(boolean show) {
        int loading = show ? View.VISIBLE : View.GONE;
        int content = show ? View.GONE : View.VISIBLE;

        mBinding.content.setVisibility(content);
        mBinding.loading.setVisibility(loading);
    }


    private void checkButtonState() {
        boolean condition = formValid();

        int bgColor = condition ? R.color.colorPrimary : R.color.grey;
        int textColor = condition ? android.R.color.white : android.R.color.black;

        mBinding.register.setBackgroundColor(ContextCompat.getColor(this, bgColor));
        mBinding.register.setTextColor(ContextCompat.getColor(this, textColor));
    }

    private boolean formValid() {

        return mFirstName && mLastName && mZip && mPhone &&
                mChildFirst && mChildLast && mChildBirth && mAgree &&
                mSchoolName && mSchoolDistrict && mTeacherName;
    }


    private class DateClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Calendar date = Calendar.getInstance();
            date.setTime(mDate);

            DatePickerDialog dialog = new DatePickerDialog(RegistrationActivity.this,
                    mDateListener,
                    date.get(Calendar.YEAR),
                    date.get(Calendar.MONTH),
                    date.get(Calendar.DAY_OF_MONTH));

            dialog.show();

        }
    }


    private class DateListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar newDate = Calendar.getInstance();
            newDate.setTime(mDate);

            newDate.set(Calendar.YEAR, year);
            newDate.set(Calendar.MONTH, monthOfYear);
            newDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // swaps the modified date as the current one.
            mDate = newDate.getTime();
            mTime = mDate.getTime();
            mTimeString = String.valueOf(mTime);
            mBinding.birthDate.setText(DateUtils.DATE_ONLY.format(mDate));
        }
    }
}
