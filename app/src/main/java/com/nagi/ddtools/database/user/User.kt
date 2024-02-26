package com.nagi.ddtools.database.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val username: String,
    val nickname: String,
    val avatar_url: String,
    val bio: String,
    val email: String,
    val role: String,
    val experience: String,
    val status: String,
    val ext: String,
    val sex: String?,
    val preferred_location: String?,
    var isMainUser:Boolean
)