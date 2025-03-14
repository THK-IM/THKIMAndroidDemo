package com.thinking.im.demo.ui.message.group

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.thinking.im.demo.R
import com.thinking.im.demo.databinding.ActivityFragmentContainerBinding
import com.thinking.im.demo.ui.base.BaseActivity

class CreateGroupActivity : BaseActivity() {

    companion object {
        fun start(ctx: Context) {
            val intent = Intent(ctx, CreateGroupActivity::class.java)
            ctx.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityFragmentContainerBinding

    override fun getToolbar(): Toolbar {
        return binding.tbTop.toolbar
    }

    override fun toolbarTitle(): String {
        return ContextCompat.getString(this, R.string.create_group)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityFragmentContainerBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initFragment()
    }

    private fun initFragment() {
        val tag = CreateGroupFragment::class.java.name
        var fragment: CreateGroupFragment? =
            supportFragmentManager.findFragmentByTag(tag) as? CreateGroupFragment
        if (fragment == null) {
            fragment = CreateGroupFragment()
        }
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(binding.lyContainer.id, fragment, tag)
        ft.commit()
    }
}