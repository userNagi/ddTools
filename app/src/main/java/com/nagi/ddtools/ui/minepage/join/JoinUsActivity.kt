package com.nagi.ddtools.ui.minepage.join

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nagi.ddtools.R
import com.nagi.ddtools.databinding.ActivityJoinUsBinding
import com.nagi.ddtools.ui.base.DdToolsBindingBaseActivity

class JoinUsActivity : DdToolsBindingBaseActivity<ActivityJoinUsBinding>() {
    override fun createBinding(): ActivityJoinUsBinding {
        return ActivityJoinUsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.titleInclude.titleBack.setOnClickListener { finish() }
        binding.titleInclude.titleText.text = "加入我们"
    }
}