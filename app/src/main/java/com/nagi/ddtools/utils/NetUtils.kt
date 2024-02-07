package com.nagi.ddtools.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import com.nagi.ddtools.data.ProgressListener
import com.nagi.ddtools.data.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object NetUtils {

    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS) // 连接超时设置为10秒
            .readTimeout(30, TimeUnit.SECONDS)    // 读取超时设置为30秒
            .writeTimeout(15, TimeUnit.SECONDS)   // 写入超时设置为15秒
            .retryOnConnectionFailure(true)       // 设置为对连接失败进行重试
            .build()
    }


    enum class HttpMethod {
        GET, POST
    }

    // 执行网络请求
    fun fetch(
        url: String,
        method: HttpMethod,
        params: Map<String, String>,
        callback: (Resource<String>) -> Unit
    ) {
        val request = createRequest(url, method, params)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Resource.Error("请求失败: ${e.localizedMessage}"))
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        val responseBody = it.body?.string()
                        val json = JSONObject(responseBody!!)
                        val status = json.optString("status")
                        val message = json.optString("message")

                        if (status == "success") {
                            LogUtils.e(message)
                            callback(Resource.Success(message))
                        } else {
                            LogUtils.e(message)
                            LogUtils.e(params.toString())
                            callback(Resource.Error(message ?: "未知错误"))
                        }
                    } else {
                        callback(Resource.Error("响应失败: HTTP ${it.code}"))
                    }
                }
            }
        })
    }

    // 执行网络请求并将结果保存到文件
    fun fetchAndSave(
        url: String,
        method: HttpMethod,
        params: Map<String, String>,
        internalPath: String,
        callback: (Resource<String>) -> Unit
    ) {
        fetch(url, method, params) { resource ->
            when (resource) {
                is Resource.Success -> {
                    saveToFile(resource.data, internalPath) { success, message ->
                        if (success) {
                            callback(Resource.Success(message))
                        } else {
                            callback(Resource.Error(message))
                        }
                    }
                }

                is Resource.Error -> callback(resource)
            }
        }
    }

    //上传文件
    fun uploadImage(
        context: Context,
        url: String,
        uri: Uri,
        type: String,
        callback: (Resource<String>) -> Unit
    ) {
        // 检测是否为支持的图片类型
        val mimeType =
            uriToMimeType(context, uri) ?: return callback(Resource.Error("不支持的文件类型"))
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val tempFile = createTempFile(context)
            tempFile.outputStream().use {
                inputStream.copyTo(it)
            }

            val fileBody = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", tempFile.name, fileBody)
                .addFormDataPart("type", type) // 添加描述部分
                .build()

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    tempFile.delete()
                    callback(Resource.Error("请求失败: ${e.localizedMessage}"))
                }

                override fun onResponse(call: Call, response: Response) {
                    tempFile.delete()
                    response.use {
                        if (it.isSuccessful) {
                            val responseBody = it.body?.string()
                            val json = JSONObject(responseBody!!)
                            val status = json.optString("status")
                            val message = json.optString("message")
                            if (status == "success") {
                                callback(Resource.Success(message))
                            } else {
                                callback(Resource.Error(message ?: "未知错误"))
                            }
                        } else {
                            callback(Resource.Error("响应失败: HTTP ${it.code}"))
                        }
                    }
                }
            })
        }

    }

    private fun createTempFile(context: Context): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.externalCacheDir
        return File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
    }

    private fun uriToMimeType(context: Context, uri: Uri): String? {
        return context.contentResolver.getType(uri)
    }

    // 根据请求类型创建Request对象
    private fun createRequest(
        url: String,
        method: HttpMethod,
        params: Map<String, String>
    ): Request {
        val builder = Request.Builder()
        when (method) {
            // POST请求时，构建FormBody并添加参数
            HttpMethod.POST -> {
                val formBodyBuilder = FormBody.Builder()
                params.forEach { (key, value) -> formBodyBuilder.add(key, value) }
                builder.url(url)
                builder.post(formBodyBuilder.build())
            }
            // GET请求时，构建HttpUrl并添加查询参数
            HttpMethod.GET -> {
                val httpUrlBuilder = url.toHttpUrlOrNull()?.newBuilder()
                    ?: throw IllegalArgumentException("Invalid URL: $url")
                params.forEach { (key, value) ->
                    httpUrlBuilder.addQueryParameter(
                        key,
                        value
                    )
                }
                builder.url(httpUrlBuilder.build())
            }
        }
        return builder.build()
    }

    // 获取应用的本地版本信息
    fun getLocalVersion(context: Context): String {
        return try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            LogUtils.e("Failed to get app version: ${e.localizedMessage}")
            "1.0"
        }
    }

    // 将数据保存到文件
    private fun saveToFile(
        data: String,
        internalPath: String,
        callback: (Boolean, String) -> Unit
    ) {
        try {
            File(internalPath).apply {
                parentFile?.mkdirs()
                writeText(data)
            }
            callback(true, "Data successfully saved to $internalPath.")
        } catch (e: IOException) {
            callback(false, "Failed to save data: ${e.localizedMessage}")
        }
    }

    //下载并保存文件
    fun downloadFileWithProgress(
        downloadUrl: String,
        directoryPath: String,
        progressListener: ProgressListener,
        callback: (String?, Exception?) -> Unit
    ) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(downloadUrl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback(null, IOException("Unexpected code $response"))
                    return
                }

                val responseBody = response.body
                val contentLength = responseBody?.contentLength() ?: -1
                var bytesRead: Long = 0
                val buffer = ByteArray(2048)
                val inputStream: InputStream? = responseBody?.byteStream()
                val file = File(directoryPath, File(downloadUrl).name)

                val directory = File(directoryPath)
                if (!directory.exists()) {
                    directory.mkdirs()
                }

                try {
                    val outputStream = FileOutputStream(file)
                    var read: Int
                    while (inputStream!!.read(buffer).also { read = it } != -1) {
                        bytesRead += read
                        outputStream.write(buffer, 0, read)
                        val progress =
                            if (contentLength > 0) (bytesRead * 100 / contentLength).toInt() else -1
                        progressListener.onProgress(progress)
                    }
                    outputStream.flush()
                    outputStream.close()
                    inputStream.close()

                    callback(file.absolutePath, null)
                } catch (e: IOException) {
                    callback(null, e)
                }
            }
        })
    }


    suspend fun getBitmapFromURL(urlString: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val imgUrl = URL(urlString)
                val conn = imgUrl.openConnection().apply {
                    connectTimeout = 6000
                    readTimeout = 6000
                    doInput = true
                    useCaches = true
                }
                conn.connect()
                conn.getInputStream().use { input ->
                    BitmapFactory.decodeStream(input)
                }
            } catch (e: Exception) {
                LogUtils.e("Error getting bitmap from URL: ${e.message}")
                null
            }
        }
    }
}

