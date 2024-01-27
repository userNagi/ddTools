package com.nagi.ddtools

import android.app.Application
import com.nagi.ddtools.database.AppDatabase
import com.nagi.ddtools.resourceGet.DataSet
import com.nagi.ddtools.resourceGet.NetGet
import com.nagi.ddtools.utils.FileUtils.copyRawResourceToFile
import com.nagi.ddtools.utils.LogUtils
import com.nagi.ddtools.utils.PrefsUtils

class DdTools : Application() {

    override fun onCreate() {
        super.onCreate()
        AppDatabase.getInstance(applicationContext)
        LogUtils.isDebug = true
        initGroupList()
    }

    private fun initGroupList() {
        if (PrefsUtils.isFirstRun(applicationContext)) {
            copyRawResourceToFile(
                applicationContext,
                R.raw.idolgrouplist,
                "idolGroupList.json",
                "data"
            )
            copyRawResourceToFile(
                applicationContext,
                R.raw.activitylist,
                "activityList.json",
                "data"
            )
            PrefsUtils.setFirstRunDone(applicationContext)
        }
        NetGet.getIdolGroupList(applicationContext)
        NetGet.getActivityList(applicationContext)
        DataSet.setIdolGroups(applicationContext)
        DataSet.setActivity(applicationContext)
    }
}
