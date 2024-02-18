package com.nagi.ddtools.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.nagi.ddtools.R
import com.nagi.ddtools.data.ProgressListener
import com.nagi.ddtools.utils.UiUtils.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * @projectName
 * @author :fmqwd
 * @description:
 * @date :2023/12/27 23:01
 */
object FileUtils {
    const val IDOL_LIST_FILE = "/data/idolList.json"
    const val IDOL_GROUP_FILE = "/data/idolGroupList.json"
    const val ACTIVITY_LIS_FILE = "/data/activityList.json"
    private const val TAG = "FileUtils"
    private const val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 114

    /**
     * 打开图片库，选择一张图片。
     * @param activity 当前活动。
     * @param resultLauncher 用于启动选择图片的Activity Result Launcher。
     */
    fun openImageGallery(activity: Activity, resultLauncher: ActivityResultLauncher<Intent>) {
        checkWriteExternalPermission(activity) { granted ->
            if (!granted) {
                activity.toast("您没有给予权限")
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
    fun moveImage(context: Context, uri: Uri, path: String): String? {
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
            LogUtils.e("Error moving image: " + e.message)
            null
        }
    }


    /**
     * 复制资源文件到指定的内部存储文件夹，如有需要则创建文件夹
     *
     * @param context 上下文
     * @param resourceId 资源文件的ID
     * @param outputFileName 输出文件的名称
     * @param outputFolderName 可选，输出文件夹的名称
     */
    fun copyRawResourceToFile(
        context: Context,
        resourceId: Int,
        outputFileName: String,
        outputFolderName: String? = null,
        overwrite: Boolean = false
    ) {
        val outputDirectory = if (outputFolderName != null) {
            val folder = File(context.filesDir, outputFolderName)
            if (!folder.exists()) {
                val success = folder.mkdirs()
                if (!success) {
                    LogUtils.e("Failed to create directory at ${folder.absolutePath}")
                    return
                }
            }
            folder
        } else {
            context.filesDir
        }
        val outputFile = File(outputDirectory, outputFileName)

        if (outputFile.exists() && !overwrite) {
            LogUtils.e("File already exists at ${outputFile.absolutePath} ")
            return
        }

        try {
            context.resources.openRawResource(resourceId).use { input ->
                FileOutputStream(outputFile, false).use { output ->
                    input.copyTo(output)
                }
            }
            Log.d(TAG, "File copied to internal storage at ${outputFile.absolutePath}")
        } catch (e: IOException) {
            Log.e(TAG, "Error copying file", e)
        }
    }

    /**
     * 检查应用目录下文件夹是否存在，如果不存在则创建。
     * @param path 目录的路径。
     * @return 返回文件夹
     */
    fun checkOrCreatePostureDirectory(path: String): File {
        val postureDir = File(path)
        if (!postureDir.exists()) {
            postureDir.mkdirs()
        }
        return postureDir
    }

    /**
     * 检查某个文件是否存在，不存在则返回false
     * @param directoryPath 目录的路径。
     * @param fileName 文件名。
     * @return 返回文件是否存在。
     */
    fun doesFileExist(directoryPath: String, fileName: String): Boolean {
        return try {
            val directory = File(directoryPath)
            if (!directory.exists() || !directory.isDirectory) {
                return false
            }
            directory.listFiles()?.any { it.name == fileName } == true
        } catch (e: SecurityException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 统计某个目录下的文件数量。
     * @param directoryPath 目录的路径。
     * @return 返回文件数量。
     */
    fun countFilesInDirectory(directoryPath: String): Int {
        return try {
            val directory = File(directoryPath)
            if (directory.exists() && directory.isDirectory) {
                directory.listFiles()?.count { it.isFile } ?: 0
            } else 0
        } catch (e: SecurityException) {
            0
        } catch (e: Exception) {
            0
        }
    }

    /**
     * 加载本地图片并且返回bitmap
     */
    fun loadBitmapFromPath(scope: CoroutineScope, path: String, callback: (Bitmap?) -> Unit) {
        scope.launch {
            try {
                val bitmap = withContext(Dispatchers.IO) {
                    val file = File(path)
                    if (file.exists()) BitmapFactory.decodeFile(path) else null
                }
                callback(bitmap)
            } catch (e: Exception) {
                Log.e("BitmapLoading", "Error loading bitmap", e)
                callback(null)
            }
        }
    }


    /**
     * 删除文件
     * @param fileOrDirectory 指定的文件
     * @return 是否成功
     */
    private fun deleteRecursively(fileOrDirectory: File): Boolean {
        if (fileOrDirectory.isDirectory) {
            val files = fileOrDirectory.listFiles()
            if (files != null) {
                for (child in files) {
                    deleteRecursively(child)
                }
            }
        }
        return fileOrDirectory.delete()
    }

    /**
     * 从路径删除文件/文件夹
     * @param path 指定路径
     * @return 是否成功
     */
    fun deleteWithPath(path: String): Boolean {
        val fileOrDirectory = File(path)
        return deleteRecursively(fileOrDirectory)
    }


    /**
     * 解压文件
     * @param zipFilePath 目录
     * @param outputFolderPath 解压后的文件
     */
    fun unzipFile(
        zipFilePath: String,
        outputFolderPath: String,
        progressListener: ProgressListener
    ): Boolean {
        try {
            val zipFile = File(zipFilePath)
            val zipSize = zipFile.length()
            var totalUnzippedBytes = 0L

            FileInputStream(zipFilePath).use { fis ->
                ZipInputStream(fis).use { zis ->
                    var zipEntry: ZipEntry? = zis.nextEntry
                    val buffer = ByteArray(4096)
                    while (zipEntry != null) {
                        val newFile = createFileForZipEntry(outputFolderPath, zipEntry)
                        if (!newFile.isDirectory) {
                            FileOutputStream(newFile).use { fos ->
                                var len: Int
                                while (zis.read(buffer).also { len = it } > 0) {
                                    fos.write(buffer, 0, len)
                                    totalUnzippedBytes += len
                                    val progress = (totalUnzippedBytes * 100 / zipSize).toInt()
                                    progressListener.onProgress(progress)
                                }
                            }
                        }
                        zipEntry = zis.nextEntry

                    }
                    zis.closeEntry()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    fun getDrawableForMedia(context: Context, type: String): Drawable? {
        return ContextCompat.getDrawable(
            context, when (type) {
                "weibo" -> R.drawable.ic_weibo
                "xiaohongshu" -> R.drawable.ic_xiaohongshu
                "douyin" -> R.drawable.ic_douyin
                "X" -> R.drawable.ic_twitter
                "qq" -> R.drawable.ic_qq
                "bili" -> R.drawable.ic_bili
                "youtube" -> R.drawable.ic_youtube
                else -> R.drawable.ic_weibo
            }
        )
    }


    @Throws(IOException::class)
    private fun createFileForZipEntry(outputFolderPath: String, zipEntry: ZipEntry): File {
        val outputFile = File(outputFolderPath, zipEntry.name)

        if (zipEntry.isDirectory) {
            if (!outputFile.isDirectory && !outputFile.mkdirs()) {
                throw IOException("Failed to create directory ${outputFile.path}")
            }
        } else {
            val parentDir = outputFile.parentFile
            if (parentDir != null) {
                if (!parentDir.isDirectory && !parentDir.mkdirs()) {
                    throw IOException("Failed to create directory ${parentDir.path}")
                }
            }
        }

        return outputFile
    }

    private fun getFileNameWithTimestamp(uri: Uri): String {
        val originalFileName = uri.lastPathSegment ?: "image"
        val extension = originalFileName.substringAfterLast('.', "png")
        val timestamp = System.currentTimeMillis()
        return "IMG_$timestamp.$extension"
    }
}