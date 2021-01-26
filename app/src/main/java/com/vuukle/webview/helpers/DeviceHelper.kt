package com.vuukle.webview.helpers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import com.vuukle.webview.AppApplication
import java.util.*
import kotlin.random.Random

fun getDeviceWidth(activity: FragmentActivity): Float {

    val displayMetrics = DisplayMetrics()
    activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.widthPixels.toFloat()
}

fun getStatusBarHeight(context: Context): Int {

    val resourceId: Int =
        context.resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        return context.resources.getDimensionPixelSize(resourceId)
    }
    return -1
}

fun getDeviceWidth(context: Context): Float {

    val displayMetrics = DisplayMetrics()
    val windowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.widthPixels.toFloat()
}

fun getDeviceFullHeight(context: Context): Float {

    val displayMetrics = DisplayMetrics()
    val windowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    windowManager.defaultDisplay.getMetrics(displayMetrics)

    var height = displayMetrics.heightPixels.toFloat()

    val resources = context.resources
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    if (resourceId > 0) {
        height += resources.getDimensionPixelSize(resourceId).toFloat()
    }

    return height
}

fun getDeviceCenterY(activity: Activity): Int {
    val display: Display = activity.windowManager.defaultDisplay
    val size = Point()
    display.getSize(size)
    val height = size.y
    val centerY = height / 2

    return centerY
}

fun getDeviceHeight(context: Context): Float {

    val displayMetrics = DisplayMetrics()
    val windowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    windowManager.defaultDisplay.getMetrics(displayMetrics)

    return displayMetrics.heightPixels.toFloat()
}

fun getDeviceFullHeight(activity: FragmentActivity): Float {

    val displayMetrics = DisplayMetrics()
    activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.heightPixels.toFloat()
}

@SuppressLint("HardwareIds")
fun getDeviceUUID(): String? {

    return Settings.Secure.getString(
        AppApplication.mContext.contentResolver,
        Settings.Secure.ANDROID_ID
    )
}

fun getScreenWidthPixel(): Int {
    val display =
        (AppApplication.mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    val displayMetrics = DisplayMetrics()
    display.getMetrics(displayMetrics)
    return displayMetrics.widthPixels
}

fun getNavigationBarHeight(): Int {

    if (AppApplication.mContext == null) return 0

    val resources: Resources = AppApplication.mContext.resources
    val resourceId: Int =
        resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else 0
}

fun getAppVersion(): String {

    val packageManager = AppApplication.mContext.packageManager
    val packageName = AppApplication.mContext.packageName

    if (packageManager == null || packageName == null)
        return ""

    val packageInfo = packageManager.getPackageInfo(packageName, 0)
    return packageInfo.versionName ?: ""
}

fun getDeviceLanguageISO2(): String {

    val language = Locale.getDefault().isO3Language
    return language.substring(0, 2)
}

fun getRandomNumberAccordingHeight(minPercent: Float, maxPercent: Float): Int {

    val deviceHeight = getDeviceFullHeight(AppApplication.mContext)
    val minimum = (deviceHeight * minPercent / 100).toInt()
    val maximum = (deviceHeight * maxPercent / 100).toInt()
    return Random.nextInt(minimum, maximum)
}