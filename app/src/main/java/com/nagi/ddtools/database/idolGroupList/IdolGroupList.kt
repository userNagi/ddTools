package com.nagi.ddtools.database.idolGroupList

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class IdolGroupList(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val img_url: String,
    val name: String,
    val version: Int,
    val location: String,
    val group_desc: String,
    val ext:String
)
