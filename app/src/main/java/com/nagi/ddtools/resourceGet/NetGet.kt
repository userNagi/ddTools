package com.nagi.ddtools.resourceGet

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.nagi.ddtools.data.Resource
import com.nagi.ddtools.data.UpdateInfo
import com.nagi.ddtools.database.activityList.ActivityList
import com.nagi.ddtools.database.idolGroupList.IdolGroupList
import com.nagi.ddtools.database.user.User
import com.nagi.ddtools.utils.DataUtils
import com.nagi.ddtools.utils.FileUtils.ACTIVITY_LIS_FILE
import com.nagi.ddtools.utils.FileUtils.IDOL_GROUP_FILE
import com.nagi.ddtools.utils.FileUtils.IDOL_LIST_FILE
import com.nagi.ddtools.utils.LogUtils
import com.nagi.ddtools.utils.NetUtils
import java.io.File

object NetGet {
    private const val ROOT_URL = "https://wiki.chika-idol.live/request/ddtools/"
    private const val INFO_URL = "https://info.chika-idol.live/"
    private const val IDOL_GROUP_LIST_URL = "getChikaIdolList.php/"
    private const val IDOL_LIST_URL = "getIdolList.php/"
    private const val ACTIVITY_LIST_URL = "getActivity.php/"
    private const val CHECK_UPDATE_URL = "getUpdateInfo.php/"
    private const val USER_LOGIN = "userLogin.php/"
    private const val USER_REGISTER = "registerUser.php/"
    private const val USER_FEEDBACK = "userFeedback.php/"
    private const val USER_EVALUATE = "updateTags.php/"
    private const val GET_TAGS_URL = "getTags.php/"
    private const val UPDATE_GROUP_INFO = "upDateGroupList.php/"
    private const val UPLOAD_IMG = "uploadImg.php/"
    private const val GET_IDOL_TAG = "getIdolTag.php/"
    private const val INSERT_ACTIVITY_INFO = "insertActivity.php/"

    fun getIdolGroupList(context: Context) {
        NetUtils.fetchAndSave(
            ROOT_URL + IDOL_GROUP_LIST_URL,
            NetUtils.HttpMethod.POST,
            emptyMap(),
            File(context.filesDir, IDOL_GROUP_FILE).path
        ) {
            if (it is Resource.Error) LogUtils.e("Failed to fetch idol group list: ${it.message}")
        }
    }

    fun getIdolList(context: Context) {
        NetUtils.fetchAndSave(
            ROOT_URL + IDOL_LIST_URL,
            NetUtils.HttpMethod.POST,
            emptyMap(),
            File(context.filesDir, IDOL_LIST_FILE).path
        ) {
            if (it is Resource.Error) LogUtils.e("Failed to fetch idol group list: ${it.message}")
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
        nickname: String
    ) {
        val requestBody = mutableMapOf(
            "username" to username,
            "password" to DataUtils.hashPassword(username, password),
            "email" to email,
            "nickname" to nickname
        )
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

    fun getTags(
        type: String,
        typeId: String,
        userId: Int? = 0,
        callback: (Resource<String>) -> Unit
    ) {
        NetUtils.fetch(
            ROOT_URL + GET_TAGS_URL,
            NetUtils.HttpMethod.POST,
            mutableMapOf(
                "tag_type" to type,
                "type_id" to typeId,
                "user_id" to userId.toString()
            )
        ) { resource ->
            when (resource) {
                is Resource.Success -> callback(Resource.Success(resource.data))
                is Resource.Error -> callback(Resource.Error(resource.message))
            }
        }
    }

    fun sendEvaluate(
        type: String,
        typeId: Int,
        content: String,
        userId: Int,
        action: String,
        tagId: String,
        callback: (Resource<String>) -> Unit
    ) {
        val requestBody = mutableMapOf(
            "taggable_type" to type,
            "taggable_id" to typeId.toString(),
            "content" to content,
            "user_id" to userId.toString(),
            "action" to action,
            "tag_id" to tagId
        )
        NetUtils.fetch(
            ROOT_URL + USER_EVALUATE,
            NetUtils.HttpMethod.POST,
            requestBody
        ) { resource ->
            when (resource) {
                is Resource.Success -> callback(Resource.Success(resource.data))
                is Resource.Error -> callback(Resource.Error(resource.message))
            }
        }
    }

    fun userFeedBack(content: String, info: String) {
        NetUtils.fetch(
            ROOT_URL + USER_FEEDBACK,
            NetUtils.HttpMethod.POST,
            mapOf(
                "content" to content,
                "info" to info
            )
        ) {}
    }

    fun uploadImage(
        context: Context,
        uri: Uri,
        type: String,
        callback: (Resource<String>) -> Unit
    ) {
        NetUtils.uploadImage(
            context,
            ROOT_URL + UPLOAD_IMG,
            uri,
            type
        ) { resource ->
            when (resource) {
                is Resource.Success -> callback(Resource.Success(resource.data))
                is Resource.Error -> callback(Resource.Error(resource.message))
            }
        }
    }

    fun editGroupInfo(group: IdolGroupList, callback: (Resource<String>) -> Unit) {
        NetUtils.fetch(
            ROOT_URL + UPDATE_GROUP_INFO,
            NetUtils.HttpMethod.POST,
            mapOf(
                "id" to group.id.toString(),
                "name" to group.name,
                "img_url" to group.imgUrl,
                "location" to group.location,
                "version" to group.version.toString(),
                "group_desc" to group.groupDesc,
                "ext" to group.ext
            )
        ) { resource ->
            when (resource) {
                is Resource.Success -> callback(Resource.Success(resource.data))
                is Resource.Error -> callback(Resource.Error(resource.message))
            }
        }

    }

    fun insertActivity(data: ActivityList, callback: (Resource<String>) -> Unit) {
        NetUtils.fetch(
            ROOT_URL + INSERT_ACTIVITY_INFO,
            NetUtils.HttpMethod.POST,
            mapOf(
                "id" to data.id.toString(),
                "name" to data.name,
                "duration_date" to data.durationDate,
                "duration_time" to data.durationTime,
                "location" to data.location,
                "location_desc" to data.locationDesc,
                "price_desc" to data.price,
                "buy_url" to data.buyUrl,
                "info_desc" to data.infoDesc,
                "weibo_url" to data.weiboUrl
            ) + (data.ext?.let { mapOf("ext" to it) } ?: emptyMap())
                    + (data.participatingGroup?.let { mapOf("participating_group" to it) }
                ?: emptyMap()) + (data.biliUrl?.let { mapOf("bili_url" to data.biliUrl) }
                ?: emptyMap())
        ) { resource ->
            when (resource) {
                is Resource.Success -> callback(Resource.Success(resource.data))
                is Resource.Error -> callback(Resource.Error(resource.message))
            }
        }
    }

    fun getIdolTag(callback: (Resource<String>) -> Unit) {
        NetUtils.fetch(ROOT_URL + GET_IDOL_TAG, NetUtils.HttpMethod.POST, mapOf()) { resource ->
            when (resource) {
                is Resource.Success -> callback(Resource.Success(resource.data))
                is Resource.Error -> callback(Resource.Error(resource.message))
            }
        }
    }

    fun getUrl(url: String): String =
        when (url) {
            "info" -> INFO_URL
            "login" -> ROOT_URL + USER_LOGIN
            "group" -> ROOT_URL + IDOL_GROUP_LIST_URL
            "activity" -> ROOT_URL + ACTIVITY_LIST_URL
            else -> ""
        }
}