package com.nagi.ddtools.utils

import android.content.Context
import android.content.pm.PackageManager
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.IOException
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
        callback: (Boolean, String?) -> Unit
    ) {
        val request = createRequest(url, method, params)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, "请求失败: ${e.localizedMessage}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        val responseBody = it.body?.string()
                        callback(true, responseBody)
                    } else {
                        callback(false, "响应失败: HTTP ${it.code}")
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
        callback: (Boolean, String) -> Unit
    ) {
        fetch(url, method, params) { success, result ->
            if (success && result != null) {
                saveToFile(result, internalPath, callback)
            } else {
                callback(false, result ?: "Unknown Error")
            }
        }
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
                params.forEach { (key, value) -> httpUrlBuilder.addQueryParameter(key, value) }
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

}
