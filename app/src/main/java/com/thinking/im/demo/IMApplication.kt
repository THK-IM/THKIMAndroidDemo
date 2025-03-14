package com.thinking.im.demo

import android.app.Application
import com.thinking.im.demo.module.im.IMManger
import com.thinking.im.demo.repository.DataRepository

class IMApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DataRepository.init(this, true)
        IMManger.initIMConfig(this, true)
    }
}