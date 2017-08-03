package com.lumen.adapter;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.lumen.usage.satistics.R;
import com.lumen.util.TextUtils2;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;


public class ImageViewAdapter {

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView view, String url) {
        if (TextUtils2.isNotEmpty(url)) {
            Picasso.with(
                    view.getContext())
                    .load(url)
                    .placeholder(R.drawable.ic_android_black_24dp)
                    .error(R.drawable.ic_android_black_24dp)
                    .into(view);
        }
    }
}
