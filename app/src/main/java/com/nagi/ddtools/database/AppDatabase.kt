package com.nagi.ddtools.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nagi.ddtools.database.activityList.ActivityList
import com.nagi.ddtools.database.activityList.ActivityListDao
import com.nagi.ddtools.database.homePagLis.HomePageListDao
import com.nagi.ddtools.database.homePagLis.HomePageList
import com.nagi.ddtools.database.idolGroupList.IdolGroupList
import com.nagi.ddtools.database.idolGroupList.IdolGroupListDao

/**
 * @projectName
 * @author :fmqwd
 * @description:
 * @date :2023/12/27 1:37
 */
@Database(
    entities = [
        HomePageList::class,
        IdolGroupList::class,
        ActivityList::class
    ], version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun homePageDao(): HomePageListDao
    abstract fun idolGroupListDao(): IdolGroupListDao
    abstract fun activityListDao(): ActivityListDao
    companion object {
        private var instance: AppDatabase? = null
        private val instanceLock = Any()
        fun getInstance(context: Context): AppDatabase {
            synchronized(instanceLock) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "ddTools"
                    ).build()
                }
            }
            return instance!!
        }

        fun getInstance() = instance
    }
}