package com.nagi.ddtools.ui.minepage.user.userInfo

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.nagi.ddtools.database.activityList.ActivityList
import com.nagi.ddtools.database.user.User
import com.nagi.ddtools.databinding.ActivityUserInfoBinding
import com.nagi.ddtools.ui.adapter.ActivityListAdapter
import com.nagi.ddtools.ui.base.DdToolsBindingBaseActivity
import com.nagi.ddtools.ui.minepage.user.login.LoginActivity
import com.nagi.ddtools.utils.FileUtils
import com.nagi.ddtools.utils.UiUtils.dialog
import com.nagi.ddtools.utils.UiUtils.openPage
import com.nagi.ddtools.utils.UiUtils.toast

class UserInfoActivity : DdToolsBindingBaseActivity<ActivityUserInfoBinding>() {
    private lateinit var uri: Uri
    private val viewModel: UserInfoViewModel by viewModels()
    private lateinit var collectActivityAdapter: ActivityListAdapter
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    override fun createBinding(): ActivityUserInfoBinding {
        return ActivityUserInfoBinding.inflate(layoutInflater)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUser()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!viewModel.getUserLogin()) initViewModel() else openPage(
            this@UserInfoActivity, LoginActivity::class.java
        )
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                handleActivityResult(result)
            }
    }

    private fun initView(user: User) {
        binding.apply {
            userNickname.text = user.nickname
            userUsername.text = user.username
            userInclude.text = user.bio
            collectActivityAdapter = ActivityListAdapter(mutableListOf(), -1)
            userCollectList.adapter = collectActivityAdapter
            includeTitle.titleText.text = "用户信息"
            includeTitle.titleBack.setOnClickListener { finish() }
            includeTitle.titleRight.text = "退出"
            includeTitle.titleRight.visibility = View.VISIBLE
            includeTitle.titleRight.setOnClickListener { loginOut() }
            userEdit.setOnClickListener { editUser() }
            if (user.avatar_url.isNotEmpty()) {
                userAvatar.setImageUrl(user.avatar_url)
                userAvatar.onCLick(user.avatar_url)
            } else userAvatar.setOnClickListener {
                dialog(
                    "选择头像",
                    "请选择图片，注意，由于服务器性能原因，目前头像上传后暂不可直接修改，如需修改，请通过用户反馈告知用户名，管理员一天内将会删除原本的头像（不可恢复）",
                    "确定",
                    "取消",
                    {  FileUtils.openImageGallery(this@UserInfoActivity, resultLauncher)}
                )
            }
        }
    }

    private fun initViewModel() {
        viewModel.thisUser.observe(this) { initView(it) }
        viewModel.collectActivity.observe(this) { updateAdapter(it) }
    }

    private fun updateAdapter(activities: List<ActivityList>) {
        collectActivityAdapter.isShowTime = false
        collectActivityAdapter.updateData(activities, -1)
    }

    private fun loginOut() {
        dialog("确认退出登录吗？", "退出后……额，退出后除了不能评价和打不开这个页面外什么也不会发生……",
            "确定", "取消", {
                viewModel.logout()
                finish()
            })
    }

    private fun handleActivityResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uriEnd ->
                try {
                    uri = uriEnd
                    contentResolver.openInputStream(uriEnd)?.use { inputStream ->
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        binding.userAvatar.setImageBitmap(bitmap)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    toast("文件未找到或无法打开")
                } catch (e: Exception) {
                    e.printStackTrace()
                    toast("读取图片时出错")
                }
            } ?: toast("您未选择图片")
        }
    }

    private fun editUser() {
    }
}