package com.nagi.ddtools.ui.toolpage.tools.idolsearch.details

import android.os.Bundle
import com.nagi.ddtools.databinding.ActivityDetailsActivityBinding
import com.nagi.ddtools.ui.base.DdToolsBindingBaseActivity

class IdolDetails : DdToolsBindingBaseActivity<ActivityDetailsActivityBinding>() {
    override fun createBinding(): ActivityDetailsActivityBinding {
        return ActivityDetailsActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}