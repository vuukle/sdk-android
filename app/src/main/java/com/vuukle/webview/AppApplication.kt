package com.vuukle.webview

import android.app.Application
import android.content.Context

class AppApplication : Application() {

    companion object {
        lateinit var mContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this
    }
}