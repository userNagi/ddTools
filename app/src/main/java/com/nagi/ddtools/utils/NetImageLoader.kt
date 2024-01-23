package com.nagi.ddtools.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

object NetImageLoader {
    val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun cancelAll() {
        coroutineScope.cancel()
    }
}