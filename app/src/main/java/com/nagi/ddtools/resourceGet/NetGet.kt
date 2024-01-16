package com.nagi.ddtools.resourceGet

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.nagi.ddtools.data.UpdateInfo
import com.nagi.ddtools.utils.FileUtils.ACTIVITY_LIS_FILE
import com.nagi.ddtools.utils.FileUtils.IDOL_GROUP_FILE
import com.nagi.ddtools.utils.LogUtils
import com.nagi.ddtools.utils.NetUtils
import java.io.File

object NetGet {
    private const val ROOT_URL = "https://wiki.chika-idol.live/request/ddtools/"
    private const val IDOL_GROUP_LIST_URL = "getChikaIdolList.php/"
    private const val ACTIVITY_LIST_URL = "getActivity.php/"
    private const val CHECK_UPDATE_URL = "getUpdateInfo.php/"
    fun getIdolGroupList(context: Context) {
        NetUtils.fetchAndSave(
            ROOT_URL + IDOL_GROUP_LIST_URL,
            NetUtils.HttpMethod.POST,
            emptyMap(),
            File(context.filesDir, IDOL_GROUP_FILE).path
        ) { success, message ->
            if (!success) {
                LogUtils.e("Failed to fetch idol group list: $message")
            }
        }
    }

    fun getActivityList(context: Context) {
        NetUtils.fetchAndSave(
            ROOT_URL + ACTIVITY_LIST_URL,
            NetUtils.HttpMethod.POST,
            emptyMap(),
            File(context.filesDir,ACTIVITY_LIS_FILE).path
        ) { success, message ->
            if (!success) {
                LogUtils.e("Failed to fetch activity list: $message")
            }
        }
    }

    fun getUpdateDetails(callback: (UpdateInfo?) -> Unit) {
        NetUtils.fetch(
            ROOT_URL + CHECK_UPDATE_URL,
            NetUtils.HttpMethod.GET,
            emptyMap()
        ) { success, result ->
            if (success && result != null) {
                try {
                    val itemType = object : TypeToken<UpdateInfo>() {}.type
                    val updateInfo: UpdateInfo = Gson().fromJson(result, itemType)
                    callback(updateInfo)
                } catch (e: JsonSyntaxException) {
                    LogUtils.e("UpdateInfo: JSON parsing error: ${e.localizedMessage}")
                    callback(null)
                }
            } else {
                LogUtils.e("UpdateInfo: Failed to fetch update details: $result")
                callback(null)
            }
        }
    }


}