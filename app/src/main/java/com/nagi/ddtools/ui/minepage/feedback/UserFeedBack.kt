package com.nagi.ddtools.ui.minepage.feedback

import android.os.Bundle
import android.os.Looper
import com.nagi.ddtools.data.Resource
import com.nagi.ddtools.databinding.ActivityUserFeedBackBinding
import com.nagi.ddtools.resourceGet.NetGet
import com.nagi.ddtools.ui.base.DdToolsBindingBaseActivity
import com.nagi.ddtools.utils.UiUtils.dialog

class UserFeedBack : DdToolsBindingBaseActivity<ActivityUserFeedBackBinding>() {
    override fun createBinding(): ActivityUserFeedBackBinding {
        return ActivityUserFeedBackBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.apply {
            titleInclude.apply {
                titleBack.setOnClickListener { finish() }
                titleText.text = "用户反馈"
            }
            feedbackUp.setOnClickListener {
                if (checkUp()) {
                    dialog("提示", "收到，感谢反馈", "我知道了", "取消")
                    NetGet.userFeedBack(
                        contactInformation.text.toString(),
                        feedbackInput.text.toString()
                    )
                    finish()
                } else {
                    dialog("提示", "请填写完整，最多五百字，联系方式不能为空", "我知道了", "取消")
                }
            }
        }
    }

    private fun checkUp(): Boolean {
        binding.apply {
            if (feedbackInput.text.length > 500) return false
            if (feedbackInput.text.isNullOrEmpty()) return false
            if (contactInformation.text.length > 100) return false
            if (contactInformation.text.isNullOrEmpty()) return false
            return true
        }
    }
}