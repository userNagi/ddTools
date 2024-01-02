package com.nagi.ddtools.data

data class UpdateInfo (
    val version: String = "1.0",
    val updateUrl: String = "https://wiki.chika-idol.live/request/ddtools/ddtools.apk",
    val title: String = "更新提示",
    val message: String = "有新版本可以更新"
)
