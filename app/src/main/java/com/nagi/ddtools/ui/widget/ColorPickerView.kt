package com.nagi.ddtools.ui.widget

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import android.util.SparseArray
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.nagi.ddtools.R

class ColorPickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    isSingleSelect: Boolean = false
) : LinearLayout(context, attrs, defStyleAttr) {

    private val colorViews = SparseArray<ImageView>()
    private val colorValues = SparseIntArray()
    private var customColor: Int = Color.TRANSPARENT
    private var isMultiSelectEnabled: Boolean = !isSingleSelect

    init {
        orientation = HORIZONTAL
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.widget_color_choose, this, true)

        val colorOptionIDs = listOf(
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
            colorOption.apply {
                addView(checkMark)
                setBackgroundColor(color)
                setOnClickListener { handleColorOptionClick(it, checkMark) }
            }

            colorViews.put(colorOptionIDs[index], checkMark)
            colorValues.put(colorOptionIDs[index], color)
        }

        findViewById<FrameLayout>(R.id.colorCustom).setOnClickListener {
            showColorPickerDialog()
            notifyColorSelectionChanged()
        }
    }

    private fun handleColorOptionClick(view: View, checkMark: ImageView) {
        view.isSelected = !view.isSelected
        checkMark.visibility = if (view.isSelected) VISIBLE else INVISIBLE
        if (!isMultiSelectEnabled && view.isSelected) {
            for (i in 0 until colorViews.size()) {
                if (colorViews.keyAt(i) != view.id) {
                    val otherView = findViewById<View>(colorViews.keyAt(i))
                    otherView.isSelected = false
                    colorViews.get(colorViews.keyAt(i)).visibility = INVISIBLE
                }
            }
        }
        notifyColorSelectionChanged()
    }

    private fun notifyColorSelectionChanged() {
        val selectedColors = (0 until colorViews.size())
            .filter { findViewById<View>(colorViews.keyAt(it)).isSelected }
            .map { colorValues.get(colorViews.keyAt(it)) }
            .toMutableList()

        if (customColor != Color.TRANSPARENT) {
            selectedColors.add(customColor)
        }
    }

    fun setText(text: String = "颜色选择：") {
        findViewById<TextView>(R.id.colorOptionText).text = text
    }

    fun setIsSingleCheck(isSingleCheck: Boolean) {
        isMultiSelectEnabled = !isSingleCheck
    }
    private fun showColorPickerDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_color_picker, null)
        val colorView = dialogView.findViewById<View>(R.id.color_view)
        val colorInput = dialogView.findViewById<EditText>(R.id.color_input)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        val confirmButton = dialogView.findViewById<Button>(R.id.confirm_button)
        val configAndPushBtn = dialogView.findViewById<Button>(R.id.confirm_button_sure)
        var colorCode = ""
        confirmButton.setOnClickListener {
            colorCode = colorInput.text.toString()
            if (!TextUtils.isEmpty(colorCode) && colorCode.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$".toRegex())) {
                try {
                    customColor = Color.parseColor(colorCode)
                    colorView.setBackgroundColor(Color.parseColor(colorCode))
                    confirmButton.setBackgroundColor(Color.parseColor(colorCode))
                    configAndPushBtn.setBackgroundColor(Color.parseColor(colorCode))
                } catch (e: Exception) {
                    colorInput.error = "颜色编码不合法"
                }
            } else {
                colorInput.error = "颜色编码不合法"
            }
        }
        configAndPushBtn.setOnClickListener {
            if (colorCode.isNotEmpty()) {
                customColor = Color.parseColor(colorCode)
                dialog.dismiss()
            }
        }
        dialog.show()
    }


    fun getSelectedColors(): List<Int> {
        return (0 until colorViews.size())
            .filter { findViewById<View>(colorViews.keyAt(it)).isSelected }
            .map { colorValues.get(colorViews.keyAt(it)) }
            .toMutableList()
            .apply {
                if (customColor != Color.TRANSPARENT) add(customColor)
            }
    }
}
