package com.nagi.ddtools.ui.minepage.setting

import android.os.Bundle
import androidx.activity.viewModels
import com.nagi.ddtools.databinding.ActivityGlobalSettingBinding
import com.nagi.ddtools.ui.base.DdToolsBindingBaseActivity
import com.nagi.ddtools.ui.minepage.user.userInfo.UserInfoActivity
import com.nagi.ddtools.utils.PrefsUtils
import com.nagi.ddtools.utils.UiUtils
import com.nagi.ddtools.utils.UiUtils.dialog

class GlobalSettingActivity : DdToolsBindingBaseActivity<ActivityGlobalSettingBinding>() {
    private val viewModel: GlobalSettingViewModel by viewModels()
    override fun createBinding(): ActivityGlobalSettingBinding {
        return ActivityGlobalSettingBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initViewModel()
    }

    private fun initView() {
        val location = PrefsUtils.getSettingLocation(applicationContext)
        binding.apply {
            globalSettingLocation.setData("区域设置", location ?: "全世界")
            globalSettingLocation.setOnClickListener {
                setGlobalLocation()
            }
            globalSettingUser.setData("未登录", "去登录")
            globalSettingUser.setOnClickListener {
                UiUtils.openPage(this@GlobalSettingActivity, UserInfoActivity::class.java)
            }
        }
    }

    private fun initViewModel() {
        viewModel.users.observe(this) {
            binding.globalSettingUser.setData(it.nickname, "用户设置")
        }
    }

    private fun setGlobalLocation() {
        dialog(
            "请选择地区",
            "可在默认的选项中选择，选择后，进入查询页会自动优先展示该地区相关的信息",
            "确定",
            "取消",
            {},
            {}
        )
    }
}