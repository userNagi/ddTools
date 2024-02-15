package com.nagi.ddtools.ui.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import com.nagi.ddtools.R
import com.nagi.ddtools.data.MediaList
import androidx.core.content.ContextCompat
import com.nagi.ddtools.databinding.WidgetMediaSpinnerBinding
import com.nagi.ddtools.utils.FileUtils

class MediaSpinnerAndInput @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val binding: WidgetMediaSpinnerBinding =
        WidgetMediaSpinnerBinding.inflate(LayoutInflater.from(context), this, true)
    private val mediaItems = arrayOf("微博", "小红书", "抖音", "X", "QQ", "B站", "YouTube")
    private val mediaType = arrayOf("weibo", "xiaohongshu", "douyin", "X", "qq", "bili", "youtube")
    private var selectedMediaIndex: Int = 0
    private var nameGet = ""

    init {
        binding.spinner.apply {
            adapter =
                ArrayAdapter(context, android.R.layout.simple_spinner_item, mediaItems).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedMediaIndex = position
                    val drawableId = when (position) {
                        0 -> R.drawable.ic_weibo
                        1 -> R.drawable.ic_xiaohongshu
                        2 -> R.drawable.ic_douyin
                        3 -> R.drawable.ic_twitter
                        4 -> R.drawable.ic_qq
                        5 -> R.drawable.ic_bili
                        6 -> R.drawable.ic_youtube
                        else -> 0
                    }
                    setIcon(ContextCompat.getDrawable(context, drawableId))
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun setIcon(drawable: Drawable?) {
        binding.icon.setImageDrawable(drawable)
    }

    fun setData(mediaList: MediaList) {
        nameGet = mediaList.name
        binding.spinner.setSelection(
            when (mediaList.type) {
                "weibo" -> 0
                "xiaohongshu" -> 1
                "douyin" -> 2
                "X" -> 3
                "qq" -> 4
                "bili" -> 5
                "youtube" -> 6
                else -> 0
            }
        )
        binding.name.setText(mediaList.name)
        binding.input.setText(mediaList.url)
        binding.icon.setImageDrawable(FileUtils.getDrawableForMedia(context, mediaList.type))
    }

    fun getData(name: String): MediaList? {
        binding.input.apply {
            return if (text.isNullOrEmpty()) null
            else MediaList(
                binding.name.text.toString().ifEmpty { "$name${mediaItems[selectedMediaIndex]}" },
                text.toString(),
                mediaType[selectedMediaIndex]
            )

        }
    }
}