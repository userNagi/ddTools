package com.nagi.ddtools.utils

import android.util.Log

object LogUtils {
    private const val TAG: String = "LogUtils"
    var isDebug: Boolean = true

    private fun buildLogMsg(message: String): String {
        val stackTrace = Throwable().stackTrace[2]
        val className = stackTrace.className.substringAfterLast('.')
        val methodName = stackTrace.methodName
        val lineNumber = stackTrace.lineNumber
        return "[$className|$methodName|$lineNumber]: $message"
    }

    fun v(message: String) {
        if (isDebug) Log.v(TAG, buildLogMsg(message))
    }

    fun d(message: String) {
        if (isDebug) Log.d(TAG, buildLogMsg(message))
    }

    fun i(message: String) {
        if (isDebug) Log.i(TAG, buildLogMsg(message))
    }

    fun w(message: String) {
        if (isDebug) Log.w(TAG, buildLogMsg(message))
    }

    fun e(message: String) {
        if (isDebug) Log.e(TAG, buildLogMsg(message))
    }
}
