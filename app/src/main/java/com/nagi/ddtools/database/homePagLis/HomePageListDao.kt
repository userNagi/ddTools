package com.nagi.ddtools.database.homePagLis

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

/**
 * @projectName
 * @author :fmqwd
 * @description:
 * @date :2023/12/27 1:31
 */
@Dao
interface HomePageListDao {
    @Query("SELECT * FROM HomePageList")
    fun getAll(): List<HomePageList>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(info: HomePageList)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(info: HomePageList)

    @Delete
    fun delete(info: HomePageList)
}