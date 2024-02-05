package com.nagi.ddtools.database.homePagList

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

    @Query("SELECT * FROM HomePageList WHERE id = :id")
    fun getById(id: Int): HomePageList

    @Query("SELECT * FROM HomePageList WHERE parent = :id")
    fun getByParent(id: Int): List<HomePageList>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(info: HomePageList)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(info: HomePageList)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(info: List<HomePageList>)

    @Delete
    fun delete(info: HomePageList)

    @Query("DELETE FROM HomePageList WHERE parent = :id")
    fun deleteByParent(id: Int)
}