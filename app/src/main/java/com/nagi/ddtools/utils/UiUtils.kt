package com.nagi.ddtools.utils

import android.content.Context
import android.widget.Toast

/**
 * @projectName
 * @author :fmqwd
 * @description:
 * @date :2023/12/28 0:49
 */
import java.util.concurrent.atomic.AtomicReference

object UiUtils {
    private val currentToastRef = AtomicReference<Toast?>(null)

    fun toast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        currentToastRef.get()?.cancel()
        currentToastRef.set(Toast.makeText(context, message, duration))
        currentToastRef.get()?.show()
    }
}