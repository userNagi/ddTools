package com.nagi.ddtools.ui.toolpage.tools.fanboard

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.nagi.ddtools.databinding.ActivityFanBoardBinding

class FanBoardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFanBoardBinding
    private val viewModel: FanBoardViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFanBoardBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        initView()
    }
    private fun initView() {
    }
}