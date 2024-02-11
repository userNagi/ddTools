package com.nagi.ddtools.database.activityList

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
@Dao
interface ActivityListDao {
    @Query("SELECT * FROM ActivityList ORDER BY duration_date ASC")
    fun getAll(): List<ActivityList>

    @Query("SELECT * FROM ActivityList WHERE id = :id")
    fun getById(id: String): ActivityList

    @Query("SELECT * FROM ActivityList WHERE name = :name")
    fun getByName(name: String): ActivityList
    @Query("SELECT * FROM ActivityList WHERE location = :location")
    fun getByLocation(location: String): List<ActivityList>

    @Query("SELECT * FROM ActivityList WHERE duration_date >= :date")
    fun getByDateAfter(date: String): List<ActivityList>

    @Query("SELECT * FROM ActivityList WHERE duration_date = :date")
    fun getByDateAsToday(date: String): List<ActivityList>
    @Query("SELECT * FROM ActivityList WHERE duration_date < :date")
    fun getByDateBefore(date: String): List<ActivityList>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(activityList: ActivityList)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(activityList: List<ActivityList>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(activity: ActivityList)

    @Delete
    fun delete(activity: ActivityList)

    @Query("DELETE FROM ActivityList")
    fun deleteAll()

}