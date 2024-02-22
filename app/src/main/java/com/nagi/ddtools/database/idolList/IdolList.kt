package com.nagi.ddtools.database.idolList

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

@Entity
data class IdolList(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    @SerializedName("group_id") @ColumnInfo(name = "group_id")
    val groupId: Int,
    @SerializedName("group_name") @ColumnInfo(name = "group_name")
    val groupName: String,
    @SerializedName("image_url") @ColumnInfo(name = "image_url")
    var imageUrl: String,
    val version: String,
    val location: String,
    val description: String?,
    var sex: String?,
    val birthday: String?,
    val tag: IdolTag?,
    @SerializedName("additional_info") @ColumnInfo(name = "additional_info")
    val ext: String?
) {
    class Converters {

        @TypeConverter
        fun fromIdolTag(idolTag: IdolTag?): String? {
            if (idolTag == null) return null
            val gson = Gson()
            return gson.toJson(idolTag)
        }

        @TypeConverter
        fun toIdolTag(idolTagString: String?): IdolTag? {
            if (idolTagString == null) return null
            val gson = Gson()
            return gson.fromJson(idolTagString, IdolTag::class.java)
        }
    }
}