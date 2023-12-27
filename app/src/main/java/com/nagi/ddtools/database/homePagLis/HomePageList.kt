package com.nagi.ddtools.database.homePagLis

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * @projectName
 * @author :fmqwd
 * @description:
 * @date :2023/12/27 1:21
 */
@Parcelize
@Entity
data class HomePageList(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val imgUrl: String,
    val name: String
) : Parcelable
