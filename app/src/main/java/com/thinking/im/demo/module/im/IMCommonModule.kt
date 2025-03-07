package com.thinking.im.demo.module.im

import android.app.Application
import com.thk.im.android.core.module.internal.DefaultCommonModule

class IMCommonModule(val app: Application) : DefaultCommonModule() {

    override fun beKickOff() {
    }

    override fun onSignalReceived(type: Int, body: String) {
    }
}