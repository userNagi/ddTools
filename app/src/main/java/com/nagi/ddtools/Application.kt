package com.nagi.ddtools

import android.app.Application
import com.nagi.ddtools.database.AppDatabase
import com.nagi.ddtools.utils.FileUtils.copyRawResourceToFile

class DdTools : Application() {
    override fun onCreate() {
        super.onCreate()
        AppDatabase.getInstance(applicationContext)
        copyRawResourceToFile(applicationContext, R.raw.idolgrouplist, "idolGroupList.json", "data")
    }
}
