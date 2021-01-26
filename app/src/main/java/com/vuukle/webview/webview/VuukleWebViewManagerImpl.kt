package com.vuukle.webview.webview

import android.webkit.WebView

class VuukleWebViewManagerImpl(private val webView: WebView): VuukleWebViewManager {

    override fun loadPage(url: String) {
        webView.loadUrl(url)
    }
}