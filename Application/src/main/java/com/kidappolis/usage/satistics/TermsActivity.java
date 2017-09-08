package com.kidappolis.usage.satistics;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Terms of service screen
 */

public class TermsActivity extends BackArrowActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_terms);

        WebView terms = findViewById(R.id.terms);
        terms.setWebViewClient(new WebViewClient());
        terms.getSettings().setJavaScriptEnabled(true);
        terms.loadUrl("https://docs.google.com/a/toptal.com/document/d/1aP3wAa8OhOY0ImI6aFsIErzPn-APIXPqGwiLJl2C-Ek/edit?usp=sharing");
    }
}
