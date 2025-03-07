package com.thinking.im.demo.module.im

import com.thk.im.android.core.Crypto
import com.thk.im.android.core.base.AES

class IMCipher : Crypto {

    companion object {
        fun key(): String {
            return "1234123412341234"
        }

        fun iv(): String {
            return "0000000000000000"
        }
    }

    private val aes = AES(key(), iv())

    override fun encrypt(text: String): String? {
        val result = aes.encrypt(text)
        if (result.isEmpty()) {
            return null
        }
        return result
    }

    override fun decrypt(cipherText: String): String? {
        val result = aes.decrypt(cipherText)
        if (result.isEmpty()) {
            return null
        }
        return result
    }
}