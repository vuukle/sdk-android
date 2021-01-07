package com.vuukle.webview

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.*
import android.webkit.WebView.WebViewTransport
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.vuukle.webview.ext.needOpenWithOther
import com.vuukle.webview.utils.Dialog
import com.vuukle.webview.utils.ListenerModalWindow
import com.vuukle.webview.utils.OpenPhoto
import com.vuukle.webview.utils.OpenSite


class MainActivity : AppCompatActivity(), ListenerModalWindow, PermissionListener {
    //URL for loading into WebView
    private val COMMENTS_URL = "https://cdn.vuukle.com/amp.html?url=https%3A%2F%2Fwww.prowrestling.com%2Fimpact-wrestling-results-1282020%2F&host=prowrestling.com&id=1196371&apiKey=46489985-43ef-48ed-9bfd-61971e6af217&img=https%3A%2F%2Fwww.prowrestling.com%2Fwp-content%2Fuploads%2F2020%2F12%2FKenny-Omega-Impact-Wrestling.jpeg&title=IMPACT%2BWrestling%2BResults%2B%252812%252F8%2529%253A%2BKenny%2BOmega%2BSpeaks%252C%2BKnockouts%2BTag%2BTournament%2BContinues%2521&tags=Featured"
    private val POWERBAR_URL = "https://cdntest.vuukle.com/widgets/powerbar.html?amp=false&apiKey=664e0b85-5b2c-4881-ba64-3aa9f992d01c&host=relaxed-beaver-76304e.netlify.com&articleId=Index&img=https%3A%2F%2Fwww.gettyimages.ie%2Fgi-resources%2Fimages%2FHomepage%2FHero%2FUK%2FCMS_Creative_164657191_Kingfisher.jpg&title=Index&url=https%3A%2F%2Frelaxed-beaver-76304e.netlify.app%2F&tags=123&author=123&lang=en&gr=false&darkMode=false&defaultEmote=1&items=&standalone=0&mode=horizontal"
    private val PERMISSION_REQUEST_CODE = 200

    //login name
    var name = "Alex"

    //login email
    var email = "email@test.com"
    var popup: WebView? = null

