package com.nagi.ddtools.ui.minepage.user.userInfo

import android.os.Bundle
import androidx.activity.viewModels
import com.nagi.ddtools.database.user.User
import com.nagi.ddtools.databinding.ActivityUserInfoBinding
import com.nagi.ddtools.ui.base.DdToolsBindingBaseActivity
import com.nagi.ddtools.ui.minepage.user.login.LoginActivity
import com.nagi.ddtools.utils.UiUtils.openPage

class UserInfoActivity : DdToolsBindingBaseActivity<ActivityUserInfoBinding>() {
    private val viewModel: UserInfoViewModel by viewModels()
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
    }

    private fun initView(user: User) {
        binding.apply {
            userAvatar.setImageUrl(user.avatar_url)
            userNickname.text = user.nickname
            userUsername.text = user.username
            userInclude.text = user.bio
        }
    }

    private fun initViewModel() {
        viewModel.thisUser.observe(this) { initView(it) }
    }
}