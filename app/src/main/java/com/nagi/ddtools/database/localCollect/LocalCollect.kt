package com.nagi.ddtools.database.localCollect

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocalCollect(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val collectType: String,
    val collectId: Int,
    val userId: Int = -1,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = "",
    val ext: String = ""
)