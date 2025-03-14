package com.thinking.im.demo.module.im

import android.app.Application
import androidx.core.content.ContextCompat
import com.thinking.im.demo.R
import com.thk.im.android.core.base.utils.ToastUtils
import com.thk.im.android.core.module.internal.DefaultCommonModule

class IMCommonModule(val app: Application) : DefaultCommonModule() {

    override fun beKickOff() {
        val text = ContextCompat.getString(app, R.string.be_kick_off)
        ToastUtils.show(text)
    }

    private fun logout(reason: String) {
        // TODO
    }

    override fun onSignalReceived(type: Int, body: String) {
        if (type == 5) {
            logout(ContextCompat.getString(app, R.string.other_device_login))
        } else {
            super.onSignalReceived(type, body)
        }
    }
}