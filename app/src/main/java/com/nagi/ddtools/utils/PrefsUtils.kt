package com.nagi.ddtools.utils

import android.content.Context
import android.content.SharedPreferences
import com.nagi.ddtools.utils.DataUtils.getCurrentDateString

object PrefsUtils {
    private const val PREFS_NAME = "DdToolsPrefs"
    private const val KEY_FIRST_RUN = "firstRun"
    private const val KEY_LAST_RUN_DATE = "lastRunDate"
    private const val KEY_TOOL_TITLE = "toolTitle"
    private const val USER_SETTINGS_LOCATION = "userSettingLocation"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }


    fun isFirstRun(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_FIRST_RUN, true)
    }

    fun setFirstRunDone(context: Context) {
        getSharedPreferences(context).edit().putBoolean(KEY_FIRST_RUN, false).apply()
    }

    fun isTodayFirstRun(context: Context): Boolean {
        val todayDateString = getCurrentDateString()
        val lastRunDateString = getSharedPreferences(context).getString(KEY_LAST_RUN_DATE, "")
        return lastRunDateString != todayDateString
    }

    fun setTodayFirstRunDone(context: Context) {
        val todayDateString = getCurrentDateString()
        getSharedPreferences(context).edit().putString(KEY_LAST_RUN_DATE, todayDateString).apply()
    }

    fun getToolTitle(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_TOOL_TITLE, "别问，问就是闲的")
    }

    fun setToolTitle(context: Context, title: String) {
        getSharedPreferences(context).edit().putString(KEY_TOOL_TITLE, title).apply()
    }

    fun setSettingLocation(context: Context, location: String) {
        getSharedPreferences(context).edit().putString(USER_SETTINGS_LOCATION, location).apply()
    }
    fun getSettingLocation(context: Context): String? {
        return getSharedPreferences(context).getString(USER_SETTINGS_LOCATION, "")
    }

}

