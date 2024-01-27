package com.nagi.ddtools.resourceGet

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nagi.ddtools.database.AppDatabase
import com.nagi.ddtools.database.activityList.ActivityList
import com.nagi.ddtools.database.idolGroupList.IdolGroupList
import com.nagi.ddtools.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object DataSet {
    private val database by lazy {
        AppDatabase.getInstance()
    }

    fun setActivity(context: Context) {
        MainScope().launch {
            withContext(Dispatchers.IO) {
                val inputStream = File(context.filesDir, FileUtils.ACTIVITY_LIS_FILE)
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                val itemType = object : TypeToken<List<ActivityList>>() {}.type
                val activityList: List<ActivityList> = Gson().fromJson(jsonString, itemType)
                database.activityListDao().deleteAll()
                database.activityListDao().insertAll(activityList)
            }
        }
    }


    fun setIdolGroups(context: Context) {
        MainScope().launch {
            withContext(Dispatchers.IO) {
                val inputStream = File(context.filesDir, FileUtils.IDOL_GROUP_FILE)
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                val itemType = object : TypeToken<List<IdolGroupList>>() {}.type
                val idolGroupList: List<IdolGroupList> = Gson().fromJson(jsonString, itemType)
                database.idolGroupListDao().deleteAll()
                database.idolGroupListDao().insertAll(idolGroupList)
            }
        }
    }
}