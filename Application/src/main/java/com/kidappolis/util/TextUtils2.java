package com.kidappolis.util;

import android.text.TextUtils;
import android.widget.TextView;

/**
 * Text utils
 */

public class TextUtils2 {

    public static boolean isNotEmpty(CharSequence s) {
        return !TextUtils.isEmpty(s);
    }

    public static String getString(TextView textView) {
        return textView.getText().toString();
    }

}
