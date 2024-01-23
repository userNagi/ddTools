package com.nagi.ddtools.ui.minepage.about

import android.os.Bundle
import com.nagi.ddtools.R
import com.nagi.ddtools.databinding.ActivityAboutBinding
import com.nagi.ddtools.resourceGet.NetGet.getUrl
import com.nagi.ddtools.ui.base.DdToolsBindingBaseActivity
import com.nagi.ddtools.ui.base.WebViewActivity
import com.nagi.ddtools.utils.NetUtils
import com.nagi.ddtools.utils.UiUtils.dialog
import com.nagi.ddtools.utils.UiUtils.openPage
import com.nagi.ddtools.utils.UiUtils.toast

class AboutActivity : DdToolsBindingBaseActivity<ActivityAboutBinding>() {
    private var clickContentEasterEggNum = 0
    override fun createBinding(): ActivityAboutBinding {
        return ActivityAboutBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            aboutAppVersion.text = NetUtils.getLocalVersion(applicationContext)
            titleInclude.titleText.text = getText(R.string.about_me)
            titleInclude.titleBack.setOnClickListener { finish() }
            aboutAppContent.setOnClickListener {
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
            aboutAppProduce.apply {
                setOnClickListener {
                    openPage(this@AboutActivity,
                        WebViewActivity::class.java, false,
                        Bundle().apply {
                            putString("url", getUrl("info"))
                            putString("title", "贡献者列表")
                        }
                    )
                }
            }
        }
    }
}