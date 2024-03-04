package com.nagi.ddtools.ui.widget

import android.app.Activity
import com.nagi.ddtools.utils.UiUtils.openPage

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat.getDrawable
import com.nagi.ddtools.R
import com.nagi.ddtools.databinding.WidgetMineLinearBinding

class MineLinearView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val binding: WidgetMineLinearBinding =
        WidgetMineLinearBinding.inflate(LayoutInflater.from(context), this, true)

    private fun setIcon(drawable: Int, color: String) {
        binding.linearImage.apply {
            setImageDrawable(getDrawable(context, drawable))
            when (color) {
                "red" -> setBackgroundColor(Color.RED)
                "green" -> setBackgroundColor(Color.parseColor("#006400"))
                "blue" -> setBackgroundColor(Color.BLUE)
                "black" -> setBackgroundColor(Color.BLACK)
                "yellow" -> setBackgroundColor(Color.YELLOW)
                "gray" -> setBackgroundColor(Color.GRAY)
                "lty" -> setBackgroundColor(Color.parseColor("#66ccff"))
            }
        }
    }

    private fun setText(text: String) {
        binding.linearText.text = text
    }

    private fun setBottomIcon(drawable: Int) {
        binding.linearImageBottom.setImageDrawable(getDrawable(context, drawable))
    }

    fun setOnClickListener(activity: Activity, page: Class<*>) {
        setOnClickListener {
            openPage(activity, page)
        }
    }

    fun setData(
        icon: Int,
        text: String,
        color: String = "",
        bottom: Int = R.drawable.baseline_arrow_forward
    ) {
        setIcon(icon, color)
        setText(text)
        setBottomIcon(bottom)
    }
}
