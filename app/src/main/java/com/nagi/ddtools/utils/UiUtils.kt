package com.nagi.ddtools.utils

import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

/**
 * @projectName
 * @author :fmqwd
 * @description:
 * @date :2023/12/28 0:49
 */
import java.util.concurrent.atomic.AtomicReference

object UiUtils {
    private var currentToast: Toast? = null

    @Synchronized
    fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        currentToast?.cancel()
        currentToast = Toast.makeText(this, message, duration).apply { show() }
    }

    fun Context.dialog(
        title: String,
        message: String,
        positiveButtonText: String = "确定",
        negativeButtonText: String = "取消",
        onPositive: (() -> Unit)? = null,
        onNegative: (() -> Unit)? = null
    ) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(positiveButtonText) { _, _ -> onPositive?.invoke() }
            setNegativeButton(negativeButtonText) { _, _ -> onNegative?.invoke() }
        }.show()
    }
}
