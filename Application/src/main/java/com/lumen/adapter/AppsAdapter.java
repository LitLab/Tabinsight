package com.lumen.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lumen.model.App;
import com.lumen.usage.satistics.BR;
import com.lumen.usage.satistics.R;

import java.util.List;

/**
 * Apps list adapter
 */

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.BindingHolder> {

    public interface ItemClickListener {
        void onItemClick(App app);
    }

    List<App> mData;
    ItemClickListener mListener;


    public AppsAdapter(List<App> data, ItemClickListener listener) {
        mData = data;
        mListener = listener;
    }


    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_app, parent, false);

        return new BindingHolder(v);
    }

    @Override
    public void onBindViewHolder(BindingHolder holder, int position) {
        final App app = mData.get(position);

        ViewDataBinding binding = holder.getBinding();
        binding.setVariable(BR.imageUrl, app.imageUrl);
        binding.setVariable(BR.name, app.name);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(app);
            }
        });

        binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    static class BindingHolder extends RecyclerView.ViewHolder {

        private ViewDataBinding binding;

        public BindingHolder(View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);
        }

        public ViewDataBinding getBinding() {
            return binding;
        }
    }
}
