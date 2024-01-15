package com.nagi.ddtools.ui.toolpage.tools.fanboard

import android.os.Bundle
import androidx.activity.viewModels
import com.nagi.ddtools.databinding.ActivityFanBoardBinding
import com.nagi.ddtools.ui.base.DdToolsBaseActivity

class FanBoardActivity : DdToolsBaseActivity() {
    private lateinit var binding: ActivityFanBoardBinding
    private val viewModel: FanBoardViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFanBoardBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        viewModel.isRolling.observe(this) { isRolling ->
            binding.boardIsRoll.isChecked = isRolling
            binding.boardIsRoll.text = if (isRolling) "滚动" else "不滚动"
        }
        initView()
    }

    private fun initView() {
        binding.boardColorPicker.setText("文字颜色：")
        binding.boardIsRoll.setOnCheckedChangeListener { _, _ ->
            viewModel.toggleRolling()
        }
    }

}