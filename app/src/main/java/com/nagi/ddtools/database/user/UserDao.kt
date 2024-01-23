package com.nagi.ddtools.database.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM User")
    fun getAll(): List<User>
    @Query("SELECT * FROM User where id =:id")
    fun getById(id: Int): User

    @Query("DELETE FROM User WHERE id =:id")
    fun deleteById(id: String)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

}