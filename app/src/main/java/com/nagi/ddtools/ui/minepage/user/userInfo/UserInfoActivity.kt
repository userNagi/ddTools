package com.nagi.ddtools.ui.minepage.user.userInfo

import android.os.Bundle
import com.nagi.ddtools.databinding.ActivityUserInfoBinding
import com.nagi.ddtools.ui.base.DdToolsBindingBaseActivity

class UserInfoActivity : DdToolsBindingBaseActivity<ActivityUserInfoBinding>() {
    override fun createBinding(): ActivityUserInfoBinding {
        return ActivityUserInfoBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}