    //WebView
    var mWebViewComments: WebView? = null
    var mWebViewPowerBar: WebView? = null
    var mContainer: LinearLayout? = null
    var openSite: OpenSite? = null
    var dialog: Dialog? = null
    private val openPhoto = OpenPhoto()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initOnClicks();
        handleOnCreate()

    }

    private fun initOnClicks() {
        dialog?.addCloseListener() {
            mWebViewComments?.reload()
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
                PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_SHORT).show()

                // main logic
            } else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        showMessageOKCancel("You need to allow access permissions"
                        ) { dialog, which ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermission()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {

        AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show()
    }

    private fun handleOnCreate() {
        // debug test webView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        //initialising views
        setContentView(R.layout.activity_main)
        mWebViewComments = findViewById(R.id.activity_main_webview_comments)
        mWebViewPowerBar = findViewById(R.id.activity_main_webview_powerbar)
        mContainer = findViewById(R.id.container)
        openSite = OpenSite(this)
        dialog = Dialog(this)
        //initialising webView
        configWebView()

        //cookie
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(mWebViewComments, true)
        } else CookieManager.getInstance().setAcceptCookie(true)
        //load url to display in webView
        mWebViewComments?.loadUrl(COMMENTS_URL)
        mWebViewPowerBar?.loadUrl(POWERBAR_URL)

    }

    override fun onBackPressed() {
        if (popup != null && popup!!.parent != null) {
            mContainer!!.removeView(popup)
            popup!!.destroy()
        } else {
            mWebViewComments!!.goBack()
        }
    }

    private fun configWebView() {
        //javascript support

        mWebViewPowerBar!!.settings.javaScriptEnabled = true
        mWebViewPowerBar!!.settings.domStorageEnabled = true
        mWebViewPowerBar!!.settings.setSupportMultipleWindows(true)
        mWebViewPowerBar!!.webChromeClient = webChromeClient
        mWebViewPowerBar!!.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                //Clicked url
                Log.d(TAG, "Clicked url: $url")
                if (openSite!!.isOpenSupportInBrowser(url)) {
                    openSite!!.openPrivacyPolicy(url)
                } else if (url.contains("mailto:to") || url.contains("mailto:")) {
                    openSite!!.openApp(url)
                } else {
                    if (!url.needOpenWithOther()) {
                        dialog!!.openDialogOther(url)
                    }
                }
                return true
            }
        }


        mWebViewComments?.settings?.javaScriptEnabled = true
        mWebViewComments?.settings?.domStorageEnabled = true
        mWebViewComments?.settings?.setSupportZoom(false)
        mWebViewComments?.settings?.allowFileAccess = true
        mWebViewComments?.settings?.allowContentAccess = true
        mWebViewComments?.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        mWebViewComments!!.webChromeClient = webChromeClient
        mWebViewComments?.settings?.pluginState = WebSettings.PluginState.ON;
        mWebViewComments?.settings?.mediaPlaybackRequiresUserGesture = false;
        mWebViewComments!!.webViewClient = object : WebViewClient() {


            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                //Clicked url
                Log.i(TAG, "Clicked url: $url")
                if (openSite!!.isOpenSupportInBrowser(url)) {
                    openSite!!.openPrivacyPolicy(url)
                } else if (url.contains("mailto:to") || url.contains("mailto:")) {
                    openSite!!.openApp(url)
                } else {
                    if (!url.needOpenWithOther()) {
                        dialog!!.openDialogOther(url)
                    }
                }
                return true
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (CAMERA_PERMISSION == resultCode && requestCode == Activity.RESULT_OK) openPhoto.selectImage(this@MainActivity)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (dialog!!.uploadMessage == null) return
                if (intent == null) {
                    val intent1 = Intent()
                    intent1.data = openPhoto.imageUri
                    dialog!!.uploadMessage!!.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent1))
                } else dialog!!.uploadMessage!!.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent))
                dialog!!.uploadMessage = null
            }
        } else if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == dialog!!.uploadMessage) return
            val result = if (intent == null || resultCode != Activity.RESULT_OK) null else intent.data
            dialog!!.mUploadMessage!!.onReceiveValue(result)
            dialog!!.mUploadMessage = null
        }
    }

    private val webChromeClient: WebChromeClient = object : WebChromeClient() {
        override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
            Log.d("consoleJs", consoleMessage.message())
            //Listening for console message that contains "Comments initialized!" string
            if (consoleMessage.message().contains("Comments initialized!")) {
                //signInUser(name, email) - javascript function implemented on a page
                mWebViewComments!!.loadUrl("javascript:signInUser('$name', '$email')")
            }
            return super.onConsoleMessage(consoleMessage)
        }

        override fun onCreateWindow(view: WebView, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message): Boolean {
            popup = WebView(this@MainActivity)
            popup!!.settings.javaScriptEnabled = true
            popup!!.settings.domStorageEnabled = true
            popup!!.settings.pluginState = WebSettings.PluginState.ON
            popup!!.settings.setSupportMultipleWindows(true)
            popup!!.layoutParams = view.layoutParams
            popup!!.settings.userAgentString = popup!!.settings.userAgentString.replace("; wv", "")
            val urlLast = arrayOf("")
            popup!!.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    if (popup != null) {
                        if (url.contains(AUTH) || url.contains(CONSENT)) {
                            if (url.contains(errorTwitter)) dialog!!.close() else {
                                popup!!.loadUrl(url)
                                dialog!!.openDialog(popup)
                                if (url.contains(CONSENT)) hideKeyboard()
                            }
                        } else {

                            if (!url.needOpenWithOther()) {
                                dialog!!.openDialogOther(url)
                                dialog!!
                            }
                        }
                    }
                    checkConsent(url)
                    return true
                }

                private fun checkConsent(url: String) {
                    if (urlLast[0] == url) {
                        dialog!!.close()
                        popup!!.destroy()
                    } else {
                        urlLast[0] = url
                    }
                }
            }
            popup!!.webChromeClient = object : WebChromeClient() {
                override fun onCloseWindow(window: WebView) {
                    super.onCloseWindow(window)
                    dialog!!.close()
                    //if (mWebViewComments != null) mWebViewComments!!.reload()
                    mContainer!!.removeView(window)
                }
            }
            val transport = resultMsg.obj as WebViewTransport
            transport.webView = popup
            resultMsg.sendToTarget()
            return true
        }
    }

    fun hideKeyboard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun reloadView() {
        //mWebViewComments!!.reload()
    }

    companion object {
        const val TAG = "MainActivity"

        //Constant
        const val errorTwitter = "twitter/callback?denied"
        const val PRIVACY_POLICY = "https://docs.vuukle.com/"
        const val VUUKLE = "https://vuukle.com/"
        const val BLOG_VUUKLE = "https://blog.vuukle.com/"
        const val AUTH = "auth"
        const val CONSENT = "consent"
        const val REQUEST_SELECT_FILE = 1021
        const val FILE_CHOOSER_RESULT_CODE = 1
        const val CAMERA_PERMISSION = 2
    }

    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
        handleOnCreate()
    }

    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {

    }

    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
        Toast.makeText(this, "Please accept camera permission", Toast.LENGTH_LONG).show()
    }
}