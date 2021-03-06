/*
* Copyright (C) 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.kidappolis.usage.satistics;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * List that used to show device usage fo teh device
 */
public class UsageListAdapter extends RecyclerView.Adapter<UsageListAdapter.ViewHolder> {

    List<CustomUsageStats> mCustomUsageStatsList = new ArrayList<>();
    private DateFormat mDateFormat = new SimpleDateFormat("MM/dd/yy HH:mm", Locale.US);

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mPackageName;
        public TextView mLastTimeUsed;
        public ImageView mAppIcon;

        public ViewHolder(View v) {
            super(v);
            mPackageName = v.findViewById(R.id.textview_package_name);
            mLastTimeUsed = v.findViewById(R.id.textview_last_time_used);
            mAppIcon = v.findViewById(R.id.app_icon);
        }
    }

    public UsageListAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.usage_row, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.mPackageName.setText(
                mCustomUsageStatsList.get(position).usageStats.getPackageName());
        long lastTimeUsed = mCustomUsageStatsList.get(position).usageStats.getLastTimeUsed();
        String lastUsed = mDateFormat.format(new Date(lastTimeUsed)) + " "
                + mCustomUsageStatsList.get(position).usageStats.getTotalTimeInForeground();
        viewHolder.mLastTimeUsed.setText(lastUsed);
        viewHolder.mAppIcon.setImageDrawable(mCustomUsageStatsList.get(position).appIcon);
    }

    @Override
    public int getItemCount() {
        return mCustomUsageStatsList.size();
    }

    public void setCustomUsageStatsList(List<CustomUsageStats> customUsageStats) {
        mCustomUsageStatsList = customUsageStats;
        notifyDataSetChanged();
    }
}