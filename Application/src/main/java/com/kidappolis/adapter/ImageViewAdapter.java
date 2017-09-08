package com.kidappolis.adapter;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.kidappolis.usage.satistics.R;
import com.kidappolis.util.TextUtils2;
import com.squareup.picasso.Picasso;


/**
 * Used to show images as attributes
 */
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
