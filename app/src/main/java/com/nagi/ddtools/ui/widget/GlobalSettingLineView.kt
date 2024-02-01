package com.nagi.ddtools.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.nagi.ddtools.databinding.WidgetGlobalSettingLineBinding

class GlobalSettingLineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    val binding = WidgetGlobalSettingLineBinding.inflate(LayoutInflater.from(context), this, true)

    fun setData(left: String, right: String = "") {
        binding.apply {
            settingLeftText.text = left
            settingRightText.text = right
        }
    }
}