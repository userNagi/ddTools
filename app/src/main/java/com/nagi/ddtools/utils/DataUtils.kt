package com.nagi.ddtools.utils

import java.security.MessageDigest

object DataUtils {
    fun hashPassword(username: String, password: String): String {
        val saltedPassword = username + password
        val digest = MessageDigest.getInstance("MD5")
        val hash = digest.digest(saltedPassword.toByteArray(Charsets.UTF_8))
        return bytesToHex(hash)
    }

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
    fun checkInputText(text: String): Boolean {
        return text.matches(Regex("^[a-zA-Z0-9_]+$"))
    }
    fun checkTextLength(text: String): Boolean {
        return text.length in 6..18
    }

    fun checkInput(text: String): Boolean {
        return checkInputText(text) && checkTextLength(text)
    }
}