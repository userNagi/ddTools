package com.nagi.ddtools.ui.minepage.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.nagi.ddtools.data.Resource
import com.nagi.ddtools.databinding.ActivityGlobalSettingBinding
import com.nagi.ddtools.databinding.DialogSingeSpinnerBinding
import com.nagi.ddtools.resourceGet.NetGet
import com.nagi.ddtools.ui.base.DdToolsBindingBaseActivity
import com.nagi.ddtools.ui.minepage.user.userInfo.UserInfoActivity
import com.nagi.ddtools.utils.LogUtils
import com.nagi.ddtools.utils.NetUtils.getLocalVersion
import com.nagi.ddtools.utils.PrefsUtils
import com.nagi.ddtools.utils.UiUtils
import com.nagi.ddtools.utils.UiUtils.dialog
import com.nagi.ddtools.utils.UiUtils.toast
import kotlinx.coroutines.launch

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
        val location = PrefsUtils.getSettingLocation(applicationContext) ?: "全世界"
        binding.apply {
            titleInclude.titleText.text = "全局设置"
            globalSettingUser.setData("未登录", "去登录")
            globalSettingLocation.setData("区域设置", location.ifEmpty { "全世界" })
            globalSettingCheckUpdate.setData("检查更新", "v${getLocalVersion(applicationContext)}")
            globalSettingCheckUpdate.setOnClickListener { checkUpdate() }
            titleInclude.titleBack.setOnClickListener { finish() }
            globalSettingLocation.setOnClickListener { setGlobalLocation() }
            globalSettingUser.setOnClickListener {
                UiUtils.openPage(this@GlobalSettingActivity, UserInfoActivity::class.java)
            }
        }
    }

    private fun initViewModel() {
        viewModel.users.observe(this) {
            it?.let {
                binding.globalSettingUser.setData(it.nickname, "用户设置")
            }
        }
    }

    private fun setGlobalLocation() {
        val spinnerBinding = DialogSingeSpinnerBinding.inflate(layoutInflater)
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        spinnerBinding.spinner.adapter = adapter

        viewModel.location.observe(this) { locationList ->
            adapter.clear()
            adapter.add("全世界")
            adapter.addAll(locationList)
            adapter.notifyDataSetChanged()
        }

        val onPositive = {
            val selectedLocation = spinnerBinding.spinner.selectedItem as String
            PrefsUtils.setSettingLocation(this,
                selectedLocation.let { if (selectedLocation != "全世界") it else "" })
            binding.globalSettingLocation.setData("区域设置", selectedLocation.ifEmpty { "全世界" })
        }

        dialog(
            "请选择地区",
            "可在默认的选项中选择，选择后，进入查询页会自动优先展示该地区相关的信息",
            "确定",
            "取消",
            onPositive,
            {},
            customView = spinnerBinding.root
        )
    }

    private var lastClickTime: Long = 0
    private fun checkUpdate() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime >= 5000) {
            lastClickTime = currentTime
            NetGet.getUpdateDetails { resource ->
                lifecycleScope.launch {
                    when (resource) {
                        is Resource.Success -> {
                            val updateInfo = resource.data
                            val version = getLocalVersion(applicationContext)
                            if (updateInfo.version != version && updateInfo.updateUrl.isNotEmpty()) {
                                try {
                                    applicationContext.dialog(
                                        title = updateInfo.title,
                                        message = updateInfo.message,
                                        positiveButtonText = "确定",
                                        onPositive = {
                                            startActivity(
                                                Intent(
                                                    Intent.ACTION_VIEW,
                                                    Uri.parse(updateInfo.updateUrl)
                                                )
                                            )
                                        },
                                        negativeButtonText = "取消",
                                        onNegative = {}
                                    )
                                } catch (e: Exception) {
                                    LogUtils.e("Get Update Failed: $e")
                                }
                            } else toast("当前已是最新版本")
                        }

                        is Resource.Error -> {
                            LogUtils.e("Error checking for update: ${resource.message}")
                        }
                    }
                }
            }
        } else toast("每5秒只能点一次，请不要频繁点击")

    }
}