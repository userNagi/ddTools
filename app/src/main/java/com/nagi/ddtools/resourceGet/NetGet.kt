package com.nagi.ddtools.resourceGet

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.nagi.ddtools.data.Resource
import com.nagi.ddtools.data.UpdateInfo
import com.nagi.ddtools.database.user.User
import com.nagi.ddtools.utils.DataUtils
import com.nagi.ddtools.utils.FileUtils.ACTIVITY_LIS_FILE
import com.nagi.ddtools.utils.FileUtils.IDOL_GROUP_FILE
import com.nagi.ddtools.utils.LogUtils
import com.nagi.ddtools.utils.NetUtils
import java.io.File

object NetGet {
    private const val ROOT_URL = "https://wiki.chika-idol.live/request/ddtools/"
    private const val INFO_URL = "https://info.chika-idol.live/"
    private const val IDOL_GROUP_LIST_URL = "getChikaIdolList.php/"
    private const val ACTIVITY_LIST_URL = "getActivity.php/"
    private const val CHECK_UPDATE_URL = "getUpdateInfo.php/"
    private const val USER_LOGIN = "userLogin.php/"
    private const val USER_REGISTER = "registerUser.php/"
    fun getIdolGroupList(context: Context) {
        NetUtils.fetchAndSave(
            ROOT_URL + IDOL_GROUP_LIST_URL,
            NetUtils.HttpMethod.POST,
            emptyMap(),
            File(context.filesDir, IDOL_GROUP_FILE).path
        ) { resource ->
            if (resource is Resource.Error) {
                LogUtils.e("Failed to fetch idol group list: ${resource.message}")
            }
        }
    }

    fun getActivityList(context: Context) {
        NetUtils.fetchAndSave(
            ROOT_URL + ACTIVITY_LIST_URL,
            NetUtils.HttpMethod.POST,
            emptyMap(),
            File(context.filesDir, ACTIVITY_LIS_FILE).path
        ) { resource ->
            if (resource is Resource.Error) {
                LogUtils.e("Failed to fetch activity list: ${resource.message}")
            }
        }
    }

    fun getUpdateDetails(callback: (Resource<UpdateInfo>) -> Unit) {
        NetUtils.fetch(
            ROOT_URL + CHECK_UPDATE_URL,
            NetUtils.HttpMethod.GET,
            emptyMap()
        ) { resource ->
            when (resource) {
                is Resource.Success -> {
                    try {
                        val itemType = object : TypeToken<UpdateInfo>() {}.type
                        val updateInfo: UpdateInfo = Gson().fromJson(resource.data, itemType)
                        callback(Resource.Success(updateInfo))
                    } catch (e: JsonSyntaxException) {
                        LogUtils.e("UpdateInfo: JSON parsing error: ${e.localizedMessage}")
                        callback(Resource.Error("JSON parsing error: ${e.localizedMessage}"))
                    }
                }

                is Resource.Error -> {
                    LogUtils.e("UpdateInfo: Failed to fetch update details: ${resource.message}")
                    callback(Resource.Error("Failed to fetch update details: ${resource.message}"))
                }
            }
        }
    }

    fun userLogin(callback: (Resource<User>) -> Unit, username: String, password: String) {
        NetUtils.fetch(
            ROOT_URL + USER_LOGIN,
            NetUtils.HttpMethod.POST,
            mapOf(
                "username" to username,
                "password" to DataUtils.hashPassword(username, password)
            )
        ) { resource ->
            if (resource is Resource.Success) {
                try {
                    val userInfo = Gson().fromJson(resource.data, User::class.java)
                    callback(Resource.Success(userInfo))
                } catch (e: JsonSyntaxException) {
                    callback(Resource.Error("JSON parsing error: ${e.localizedMessage}"))
                }
            }
            if (resource is Resource.Error) {
                callback(Resource.Error("登录失败：${resource.message}"))
            }
        }
    }

    fun userRegister(
        callback: (Resource<String>) -> Unit,
        username: String,
        password: String,
        email: String,
        nickname: String,
        avatarUrl: String? = null,
        bio: String? = null,
        role: String? = null,
        experience: Int? = null,
        status: String? = null,
        ext: Map<String, Any>? = null
    ) {
        val requestBody = mutableMapOf(
            "username" to username,
            "password" to DataUtils.hashPassword(username, password),
            "email" to email,
            "nickname" to nickname
        )
        avatarUrl?.let { requestBody["avatar_url"] = it }
        bio?.let { requestBody["bio"] = it }
        role?.let { requestBody["role"] = it }
        experience?.let { requestBody["experience"] = it.toString() }
        status?.let { requestBody["status"] = it }
        ext?.let { requestBody["ext"] = Gson().toJson(it) }

        NetUtils.fetch(
            ROOT_URL + USER_REGISTER,
            NetUtils.HttpMethod.POST,
            requestBody
        ) { resource ->
            when (resource) {
                is Resource.Success -> callback(Resource.Success("注册成功！"))
                is Resource.Error -> callback(Resource.Error(resource.message))
            }
        }
    }

    fun getUrl(url: String): String =
        when (url) {
            "info" -> INFO_URL
            "login" -> ROOT_URL + USER_LOGIN
            else -> ""
        }
}