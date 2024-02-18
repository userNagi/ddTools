package com.nagi.ddtools.ui.widget.dialog

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.nagi.ddtools.database.idolList.IdolTag
import com.nagi.ddtools.databinding.DialogTagChooseBinding
import com.nagi.ddtools.utils.UiUtils.dpToPx

class IdolTagDialog(
    context: Context,
    private val data: List<IdolTag>
) : Dialog(context) {

    private val binding = DialogTagChooseBinding.inflate(layoutInflater)
    private var textColorEnd = ""
    private var backColorEnd = ""

    interface Callback {
        fun onConfirm(selectedTag: IdolTag)
        fun onCancel()
    }

    var callback: Callback? = null

    init {
        setContentView(
            binding.root,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        binding.apply {
            chooseTag.adapter = data.map { it.text }.toSpinnerAdapter(context)
            chooseTag.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    val info = data[position]
                    val background = GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        cornerRadius = binding.root.context.dpToPx(4f)
                        color = ColorStateList.valueOf(Color.parseColor(info.backColor))
                    }
                    text.setText(info.text)
                    textColor.setText(info.textColor.removePrefix("#"))
                    backColor.setText(info.backColor.removePrefix("#"))
                    textColorSrc.setImageDrawable(ColorDrawable(Color.parseColor(info.textColor)))
                    backColorSrc.setImageDrawable(ColorDrawable(Color.parseColor(info.backColor)))
                    end.text = info.text
                    end.setTextColor(Color.parseColor(info.textColor))
                    end.background = background
                }


                override fun onNothingSelected(parent: AdapterView<*>?) {
                    chooseTag.setSelection(0)
                }
            }
            textColor.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, st: Int, co: Int, af: Int) {}
                override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    s?.let { it ->
                        val text = it.toString()
                        val regex = "^([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$".toRegex()
                        if (regex.matches(text)) {
                            val colorStr = when (text.length) {
                                3 -> "#" + text.map { "$it$it" }.joinToString("")
                                6 -> "#${text}"
                                else -> return
                            }
                            kotlin.runCatching {
                                val color = Color.parseColor(colorStr)
                                textColorSrc.setImageDrawable(ColorDrawable(color))
                                end.setTextColor(color)
                                textColorEnd = colorStr
                                textColor
                            }
                        }

                    }
                }
            })
            backColor.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, st: Int, co: Int, af: Int) {}
                override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    s?.let { it ->
                        val text = it.toString()
                        val regex = "^([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$".toRegex()
                        if (regex.matches(text)) {
                            val colorStr = when (text.length) {
                                3 -> "#" + text.map { "$it$it" }.joinToString("")
                                6 -> "#${text}"
                                else -> return
                            }
                            kotlin.runCatching {
                                val color = Color.parseColor(colorStr)
                                val background = GradientDrawable().apply {
                                    shape = GradientDrawable.RECTANGLE
                                    cornerRadius = binding.root.context.dpToPx(4f)
                                }.apply { setColor(color) }
                                backColorSrc.setImageDrawable(ColorDrawable(color))
                                end.background = background
                                backColorEnd = colorStr
                                backColor
                            }
                        }

                    }
                }
            })
            text.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}

                override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    end.text = s
                }
            })

        }


        binding.confirmButton.setOnClickListener {
            val selectedTag = IdolTag(
                labelID = 0,
                text = binding.text.text.toString(),
                textColor = textColorEnd,
                backColor = backColorEnd,
                other = null
            )
            callback?.onConfirm(selectedTag)
            dismiss()
        }

        binding.cancelButton.setOnClickListener {
            callback?.onCancel()
            dismiss()
        }
    }

    private fun Collection<String>.toSpinnerAdapter(context: Context): ArrayAdapter<String> {
        return ArrayAdapter(context, android.R.layout.simple_spinner_item, this.toList()).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }
}
