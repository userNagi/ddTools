package com.nagi.ddtools.database.activityList

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.Nullable

@Entity
data class ActivityList(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val duration_date: String,
    val duration_time: String,
    val location: String,
    val participating_group: String,
    val price: String,
    val buy_url: String,
    val location_desc: String,
    val info_desc: String,
    val weibo_url: String,
    val bili_url: String?,
    val ext: String?
)