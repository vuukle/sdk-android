package com.vuukle.webview.webview

import android.graphics.Bitmap
import android.os.Message
import android.webkit.*

class VuukleWebViewClient: WebChromeClient() {

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
    }

    override fun onReceivedTitle(view: WebView?, title: String?) {
        super.onReceivedTitle(view, title)
    }

    override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
        super.onReceivedIcon(view, icon)
    }

    override fun onReceivedTouchIconUrl(view: WebView?, url: String?, precomposed: Boolean) {
        super.onReceivedTouchIconUrl(view, url, precomposed)
    }

    override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
        return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
    }

    override fun onCloseWindow(window: WebView?) {
        super.onCloseWindow(window)
    }
}