package com.nagi.ddtools.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

    /**
     ** 打开图片库，选择一张图片
     **/
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
     ** 权限检查
     **/
    private fun checkWriteExternalPermission(activity: Activity, callback: (Boolean) -> Unit) {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                114
            )
        } else {
            callback(true)
        }
    }

    /**
     ** 移动图片到本地且返回
     **/
    fun moveImageIntoAppFile(context: Context, uri: Uri, path: String): String? {
        try {
            val appDir = context.filesDir
            val targetDir = File(appDir, path)
            if (!targetDir.exists()) {
                targetDir.mkdirs()
            }
            val targetFile = File(targetDir, getFileNameWithTimestamp(uri))
            val outputStream = FileOutputStream(targetFile)
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.copyTo(outputStream)
            }
            outputStream.flush()
            outputStream.close()
            return targetFile.path
        } catch (e: Exception) {
            Log.e("Error moving image: ", e.message, e)
        }
        return null
    }

    private fun getFileNameWithTimestamp(uri: Uri): String {
        val lastSegment = uri.path?.split(".")?.lastOrNull() ?: ".png"
        val timestamp = System.currentTimeMillis()
        return "$timestamp.$lastSegment"
    }
}