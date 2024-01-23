package com.nagi.ddtools.ui.base

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.WindowInsetsControllerCompat
import com.nagi.ddtools.R
import com.nagi.ddtools.utils.UiUtils

class WebViewActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        setupStatusBar()
        UiUtils.showLoading(this@WebViewActivity)
        if (intent.extras == null || !intent.extras!!.containsKey("url")) {
            UiUtils.hideLoading()
            finish()
        } else {
            val url = intent.extras!!.getString("url")!!
            webView = findViewById(R.id.webview)
            webView.apply {
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        UiUtils.hideLoading()
                    }
                }
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    allowFileAccess = true
                    cacheMode = WebSettings.LOAD_DEFAULT
                }
                loadUrl(url)
            }
            findViewById<AppCompatImageView>(R.id.webview_back).setOnClickListener { finish() }
            findViewById<TextView>(R.id.webview_title).text =
                if (intent.extras!!.containsKey("title")) intent.extras!!.getString("title")!! else ""

        }
    }

    private fun setupStatusBar() {
        window.statusBarColor = Color.WHITE
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
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
