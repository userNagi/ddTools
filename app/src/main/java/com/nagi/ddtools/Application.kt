package com.nagi.ddtools

import android.app.Application
import com.nagi.ddtools.database.AppDatabase

class DdTools : Application() {
    override fun onCreate() {
        super.onCreate()
        AppDatabase.getInstance(applicationContext)
    }
}
