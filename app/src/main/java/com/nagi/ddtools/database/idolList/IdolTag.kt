package com.nagi.ddtools.database.idolList

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigInteger

@Entity
data class IdolTag(
    @PrimaryKey(autoGenerate = true)
    val labelID: Int = 0,
    val text: String,
    val textColor: String,
    val backColor: String,
    val other: String?
)
