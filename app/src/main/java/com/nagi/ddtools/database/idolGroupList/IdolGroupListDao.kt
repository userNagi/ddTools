package com.nagi.ddtools.database.idolGroupList

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface IdolGroupListDao {
    @Query("SELECT * FROM IdolGroupList")
    fun getAll(): List<IdolGroupList>

    @Query("SELECT * FROM IdolGroupList WHERE id = :id")
    fun getById(id: Int): IdolGroupList
    @Query("SELECT * FROM IdolGroupList WHERE name = :name")
    fun getByName(name: String):IdolGroupList?
    @Query("SELECT * FROM IdolGroupList WHERE location = :location")
    fun getByLocation(location:String):List<IdolGroupList>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(info: IdolGroupList)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(info: List<IdolGroupList>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(info: IdolGroupList)

    @Query("DELETE FROM IdolGroupList")
    fun deleteAll()

    @Delete
    fun delete(info: IdolGroupList)
}