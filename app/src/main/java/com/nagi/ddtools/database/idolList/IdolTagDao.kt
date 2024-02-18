package com.nagi.ddtools.database.idolList

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface IdolTagDao {
    @Query("SELECT * FROM IdolTag")
    fun getAll():List<IdolTag>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tag:IdolTag)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(tag:List<IdolTag>)

    @Delete()
    fun delete(tag:IdolTag)

    @Query("DELETE FROM IdolTag")
    fun deleteAll()
}