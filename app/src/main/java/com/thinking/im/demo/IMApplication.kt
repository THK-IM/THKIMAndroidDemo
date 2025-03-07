package com.thinking.im.demo

import android.app.Application
import com.thinking.im.demo.module.im.IMManger

class IMApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        IMManger.initIMConfig(this, true)
    }
}