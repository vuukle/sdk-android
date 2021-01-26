package com.vuukle.webview.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import com.vuukle.webview.MainActivity
import java.io.UnsupportedEncodingException
import java.net.URLDecoder

class OpenSite(private val context: Context) {

    fun openWhatsApp(url: String, view: WebView) {
        var url = url
        url = decodeUrl(url)
        if (!url.contains("whatsapp://send") && !url.contains("fb-messenger"))
            view.loadUrl(url)
        else if (url.contains("whatsapp://send"))
            openApp("https://api.whatsapp.com" + url.substring(url.indexOf("://") + 2))
    }

    fun openTelegram(url: String) {

        var url = url
        url = decodeUrl(url)
        if (url.contains("telegram") || url.contains("t.me")) {

            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            var link = url.substring(url.indexOf("?url=") + 5)
            sendIntent.putExtra(Intent.EXTRA_TEXT, link)
            sendIntent.type = "text/plain"
            sendIntent.setPackage("org.telegram.messenger")
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            try {
                context.startActivity(sendIntent)
            } catch (ex: ActivityNotFoundException) {
                val mIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=org.telegram.messenger"))
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(mIntent)
            }

        }
    }

    fun openMessenger(url: String) {
        var url = url
        url = decodeUrl(url)
        if (url.contains("fb-messenger")) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent
                    .putExtra(Intent.EXTRA_TEXT,
                            url.substring(url.indexOf("?link=") + 6))
            sendIntent.type = "text/plain"
            sendIntent.setPackage("com.facebook.orca")
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(sendIntent)
            } catch (ex: ActivityNotFoundException) {
                val mIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.facebook.orca"))
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(mIntent)
            }
        }
    }

    fun isOpenSupportInBrowser(url: String): Boolean {
        return url.contains(MainActivity.PRIVACY_POLICY) || url.contains(MainActivity.VUUKLE) || url.contains(MainActivity.BLOG_VUUKLE)
    }

    fun openPrivacyPolicy(url: String?) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    fun openEmail(url: String) {

        var url = url
        url = decodeUrl(url)
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "", null))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, url.substring(url.indexOf("subject=") + 8, url.indexOf("&body")))
        emailIntent.putExtra(Intent.EXTRA_TEXT, url.substring(url.indexOf("body=") + 5))
        try {
            context.startActivity(Intent.createChooser(emailIntent, null))
        } catch (ex: ActivityNotFoundException) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.gm")))
        }
    }

    fun decodeUrl(url: String): String {

        var url = url
        url = try {
            URLDecoder.decode(url, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            return url
        }
        return url
    }

    fun openApp(url: String?) {

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

}