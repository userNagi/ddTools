package com.nagi.ddtools.ui.base

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.nagi.ddtools.R
import com.nagi.ddtools.utils.UiUtils

class WebViewActivity : DdToolsBaseActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        UiUtils.showLoading(this@WebViewActivity)
        if (intent.extras == null || !intent.extras!!.containsKey("url")) {
            UiUtils.hideLoading()
            finish()
        } else {
            val url = intent.extras!!.getString("url")
            webView = findViewById(R.id.webview)
            webView.webViewClient = WebViewClient()
            webView.settings.javaScriptEnabled = true
            webView.settings.domStorageEnabled = true
            webView.settings.allowFileAccess = true
            webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
            webView.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    UiUtils.hideLoading()
                }
            }
            webView.loadUrl(url!!)
        }


    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
