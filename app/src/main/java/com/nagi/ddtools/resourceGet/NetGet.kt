package com.nagi.ddtools.resourceGet

import android.content.Context
import com.nagi.ddtools.utils.FileUtils.IDOL_GROUP_FILE
import com.nagi.ddtools.utils.LogUtils
import com.nagi.ddtools.utils.NetUtils
import org.json.JSONObject
import java.io.File

object NetGet {
    const val ROOT_URL = "https://wiki.chika-idol.live/request/ddtools/"
    const val IDOL_GROUP_LIST_URL = "getChikaIdolList.php"
    const val CHECK_UPDATE_URL = "getUpdateInfo.php"
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

    fun getUpdateDetails(context: Context): Pair<String, String> {
        val pair = Pair("1.0", "")
        NetUtils.fetch(
            ROOT_URL + CHECK_UPDATE_URL,
            NetUtils.HttpMethod.GET,
            emptyMap()
        ) { success, result ->
            if (success && result != null) {
                try {
                    val jsonObject = JSONObject(result)
                    val version = jsonObject.optString("version")
                    val updateUrl = jsonObject.optString("updateUrl")
                    LogUtils.d("UpdateInfo：Version: $version, Update URL: $updateUrl")
                    pair.copy(version, updateUrl)
                } catch (e: Exception) {
                    LogUtils.e("UpdateInfo：JSON parsing error: ${e.localizedMessage}")
                }
            } else {
                LogUtils.e("UpdateInfo：Failed to fetch update details: $result")
            }
        }
        return pair
    }
}