package com.nagi.ddtools.utils

import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

object DataUtils {
    /**
     * 计算密码
     * @param salt 盐
     * @param string 字符串
     * @return 加密后的密码
     * 此处本地只使用md5计算，实际上在服务端有其他的加密方式，这里只是为了不明文传输密码
     */
    fun hashPassword(salt: String, string: String): String {
        val saltedPassword = salt + string
        val digest = MessageDigest.getInstance("MD5")
        val hash = digest.digest(saltedPassword.toByteArray(Charsets.UTF_8))
        return bytesToHex(hash)
    }

    /**
     * 检查输入文本是否合法
     * @param text 输入文本
     * @return true合法，false不合法
     */
    fun checkInputText(text: String): Boolean {
        return text.matches(Regex("^[a-zA-Z0-9_]+$"))
    }

    /**
     * 检查输入文本长度是否合法
     * @param text 输入文本
     * @return true合法，false不合法
     */
    fun checkTextLength(text: String): Boolean {
        return text.length in 6..18
    }

    /**
     * 检查输入文本是否合法
     * @param text 输入文本
     * @return true合法，false不合法
     */
    fun checkInput(text: String): Boolean {
        return checkInputText(text) && checkTextLength(text)
    }

    /**
     * 获取当前日期
     * @return 当前日期
     */
    fun getCurrentDateString(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    /**
     * 将日期转为数组返回，注意是倒序
     * @param dateStr 例如：2023-01-13
     * @return 数组，例如：["13", "01", "2023"]
     */
    fun parseDate(dateStr: String): Array<String> {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val localDate = LocalDate.parse(dateStr, dateFormatter)

        val day = localDate.dayOfMonth.toString()
        val month = localDate.monthValue.toString()
        val year = localDate.year.toString()

        return arrayOf(day, month, year)
    }

    /**
     * 格式化组的时间表
     * @param timeTable 例如：1111-10:00:00-12:00:00
     * @return 格式化后的时间数组，例如：["1111", "10:00:00", "12:00:00","02:00:00"]
     */
    fun getGroupTime(timeTable: String): MutableList<String> =
        timeTable.split("-").toMutableList().apply {
            if (size > 2) {
                if (this[1] == "EMPTY" || this[1].isEmpty() || this[1] == "00:00:00") {
                    removeAt(1)
                    removeAt(1)
                } else {
                    val durationTime = getDurationTime(this[1], this[2])
                    add(durationTime)
                }
            }
        }

    /*
     * 计算两个时间段的间隔
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 间隔
     */
    fun getDurationTime(startTime: String, endTime: String): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val start = LocalTime.parse(startTime, formatter)
        val end = LocalTime.parse(endTime, formatter)
        val durationInSeconds = if (end.isBefore(start)) {
            ChronoUnit.SECONDS.between(start, end.plusHours(24))
        } else {
            ChronoUnit.SECONDS.between(start, end)
        }
        val hours = durationInSeconds / 3600
        val minutes = (durationInSeconds % 3600) / 60
        val seconds = durationInSeconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    /**
     * 去除秒的时间
     * @param time 时间
     * @return 去除秒的时间
     */
    fun trimSecondsFromTime(time: String): String {
        val pattern = Regex("(:00)$")
        return pattern.replace(time, "")
    }

    /**
     * 将字节数组转换为十六进制字符串
     * @param hash 字节数组
     * @return 十六进制字符串
     */
    private fun bytesToHex(hash: ByteArray): String {
        val hexString = StringBuilder(2 * hash.size)
        for (i in hash.indices) {
            val hex = Integer.toHexString(0xff and hash[i].toInt())
            if (hex.length == 1) {
                hexString.append('0')
            }
            hexString.append(hex)
        }
        return hexString.toString()
    }
}