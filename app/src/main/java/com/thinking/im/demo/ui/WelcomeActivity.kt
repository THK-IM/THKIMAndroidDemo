package com.thinking.im.demo.ui

import android.content.Intent
import android.os.Bundle
import com.thinking.im.demo.databinding.ActivityWelcomeBinding
import com.thinking.im.demo.module.im.IMManger
import com.thinking.im.demo.repository.DataRepository
import com.thinking.im.demo.ui.base.BaseActivity
import com.thk.im.android.core.base.BaseSubscriber
import com.thk.im.android.core.base.RxTransform

class WelcomeActivity : BaseActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

        val subscriber = object : BaseSubscriber<Boolean>() {
            override fun onNext(t: Boolean?) {
                initIMResult()
            }
        }
        val token = "3a2e0af2-d9f3-43c6-b8a0-ce5dcb503ad2"
        DataRepository.updateToken(token)
        IMManger.initIMUser(token, 12)
            .compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
        addDispose(subscriber)
    }

    private fun initIMResult() {
        finish()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}