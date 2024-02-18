package com.nagi.ddtools.database.idolList

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface IdolListDao {
    @Query("SELECT * FROM IdolList")
    fun getAll(): List<IdolList>

    @Query("SELECT * FROM IdolList WHERE id = :id")
    fun getById(id: Int): IdolList

    @Query("SELECT * FROM IdolList WHERE name = :name")
    fun getByName(name: String): IdolList

    @Query("SELECT * FROM IdolList WHERE location = :location")
    fun getByLocation(location: String): List<IdolList>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(info: IdolList)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(info: List<IdolList>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(info: IdolList)

    @Query("DELETE FROM IdolList")
    fun deleteAll()

    @Delete
    fun delete(info: IdolList)
}