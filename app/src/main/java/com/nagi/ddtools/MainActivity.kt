package com.nagi.ddtools

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nagi.ddtools.database.homePagLis.HomePageList
import com.nagi.ddtools.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 通过layoutInflater初始化binding对象
        binding = ActivityMainBinding.inflate(layoutInflater)
        // 设置Activity的内容视图为binding的根视图
        setContentView(binding.root)

        // 根据Android版本加载传递的HomePageList数据
        val dataList: ArrayList<HomePageList>? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13 (TIRAMISU) 及以上版本使用类型安全的方法获取ParcelableArrayList
                intent.getParcelableArrayListExtra("homePageListData", HomePageList::class.java)
            } else {
                // 低于Android 13的版本使用旧方法
                // 此处的Suppress注解用于抑制警告，因为方法在新版Android中已被弃用
                @Suppress("DEPRECATION")
                intent.getParcelableArrayListExtra("homePageListData")
            }

    }
}