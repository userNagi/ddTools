package com.nagi.ddtools.database.homePagLis

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @projectName
 * @author :fmqwd
 * @description:
 * @date :2023/12/27 1:21
 */
@Entity
data class HomePageList(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val imgUrl: String,
    val name: String
)
