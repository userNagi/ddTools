package com.nagi.ddtools.database.activityList

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class ActivityList(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    @SerializedName("duration_date") @ColumnInfo(name = "duration_date")
    val durationDate: String,
    @SerializedName("duration_time") @ColumnInfo(name = "duration_time")
    val durationTime: String,
    val location: String,
    val price: String,
    @SerializedName("buy_url") @ColumnInfo(name = "buy_url")
    val buyUrl: String,
    @SerializedName("location_desc") @ColumnInfo(name = "location_desc")
    val locationDesc: String,
    @SerializedName("info_desc") @ColumnInfo(name = "info_desc")
    val infoDesc: String,
    @SerializedName("weibo_url") @ColumnInfo(name = "weibo_url")
    val weiboUrl: String,
    @SerializedName("bili_url") @ColumnInfo(name = "bili_url")
    val biliUrl: String?,
    val ext: String?,
    @SerializedName("participating_groups") @ColumnInfo(name = "participating_groups")
    val participatingGroup: String?
)