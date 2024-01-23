package com.nagi.ddtools.ui.minepage.about

import android.os.Bundle
import com.nagi.ddtools.databinding.ActivityAboutBinding
import com.nagi.ddtools.resourceGet.NetGet.getUrl
import com.nagi.ddtools.ui.base.DdToolsBaseActivity
import com.nagi.ddtools.ui.base.WebViewActivity
import com.nagi.ddtools.utils.NetUtils
import com.nagi.ddtools.utils.UiUtils.dialog
import com.nagi.ddtools.utils.UiUtils.openPage
import com.nagi.ddtools.utils.UiUtils.toast

class AboutActivity : DdToolsBaseActivity() {
    private lateinit var binding: ActivityAboutBinding
    private var clickContentEasterEggNum = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        binding.aboutAppVersion.text = NetUtils.getLocalVersion(applicationContext)
        binding.aboutAppContent.setOnClickListener {
            clickContentEasterEggNum++
            toast("再点${5 - clickContentEasterEggNum}次打开")
            if (clickContentEasterEggNum == 5) {
                clickContentEasterEggNum = 0
                dialog(
                    "info",
                    "A very good software that allows my recommendations to rotate\n" +
                            "love comes from china, LOL",
                    "OK",
                    "NO OK"
                )
            }
        }
        binding.aboutAppProduce.apply {
            setOnClickListener {
                openPage(
                    this@AboutActivity,
                    WebViewActivity::class.java,
                    false,
                    Bundle().apply {
                        putString("url", getUrl("info"))
                    }
                )
            }
        }
    }
}