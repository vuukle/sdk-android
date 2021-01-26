package com.vuukle.webview

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vuukle.webview.constants.constants
import com.vuukle.webview.ext.needOpenWithOther
import com.vuukle.webview.fragment.WebViewDialogFragment
import com.vuukle.webview.utils.OpenSite


class MainActivity : AppCompatActivity() {

    //URL for loading into WebView
    private val COMMENTS_URL = "https://cdn.vuukle.com/amp.html?url=https%3A%2F%2Fwww.prowrestling.com%2Fimpact-wrestling-results-1282020%2F&host=prowrestling.com&id=1196371&apiKey=46489985-43ef-48ed-9bfd-61971e6af217&img=https%3A%2F%2Fwww.prowrestling.com%2Fwp-content%2Fuploads%2F2020%2F12%2FKenny-Omega-Impact-Wrestling.jpeg&title=IMPACT%2BWrestling%2BResults%2B%252812%252F8%2529%253A%2BKenny%2BOmega%2BSpeaks%252C%2BKnockouts%2BTag%2BTournament%2BContinues%2521&tags=Featured"

    //login name
    var name = "Alex"

    //login email
    var email = "email@test.com"

    //WebView
    var mWebViewComments: WebView? = null
    var mContainer: LinearLayout? = null
    lateinit var openSite: OpenSite

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
        const val FILE_CHOOSER_RESULT_CODE = 117
    }

    private var mUploadMessage: ValueCallback<Array<Uri>>? = null
    private var mUploadMessageTwo: ValueCallback<Uri?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleOnCreate()
    }

    private fun handleOnCreate() {

        // debug test webView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        //initialising views
        setContentView(R.layout.activity_main)
        mWebViewComments = findViewById(R.id.activity_main_webview_comments)
        mContainer = findViewById(R.id.container)
        openSite = OpenSite(this)

        //initialising webView
        configCommentWebView()

        //cookie
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(mWebViewComments, true)
        } else CookieManager.getInstance().setAcceptCookie(true)
        //load url to display in webView
        mWebViewComments?.loadUrl(COMMENTS_URL)

    }

    override fun onBackPressed() {
        mWebViewComments!!.goBack()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configCommentWebView() {

        mWebViewComments?.settings?.javaScriptEnabled = true
        mWebViewComments?.settings?.domStorageEnabled = true
        mWebViewComments?.settings?.setSupportZoom(false)
        mWebViewComments?.settings?.allowFileAccess = true
        mWebViewComments?.settings?.allowContentAccess = true
        mWebViewComments?.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        mWebViewComments?.settings?.pluginState = WebSettings.PluginState.ON;
        mWebViewComments?.settings?.mediaPlaybackRequiresUserGesture = false;
        mWebViewComments?.settings?.userAgentString = "Mozilla/5.0 (Linux; Android 11) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Mobile Safari/537.36"
        mWebViewComments?.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                //Clicked url
                Log.i(TAG, "Clicked url: $url")
                if (openSite.isOpenSupportInBrowser(url)) {
                    openSite.openPrivacyPolicy(url)
                } else if (url.contains("mailto:to") || url.contains("mailto:")) {
                    openSite.openApp(url)
                } else {
                    if (!url.needOpenWithOther()) {
                        handleUrlOpen(url)
                    }
                }
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                mWebViewComments?.evaluateJavascript(constants.jsDisableZoom, null)
                mWebViewComments?.evaluateJavascript(constants.addPowerBarScript, null)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }
        }

        mWebViewComments?.webChromeClient = object : WebChromeClient() {

            override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {

                mUploadMessage = filePathCallback;
                val intent = Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.type = "image/*";
                startActivityForResult(intent, FILE_CHOOSER_RESULT_CODE)
                return true
            }

            override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {

                val alertDialog = AlertDialog.Builder(this@MainActivity)
                alertDialog.setMessage(message)

                alertDialog.setPositiveButton("OK") { dialog, which ->
                    result?.confirm();
                    dialog.dismiss()
                }

                alertDialog.setNegativeButton("CANCEL") { dialog, which ->
                    result?.cancel()
                    dialog.dismiss()
                }

                alertDialog.create()

                alertDialog.show();

                return true;
            }

            override fun onJsPrompt(view: WebView?, url: String?, message: String?, defaultValue: String?, result: JsPromptResult?): Boolean {

                val alertDialog = AlertDialog.Builder(this@MainActivity)
                alertDialog.setMessage(message)

                alertDialog.setPositiveButton("OK") { dialog, which ->
                    result?.confirm();
                    dialog.dismiss()
                }

                alertDialog.setNegativeButton("CANCEL") { dialog, which ->
                    result?.cancel()
                    dialog.dismiss()
                }

                alertDialog.create()

                alertDialog.show();

                return true;
            }

            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {

                val alertDialog = AlertDialog.Builder(this@MainActivity)
                alertDialog.setMessage(message)
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    dialog.dismiss()
                }.create();

                alertDialog.show();
                result?.confirm();

                return true;
            }
        }
    }

    private fun handleUrlOpen(url: String) {

        when {
            url.contains("fb-messenger") -> {
                openSite.openMessenger(url)
            }
            url.contains("telegram.me") || url.contains("t.me")-> {
                openSite.openTelegram(url)
            }
            url.contains("whatsapp") -> {

                if (!url.contains("whatsapp://send") && !url.contains("fb-messenger")) {
                    openUrlInDialog(openSite.decodeUrl(url))
                } else {
                    openSite.openWhatsApp(url, mWebViewComments!!)
                }
            }
            else -> {
                openUrlInDialog(url)
            }
        }
    }

    private fun openUrlInDialog(url: String) {

        val dialogFragment = WebViewDialogFragment.newInstance(url)
        dialogFragment.show(supportFragmentManager, "fragment_edit_name")
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

    fun reloadContent() {
        mWebViewComments?.reload()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (requestCode == FILE_CHOOSER_RESULT_CODE) {
                if (mUploadMessage == null) return
                mUploadMessage?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data))
                mUploadMessage = null
            }

        } else if (requestCode == FILE_CHOOSER_RESULT_CODE) {

            if (null == mUploadMessage) return
            val result = if (intent == null || resultCode != RESULT_OK) null else intent.data
            mUploadMessageTwo?.onReceiveValue(result)
            mUploadMessageTwo = null

        } else Toast.makeText(this, "Failed to Upload Image", Toast.LENGTH_LONG).show()
    }
}