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
import android.view.View;
import android.widget.DatePicker;

import com.lumen.database.LocalRepo;
import com.lumen.usage.satistics.AppsActivity;
import com.lumen.usage.satistics.R;
import com.lumen.usage.satistics.databinding.ActivityRegistrationBinding;
import com.lumen.util.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Registration screen
 */

public class RegistrationActivity extends AppCompatActivity {

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
    private DateClickListener mListener = new DateClickListener();


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

        mBinding.birthDate.setOnClickListener(mListener);

        mBinding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formValid()) {
                    String id = mBinding.phone.getText().toString();
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

        return mFirstName && mLastName && mZip && mPhone & mChildFirst && mChildLast && mChildBirth;
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

            dialog.getDatePicker().setMinDate(new Date().getTime());
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
            mBinding.birthDate.setText(DateUtils.DATE_ONLY.format(mDate));
        }
    }
}
