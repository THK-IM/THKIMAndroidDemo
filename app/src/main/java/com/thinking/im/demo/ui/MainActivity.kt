package com.thinking.im.demo.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import androidx.core.view.get
import com.thinking.im.demo.databinding.ActivityMainBinding
import com.thinking.im.demo.ui.base.BaseActivity
import com.thk.im.android.core.IMCoreManager
import com.thk.im.android.core.base.BaseSubscriber
import com.thk.im.android.core.base.RxTransform
import com.thk.im.android.core.db.entity.Session
import com.thk.im.android.core.exception.CodeMsgException
import com.thk.im.android.ui.fragment.IMMessageFragment
import com.thk.im.android.ui.main.adpater.MainFragmentAdapter

class MainActivity : BaseActivity() {

    private var chooseMenuIndex = 0
    private var bottomMenuTitles = setOf("message", "contact", "group", "mine")

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        val adapter = MainFragmentAdapter(this)
        binding.vpContainer.adapter = adapter
        binding.vpContainer.isUserInputEnabled = false

        binding.bnvBottom.setOnItemSelectedListener {
            return@setOnItemSelectedListener onBottomItemSelected(it)
        }
        binding.bnvBottom.selectedItemId = binding.bnvBottom[chooseMenuIndex].id
        setTitle(bottomMenuTitles.elementAt(chooseMenuIndex))

        val subscriber = object : BaseSubscriber<Session>() {
            override fun onNext(t: Session?) {
                t?.let { session ->
                    // TODO 用一个activity去显示IMMessageFragment 把session传给fragment
                    val messageFragment = IMMessageFragment()
                    messageFragment.arguments = Bundle().apply {
                        putParcelable("session", session)
                    }
                }
            }

            override fun onError(t: Throwable?) {
                super.onError(t)
                t?.let {
                    val err = it as? CodeMsgException ?: return@let
                }
            }

        }

        IMCoreManager.messageModule.getSession(1899294575512524500)
            .compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
    }

    private fun onBottomItemSelected(it: MenuItem): Boolean {
        val pos = bottomMenuTitles.indexOf(it.title)
        binding.vpContainer.setCurrentItem(pos, false)
        return true
    }

}