package com.nagi.ddtools.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import com.nagi.ddtools.utils.UiUtils.copyToClipboard
import com.nagi.ddtools.utils.UiUtils.dialog

object MapUtils {

    fun chooseLocation(context: Context, address: String) {
        val items = arrayOf("高德地图", "百度地图", "腾讯地图")
        val actions = arrayOf(
            { navigateWithGaodeMap(context, address) },
            { navigateWithBaiduMap(context, address) },
            { navigateWithTencentMap(context, address) }
        )

        AlertDialog.Builder(context)
            .setTitle("选择地图")
            .setItems(items) { _, which ->
                if (which in actions.indices) {
                    actions[which].invoke()
                }
            }
            .show()
    }

    private fun navigateWithGaodeMap(context: Context, address: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data =
                Uri.parse("androidamap://route?sourceApplication=appname&dname=$address&dev=0&t=0")
            setPackage("com.autonavi.minimap")
        }
        startActivityIfAvailable(context, intent, address)
    }

    private fun navigateWithBaiduMap(context: Context, address: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("baidumap://map/direction?destination=$address&mode=driving")
            setPackage("com.baidu.BaiduMap")
        }
        startActivityIfAvailable(context, intent, address)
    }

    private fun navigateWithTencentMap(context: Context, address: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("qqmap://map/routeplan?type=drive&to=$address")
            setPackage("com.tencent.map")
        }
        startActivityIfAvailable(context, intent, address)
    }

    private fun startActivityIfAvailable(context: Context, intent: Intent, address: String) {
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            showAppNotInstalledDialog(context, address)
        }
    }

    private fun showAppNotInstalledDialog(context: Context, address: String) {
        context.dialog(
            "提示",
            "未检测到相关地图应用，点击确定将为您复制到剪切板",
            "确定",
            "取消",
            { context.copyToClipboard(address) })
    }
}
