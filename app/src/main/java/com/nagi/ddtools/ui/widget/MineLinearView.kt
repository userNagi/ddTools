package com.nagi.ddtools.ui.widget

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.android.material.imageview.ShapeableImageView
import com.nagi.ddtools.R
import com.nagi.ddtools.utils.UiUtils.openPage
import com.nagi.ddtools.utils.UiUtils.openUrl

class MineLinearView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.widget_mine_linear, this, true)
    }

    fun setIcon(drawable: Drawable) {
        findViewById<ShapeableImageView>(R.id.linear_image).setImageDrawable(drawable)
    }

    fun setText(text: String) {
        findViewById<TextView>(R.id.linear_text).text = text
    }

    fun setBottomIcon(drawable: Drawable) {
        findViewById<ShapeableImageView>(R.id.linear_image_bottom).setImageDrawable(drawable)
    }

    fun setOnClickListener(activity: Activity, page: Class<*>) {
        context.openPage(activity, page)
    }
}