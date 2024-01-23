package com.nagi.ddtools.ui.widget

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.google.android.material.imageview.ShapeableImageView
import com.nagi.ddtools.R
import com.nagi.ddtools.utils.UiUtils.openPage

class MineLinearView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.widget_mine_linear, this, true)
    }

    private fun setIcon(drawable: Int,color: String) {
        findViewById<ShapeableImageView>(R.id.linear_image).apply {
            setImageDrawable(
                getDrawable(context, drawable)
            )
            when (color) {
                "red" -> setBackgroundColor(Color.RED)
                "green" -> setBackgroundColor(Color.GREEN)
                "blue" -> setBackgroundColor(Color.BLUE)
                "black" -> setBackgroundColor(Color.BLACK)
                "yellow" -> setBackgroundColor(Color.YELLOW)
                "gray" -> setBackgroundColor(Color.GRAY)
                "lty" -> setBackgroundColor(Color.parseColor("#66ccff"))
            }
        }
    }

    private fun setText(text: String) {
        findViewById<TextView>(R.id.linear_text).text = text
    }

    private fun setBottomIcon(drawable: Int) {
        findViewById<ShapeableImageView>(R.id.linear_image_bottom).setImageDrawable(
            getDrawable(context, drawable)
        )
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
        setIcon(icon,color)
        setText(text)
        setBottomIcon(bottom)

    }
}