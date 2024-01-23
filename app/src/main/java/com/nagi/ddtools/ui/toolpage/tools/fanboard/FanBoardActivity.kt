package com.nagi.ddtools.ui.toolpage.tools.fanboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.google.gson.Gson
import com.nagi.ddtools.databinding.ActivityFanBoardBinding
import com.nagi.ddtools.ui.base.DdToolsBindingBaseActivity

class FanBoardActivity : DdToolsBindingBaseActivity<ActivityFanBoardBinding>() {
    private val viewModel: FanBoardViewModel by viewModels()
    private val bundle: Bundle = Bundle()
    override fun createBinding(): ActivityFanBoardBinding {
        return ActivityFanBoardBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.isRolling.observe(this) { isRolling ->
            initSwitch(isRolling)
        }
        initView()
    }

    private fun initView() {
        binding.apply {
            boardColorPicker.setText("文字颜色：")
            boardColorPickerBackground.setText("背景颜色：")
            titleInclude.titleBack.setOnClickListener { finish() }
            titleInclude.titleText.text = "灯牌"
            boardIsRoll.setOnCheckedChangeListener { _, _ ->
                viewModel.toggleRolling()
            }
            boardClick.setOnClickListener {
                val backColor = boardColorPickerBackground.getSelectedColors()
                val color = boardColorPicker.getSelectedColors()
                val text = boardInputEdit.text.toString()
                val textSize = boardSizeSeekbar.progress
                bundle.apply {
                    putString("backColor", Gson().toJson(backColor))
                    putString("textColor", Gson().toJson(color))
                    putString("text", text)
                    putInt("textSize", textSize)
                }
                openBoard()
            }
        }
    }

    private fun initSwitch(isRolling: Boolean) {
        binding.apply {
            boardIsRoll.isChecked = isRolling
            bundle.putBoolean("isRolling", isRolling)
            if (!isRolling) {
                boardRollWay.visibility = View.GONE
                boardRollWayGroup.visibility = View.GONE
                boardRollSpeed.visibility = View.GONE
                boardSpeedSeekbar.visibility = View.GONE
            } else {
                boardRollWay.visibility = View.VISIBLE
                boardRollWayGroup.visibility = View.VISIBLE
                boardRollSpeed.visibility = View.VISIBLE
                boardSpeedSeekbar.visibility = View.VISIBLE
                bundle.putInt("rollSpeed", boardSpeedSeekbar.progress)
                bundle.putString(
                    "rollWay", boardRollWayGroup.checkedRadioButtonId.toString()
                )
            }
        }
    }

    private fun openBoard() {
        val intent = Intent(this, BoardActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}