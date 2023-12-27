package com.nagi.ddtools

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nagi.ddtools.database.AppDatabase
import com.nagi.ddtools.database.homePagLis.HomePageList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    companion object {
        private const val SPLASH_DISPLAY_LENGTH = 1500L
    }

    // 声明用于存储主页数据的列表，并立即初始化以避免可空性问题
    private var homePageListData = ArrayList<HomePageList>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 设置跳过按钮的点击事件，直接启动主页面
        findViewById<Button>(R.id.skipButton).setOnClickListener {
            launchMainActivity(homePageListData)
        }

        // 延迟一定时间后启动主页面
        lifecycleScope.launch {
            delay(SPLASH_DISPLAY_LENGTH)
            launchMainActivity(homePageListData)
        }

        // 在后台加载数据并更新列表
        lifecycleScope.launch {
            val homePageList = withContext(Dispatchers.IO) {
                // 在后台线程访问数据库以避免阻塞UI
                AppDatabase.getInstance(applicationContext).homePageDao().getAll()
            }
            // 在主线程中更新数据列表
            homePageListData.clear()
            homePageListData.addAll(homePageList)
        }
    }

    // 启动主页面的方法，根据条件带或不带homePageListData
    private fun launchMainActivity(homePageList: ArrayList<HomePageList>) {
        // 检查活动是否未结束以防止IllegalStateException
        if (!isFinishing) {
            val mainIntent = Intent(this, MainActivity::class.java)

            // 如果数据符合大小条件，则添加到intent中
            if (homePageList.size in 1..99) {
                mainIntent.putParcelableArrayListExtra("homePageListData", homePageList)
            }

            // 启动主页面并结束当前页面，从返回栈中移除
            startActivity(mainIntent)
            finish()
        }
    }
}
