package com.nagi.ddtools.database.homePagList

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
    val name: String,
    val info: String? = "",
    val parent: Int? = 0,
)
