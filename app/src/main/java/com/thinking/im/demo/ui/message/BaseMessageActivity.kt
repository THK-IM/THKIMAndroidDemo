package com.thinking.im.demo.ui.message

import android.os.Build
import android.os.Bundle
import com.thinking.im.demo.ui.base.BaseRelationActivity
import com.thk.im.android.core.db.entity.Session
import com.thk.im.android.ui.manager.IMUIManager

abstract class BaseMessageActivity : BaseRelationActivity() {

    override fun toolbarTitle(): String? {
        val session = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("session", Session::class.java)
        } else {
            intent.getParcelableExtra("session")
        }
        return session?.name
    }

    override fun toolbarBgColor(): Int? {
        return IMUIManager.uiResourceProvider?.inputBgColor()
    }

    protected fun session(): Session? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("session", Session::class.java)
        } else {
            intent.getParcelableExtra("session")
        }
    }

    fun initMessageFragment(containerId: Int) {
        val session = session()
        val tag = IMBaseMessageFragment::class.java.name
        var fragment: IMBaseMessageFragment? =
            supportFragmentManager.findFragmentByTag(tag) as? IMBaseMessageFragment
        if (fragment == null) {
            fragment = IMBaseMessageFragment()
        }
        fragment.arguments = Bundle().apply { putParcelable("session", session) }
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(containerId, fragment, tag)
        ft.commit()
    }

}