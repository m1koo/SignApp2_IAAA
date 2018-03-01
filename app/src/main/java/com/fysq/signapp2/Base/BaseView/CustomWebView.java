package com.fysq.signapp2.Base.BaseView;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Miko on 2017/4/24.
 */

public class CustomWebView extends WebView {
    public void initWeb() {
        getSettings().setJavaScriptEnabled(true);
        getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        getSettings().setSupportMultipleWindows(true);
        setWebViewClient(new WebViewClient());
        setWebChromeClient(new WebChromeClient());
    }

    public CustomWebView(Context context) {
        super(context);
        initWeb();
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWeb();
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWeb();
    }
}
