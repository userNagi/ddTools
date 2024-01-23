package com.nagi.ddtools.ui.widget

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.imageview.ShapeableImageView
import com.nagi.ddtools.R
import com.nagi.ddtools.utils.LogUtils
import com.nagi.ddtools.utils.NetImageLoader
import com.nagi.ddtools.utils.NetUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NetImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ShapeableImageView(context, attrs, defStyleAttr) {

    private var currentLoadJob: Job? = null

    fun setImageUrl(url: String) {
        currentLoadJob?.cancel()
        currentLoadJob = NetImageLoader.coroutineScope.launch {
            try {
                val bmp = withContext(Dispatchers.IO) { NetUtils.getBitmapFromURL(url) }
                bmp?.let { bitmap ->
                    setImageBitmap(bitmap)
                } ?: run {
                    setImageResource(R.drawable.baseline_user_unlogin)
                }
            } catch (e: Exception) {
                LogUtils.e("Error loading image: ${e.message}")
                setImageResource(R.drawable.baseline_user_unlogin)
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        currentLoadJob?.cancel()
    }
}

