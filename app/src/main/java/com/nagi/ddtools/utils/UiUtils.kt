package com.nagi.ddtools.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.AutoCompleteTextView.OnDismissListener
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * @projectName
 * @author :fmqwd
 * @description:
 * @date :2023/12/28 0:49
 */

object UiUtils {
    private var currentToast: Toast? = null
    private var currentDialog: AlertDialog? = null
    private var loadingDialog: Dialog? = null
    private var loadingJob: Job? = null

    @Synchronized
    fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            currentToast?.cancel()
            currentToast = Toast.makeText(this, message, duration).apply { show() }
        } else {
            MainScope().launch {
                currentToast?.cancel()
                currentToast = Toast.makeText(this@toast, message, duration).apply { show() }
            }
        }
    }

    fun Context.dialog(
        title: String,
        message: String,
        positiveButtonText: String = "确定",
        negativeButtonText: String = "取消",
        onPositive: (() -> Unit)? = null,
        onNegative: (() -> Unit)? = null,
        customView: View? = null,
        onDismiss: (() -> Unit)? = null
    ) {

        currentDialog?.dismiss()
        currentDialog = AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            if (customView != null) {
                setView(customView)
            }
            setPositiveButton(positiveButtonText) { _, _ -> onPositive?.invoke() }
            setNegativeButton(negativeButtonText) { _, _ -> onNegative?.invoke() }
            setOnDismissListener {
                onDismiss?.invoke()
            }
        }.create()
        currentDialog?.show()
    }

    @Synchronized
    fun showLoading(context: Context) {
        hideLoading()
        loadingDialog = Dialog(context).apply {
            setContentView(ProgressBar(context).apply {
                setTitle("加载中")
            })
            setCancelable(false)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            show()
        }
        loadingJob = MainScope().launch {
            withContext(Dispatchers.Main) {
                delay(9000)
                context.toast("加载超时，请检查网络或稍后再试。")
                hideLoading()
            }
        }
    }


    @Synchronized
    fun hideLoading() {
        loadingDialog?.dismiss()
        loadingDialog = null
        loadingJob?.cancel()
    }

    @Synchronized
    fun hideDialog() {
        currentDialog?.dismiss()
        currentDialog = null
    }

    fun Context.openUrl(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        intent.data = uri
        this.startActivity(intent)
    }

    fun openPage(
        activity: Activity,
        page: Class<*>,
        needFinish: Boolean = false,
        bundle: Bundle = Bundle()
    ) {
        val intent = Intent(activity, page)
        intent.putExtras(bundle)
        activity.startActivity(intent)
        if (needFinish) activity.finish()
    }

    fun Context.copyToClipboard(text: String) {
        val clipboardManager =
            this.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clipData = android.content.ClipData.newPlainText("text", text)
        clipboardManager.setPrimaryClip(clipData)
    }
}
