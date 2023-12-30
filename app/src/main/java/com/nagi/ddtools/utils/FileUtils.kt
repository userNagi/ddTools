package com.nagi.ddtools.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import com.nagi.ddtools.utils.UiUtils.toast
import java.io.File
import java.io.FileOutputStream


/**
 * @projectName
 * @author :fmqwd
 * @description:
 * @date :2023/12/27 23:01
 */
object FileUtils {
    private const val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 114

    /**
     * 打开图片库，选择一张图片。
     * @param activity 当前活动。
     * @param resultLauncher 用于启动选择图片的Activity Result Launcher。
     */
    fun openImageGallery(activity: Activity, resultLauncher: ActivityResultLauncher<Intent>) {
        checkWriteExternalPermission(activity) { granted ->
            if (!granted) {
                toast(activity, "您没有给予权限")
                return@checkWriteExternalPermission
            }
            val intent = Intent(Intent.ACTION_PICK).setType("image/*")
            resultLauncher.launch(intent)
        }
    }

    /**
     * 检查写入外部存储的权限。
     */
    private fun checkWriteExternalPermission(activity: Activity, callback: (Boolean) -> Unit) {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_EXTERNAL_STORAGE_REQUEST_CODE
            )
        } else {
            callback(true)
        }
    }

    /**
     * 移动图片到本地且返回
     */
    fun moveImageIntoAppFile(context: Context, uri: Uri, path: String): String? {
        return try {
            val appDir = context.filesDir
            val targetDir = File(appDir, path).apply {
                if (!exists()) mkdirs()
            }
            val targetFile = File(targetDir, getFileNameWithTimestamp(uri))
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(targetFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            targetFile.path
        } catch (e: Exception) {
            Log.e("Error moving image: ", e.message.toString(), e)
            null
        }
    }
    private fun getFileNameWithTimestamp(uri: Uri): String {
        val originalFileName = uri.lastPathSegment ?: "image"
        val extension = originalFileName.substringAfterLast('.', "png")
        val timestamp = System.currentTimeMillis()
        return "IMG_$timestamp.$extension"
    }
}