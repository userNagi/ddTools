package com.nagi.ddtools.ui.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.nagi.ddtools.R

class ColorPickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val colorOptions: MutableMap<FrameLayout, ImageView> = mutableMapOf()
    private val colorValues: MutableMap<FrameLayout, Int> = mutableMapOf()
    private var customColor: Int = Color.TRANSPARENT
    var onColorSelectedListener: ((List<Int>) -> Unit)? = null

    init {
        orientation = HORIZONTAL
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.widget_color_choose, this, true)

        val colorOptionIDs =
            listOf(
                R.id.colorOption1,
                R.id.colorOption2,
                R.id.colorOption3,
                R.id.colorOption4,
                R.id.colorOption5,
                R.id.colorOption6,
                R.id.colorOption7
            )

        val colors = listOf(
            Color.RED,
            Color.DKGRAY,
            Color.CYAN,
            Color.GREEN,
            Color.BLUE,
            Color.MAGENTA,
            Color.WHITE
        )
        colors.forEachIndexed { index, color ->
            val colorOption = findViewById<FrameLayout>(colorOptionIDs[index])
            val checkMark = ImageView(context).apply {
                setImageResource(R.drawable.ic_baseline_check)
                visibility = INVISIBLE
            }
            colorOption.addView(checkMark)
            colorOption.setBackgroundColor(color)
            colorOptions[colorOption] = checkMark
            colorValues[colorOption] = color
            colorOption.setOnClickListener {
                val isSelected = it.isSelected
                checkMark.visibility = if (isSelected) INVISIBLE else VISIBLE
                it.isSelected = !isSelected
                notifyColorSelectionChanged()
            }
        }

        val customColorOption = findViewById<FrameLayout>(R.id.colorCustom)
        customColorOption.setOnClickListener {
            notifyColorSelectionChanged()
        }
    }

    private fun notifyColorSelectionChanged() {
        val selectedColors =
            colorOptions.filter { it.key.isSelected }.mapNotNull { colorValues[it.key] }
        if (customColor != Color.TRANSPARENT) {
            selectedColors.toMutableList().add(customColor)
        }
        onColorSelectedListener?.invoke(selectedColors)
    }

    fun setText(text: String = "颜色选择：") {
        findViewById<TextView>(R.id.colorOptionText).text = text
    }

    fun getSelectedColors(): List<Int> {
        return colorOptions.filter { it.key.isSelected }
            .mapNotNull { colorValues[it.key] } + if (customColor != Color.TRANSPARENT) listOf(
            customColor
        ) else emptyList()
    }
}
