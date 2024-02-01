package com.nagi.ddtools.ui.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.view.setPadding
import com.google.android.material.imageview.ShapeableImageView
import com.nagi.ddtools.R
import com.nagi.ddtools.databinding.DialogImageShowBinding
import com.nagi.ddtools.utils.LogUtils
import com.nagi.ddtools.utils.NetImageLoader
import com.nagi.ddtools.utils.NetUtils.getBitmapFromURL
import com.nagi.ddtools.utils.UiUtils.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class NetImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ShapeableImageView(context, attrs, defStyleAttr) {

    private var currentLoadJob: Job? = null
    private val cacheDir = context.cacheDir

    fun setImageUrl(url: String, useSLT: Boolean = true) {
        currentLoadJob?.cancel()
        currentLoadJob = NetImageLoader.coroutineScope.launch {
            try {
                val basename = url.substringBeforeLast(".")
                val extension = url.substringAfterLast(".")

                val urlSLT = if (!useSLT) url else "${basename}_slt.$extension"


                val cachedBitmap = getCachedBitmap(urlSLT)
                val bmp = cachedBitmap ?: withContext(Dispatchers.IO) { getBitmapFromURL(urlSLT) }
                bmp?.let { bitmap ->
                    if (cachedBitmap == null) {
                        cacheBitmap(urlSLT, bitmap)
                    }
                    withContext(Dispatchers.Main) {
                        setImageBitmap(bitmap)
                    }
                } ?: run {
                    setImageResource(R.drawable.baseline_user_unlogin)
                }
            } catch (e: Exception) {
                LogUtils.e("Error loading image: ${e.message}")
                setImageResource(R.drawable.baseline_user_unlogin)
            }
        }
    }

    fun onCLick(url: String) {
        showPopupImage(context, url)
    }

    private fun getCachedBitmap(url: String): Bitmap? {
        val cacheFile = File(cacheDir, generateCacheFileName(url))
        return if (cacheFile.exists()) {
            BitmapFactory.decodeFile(cacheFile.path)
        } else null
    }

    private fun cacheBitmap(url: String, bitmap: Bitmap) {
        val cacheFile = File(cacheDir, generateCacheFileName(url))
        FileOutputStream(cacheFile).use { outStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
        }
    }

    private fun generateCacheFileName(url: String): String {
        return url.hashCode().toString()
    }

    private fun showPopupImage(context: Context, originalUrl: String) {
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupViewBinding = DialogImageShowBinding.inflate(LayoutInflater.from(context))
        val popupWindow = PopupWindow(
            popupViewBinding.root,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            true
        )
        popupViewBinding.popupImageView.setPadding(50)

        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            val cachedBitmap = getCachedBitmap(originalUrl)
            val bmp = cachedBitmap ?: withContext(Dispatchers.IO) {
                getBitmapFromURL(originalUrl)
            }
            bmp?.let { bitmap ->
                if (cachedBitmap == null) {
                    withContext(Dispatchers.IO) {
                        cacheBitmap(originalUrl, bitmap)
                    }
                }
                popupViewBinding.popupImageView.setImageBitmap(bitmap)
            } ?: run {
                popupViewBinding.popupImageView.setImageResource(R.drawable.baseline_user_unlogin)
            }
        }

        popupViewBinding.root.setOnClickListener {
            popupWindow.dismiss()
        }
        popupViewBinding.popupImageView.setOnLongClickListener {
            saveImageToGallery(context, originalUrl)
            true
        }
        popupWindow.showAtLocation(this, Gravity.CENTER, 0, 0)
    }

    private fun saveImageToGallery(context: Context, imageUrl: String) {
        context.toast("已保存至相册")
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        currentLoadJob?.cancel()
    }
}