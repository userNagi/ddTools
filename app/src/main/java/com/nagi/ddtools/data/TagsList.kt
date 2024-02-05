package com.nagi.ddtools.data

data class TagsList(
    val id:String,
    val content: String,
    val tag_type: String,
    val type_id: Int,
    val likes_count: Int,
    val user_liked: Boolean
)