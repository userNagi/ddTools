package com.nagi.ddtools.utils

import android.content.Context
import android.content.SharedPreferences

object PrefsUtils {
    private const val PREFS_NAME = "DdToolsPrefs"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isFirstRun(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_FIRST_RUN, true)
    }

    fun setFirstRunDone(context: Context) {
        getSharedPreferences(context).edit().putBoolean(KEY_FIRST_RUN, false).apply()
    }

    private const val KEY_FIRST_RUN = "firstRun"
}
