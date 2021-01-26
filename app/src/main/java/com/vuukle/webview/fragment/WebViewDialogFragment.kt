package com.vuukle.webview.fragment

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import com.vuukle.webview.MainActivity
import com.vuukle.webview.MainActivity.Companion.FILE_CHOOSER_RESULT_CODE
import com.vuukle.webview.R
import com.vuukle.webview.ext.needOpenWithOther
import com.vuukle.webview.helpers.getDeviceHeight
import com.vuukle.webview.helpers.getDeviceWidth
import com.vuukle.webview.utils.OpenSite


public open class WebViewDialogFragment : AppCompatDialogFragment() {

    companion object {

        fun newInstance(url: String?): WebViewDialogFragment {

            val frag = WebViewDialogFragment()
            val bundle = Bundle()
            bundle.putString("url", url)
            frag.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
            frag.arguments = bundle
            return frag
        }
    }

    private lateinit var rootView: View
    private lateinit var webView: WebView
    private lateinit var closeButton: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var openSite: OpenSite

    private var mUploadMessage: ValueCallback<Array<Uri>>? = null
    private var mUploadMessageTwo: ValueCallback<Uri?>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        rootView = inflater.inflate(R.layout.fragment_dialog, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initWebViewConfigs()
        initOnClicks()
        loadPage()
    }

    private fun initViews() {

        webView = rootView.findViewById(R.id.dialog_web_view)
        closeButton = rootView.findViewById(R.id.closeButton)
        progressBar = rootView.findViewById(R.id.progressBar)
        openSite = OpenSite(activity!!)
        progressBar.max = 100
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(activity!!, theme) {
            override fun onBackPressed() {
                webView.goBack()
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebViewConfigs() {

        webView.settings.setSupportMultipleWindows(false)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.pluginState = WebSettings.PluginState.ON
        webView.settings.domStorageEnabled = true;
        webView.settings.allowContentAccess = true;
        webView.settings.allowFileAccess = true
        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.pluginState = WebSettings.PluginState.ON;
        webView.settings.mediaPlaybackRequiresUserGesture = false;

        webView.settings.userAgentString = "Mozilla/5.0 (Linux; Android 11) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Mobile Safari/537.36"

        webView.webViewClient = object : WebViewClient() {

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {

                val url = request?.url?.toString() ?: ""
                Log.i("testing url", url)
                val handleResult = handleUrlOpen(url)
                return handleResult?:super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
            }
        }

        webView.webChromeClient = object : WebChromeClient() {

            override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {

                mUploadMessage = filePathCallback;
                val intent = Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.type = "image/*";
                startActivityForResult(intent, FILE_CHOOSER_RESULT_CODE)
                return true
            }

            //For Android 4.1 only
            protected fun openFileChooser(filePathCallback: ValueCallback<Uri?>, acceptType: String?, capture: String?) {

                mUploadMessageTwo = filePathCallback;
                val intent = Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.type = "image/*";
                startActivityForResult(intent, FILE_CHOOSER_RESULT_CODE)
            }

            protected fun openFileChooser(filePathCallback: ValueCallback<Uri?>) {

                mUploadMessageTwo = filePathCallback;
                val intent = Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.type = "image/*";
                startActivityForResult(intent, FILE_CHOOSER_RESULT_CODE)
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                progressBar.progress = newProgress;
            }

        }
    }

    private fun loadPage() {

        val url = arguments?.getString("url")
        url?.let {
            webView.loadUrl(url)
        }
    }

    override fun onResume() {
        super.onResume()
        initUi()
    }

    private fun initOnClicks() {

        closeButton.setOnClickListener {
            dialog?.dismiss()
        }
    }

    private fun initUi() {

        val width = (getDeviceWidth(requireContext()) * 0.97).toInt()
        val height = (getDeviceHeight(requireContext()) * 0.97).toInt()
        dialog?.window?.setLayout(width, height)
    }

    override fun onDismiss(dialog: DialogInterface) {

        super.onDismiss(dialog)
        if (activity is MainActivity && webView.url?.contains(MainActivity.CONSENT) == true) {
            (activity as MainActivity).reloadContent()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (requestCode == FILE_CHOOSER_RESULT_CODE) {
                if (mUploadMessage == null) return
                mUploadMessage?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent))
                mUploadMessage = null
            }

        } else if (requestCode == FILE_CHOOSER_RESULT_CODE) {

            if (null == mUploadMessage) return
            val result = if (intent == null || resultCode != RESULT_OK) null else intent.data
            mUploadMessageTwo?.onReceiveValue(result)
            mUploadMessageTwo = null

        } else Toast.makeText(activity, "Failed to Upload Image", Toast.LENGTH_LONG).show()
    }

    fun handleUrlOpen(url: String): Boolean? {

        return when{

            (url.contains("mailto:to") || url.contains("mailto:")) -> {
                openSite.openApp(url)
                false
            }
            openSite.isOpenSupportInBrowser(url) -> {
                openSite.openPrivacyPolicy(url)
                false
            }
            url.contains("fb-messenger") -> {
                openSite.openMessenger(url)
                false
            }
            url.contains("telegram.me") || url.contains("t.me") -> {
                openSite.openTelegram(url)
                true
            }
            url.contains("whatsapp") -> {
                openSite.openWhatsApp(url, webView)
                true
            }
            url.needOpenWithOther() -> {
                webView.loadUrl(openSite.decodeUrl(url))
                false
            }
            else -> null
        }
    }
}