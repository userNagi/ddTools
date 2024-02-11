package com.nagi.ddtools.database.idolGroupList

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class IdolGroupList(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @SerializedName("img_url")
    @ColumnInfo(name = "img_url")
    var imgUrl: String,
    val name: String,
    val version: Int,
    val location: String,
    @SerializedName("group_desc")
    @ColumnInfo(name = "group_desc")
    var groupDesc: String,
    @SerializedName("member_ids")
    @ColumnInfo(name = "member_ids")
    val memberIds: String?,
    @SerializedName("activity_ids")
    @ColumnInfo(name = "activity_ids")
    val activityIds: String?,
    val ext: String
)
