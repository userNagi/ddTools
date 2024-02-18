package com.nagi.ddtools.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nagi.ddtools.database.activityList.ActivityList
import com.nagi.ddtools.database.activityList.ActivityListDao
import com.nagi.ddtools.database.homePagList.HomePageListDao
import com.nagi.ddtools.database.homePagList.HomePageList
import com.nagi.ddtools.database.idolGroupList.IdolGroupList
import com.nagi.ddtools.database.idolGroupList.IdolGroupListDao
import com.nagi.ddtools.database.idolList.IdolList
import com.nagi.ddtools.database.idolList.IdolListDao
import com.nagi.ddtools.database.idolList.IdolTag
import com.nagi.ddtools.database.idolList.IdolTagDao
import com.nagi.ddtools.database.user.User
import com.nagi.ddtools.database.user.UserDao

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
        ActivityList::class,
        IdolList::class,
        IdolTag::class,
        User::class
    ], version = 1, exportSchema = false
)
@TypeConverters(IdolList.Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun homePageDao(): HomePageListDao
    abstract fun idolGroupListDao(): IdolGroupListDao
    abstract fun activityListDao(): ActivityListDao
    abstract fun idolListDao(): IdolListDao
    abstract fun tagDao(): IdolTagDao
    abstract fun userDao(): UserDao

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

        fun getInstance() = instance!!
    }
}