package com.nagi.ddtools.database.localCollect

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LocalCollectDao {
    @Query("SELECT * FROM LocalCollect")
    fun getAll(): List<LocalCollect>

    @Query("SELECT * FROM LocalCollect WHERE collectType = :collectType ")
    fun getByType(collectType: String): List<LocalCollect>

    @Query("SELECT * FROM LocalCollect WHERE userId = :userId ")
    fun getByUserId(userId: Int): List<LocalCollect>

    @Query("SELECT * FROM LocalCollect WHERE collectType = :type AND collectId = :id")
    fun getByTypeAndId(type: String, id: Int): List<LocalCollect>

    @Insert()
    fun insertCollect(localCollect: LocalCollect)

    @Delete()
    fun deleteCollect(localCollect: LocalCollect)

    @Query("DELETE FROM LocalCollect WHERE collectType = :type AND collectId = :id")
    fun deleteByTypeAndId(type: String, id: Int)
}