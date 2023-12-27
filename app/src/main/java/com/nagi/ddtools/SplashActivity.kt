package com.nagi.ddtools

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    companion object {
        private const val SPLASH_DISPLAY_LENGTH = 1500L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        // 设置跳过按钮的点击事件，直接启动主页面
        findViewById<Button>(R.id.skipButton).setOnClickListener {
            launchMainActivity()
        }
        // 延迟一定时间后启动主页面
        lifecycleScope.launch {
            delay(SPLASH_DISPLAY_LENGTH)
            launchMainActivity()
        }
    }

    // 启动主页面的方法，根据条件带或不带homePageListData
    private fun launchMainActivity() {
        // 检查活动是否未结束以防止IllegalStateException
        if (!isFinishing) {
            val mainIntent = Intent(this, MainActivity::class.java)
            // 启动主页面并结束当前页面，从返回栈中移除
            startActivity(mainIntent)
            finish()
        }
    }
}
