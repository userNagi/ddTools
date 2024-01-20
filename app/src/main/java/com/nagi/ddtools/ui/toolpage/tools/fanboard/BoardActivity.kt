package com.nagi.ddtools.ui.toolpage.tools.fanboard

import android.os.Bundle
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.nagi.ddtools.R

class BoardActivity : AppCompatActivity() {
    private var textColorStr: String = ""
    private var backColorStr: String = ""
    private var textGet: String = ""
    private var textSizeGet: Int = 3
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)
        initView()
    }

    private fun initView() {
        hideSystemUI()
        getBundle()
        val textColor = Gson().fromJson(textColorStr, IntArray::class.java)
        val backColor = Gson().fromJson(backColorStr, IntArray::class.java)
        findViewById<TextView>(R.id.board_text).apply {
            text = textGet
            textSize = (150+textSizeGet*10).toFloat()
            setTextColor(textColor[0])
        }
        findViewById<LinearLayout>(R.id.board_back).apply {
            setBackgroundColor(backColor[0])
        }
    }

    private fun getBundle() {
        val bundle = intent.extras
        if (bundle != null) {
            textColorStr = bundle.getString("textColor").toString()
            backColorStr = bundle.getString("backColor").toString()
            textSizeGet = bundle.getInt("textSize")
            textGet = bundle.getString("text").toString()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

    private fun hideSystemUI() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
}