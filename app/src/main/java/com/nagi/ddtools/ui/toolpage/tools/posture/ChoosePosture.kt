package com.nagi.ddtools.ui.toolpage.tools.posture

import android.os.Bundle
import com.nagi.ddtools.R
import com.nagi.ddtools.databinding.ActivityChoosePostureBinding
import com.nagi.ddtools.ui.base.DdToolsBindingBaseActivity

class ChoosePosture : DdToolsBindingBaseActivity<ActivityChoosePostureBinding>() {
    override fun createBinding(): ActivityChoosePostureBinding {
        return ActivityChoosePostureBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}