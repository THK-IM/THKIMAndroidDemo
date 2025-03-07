package com.thinking.im.demo.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.get
import com.thinking.im.demo.databinding.ActivityMainBinding
import com.thinking.im.demo.ui.base.BaseActivity
import com.thk.im.android.ui.main.adpater.MainFragmentAdapter

class MainActivity : BaseActivity() {

    private var chooseMenuIndex = 0
    private var bottomMenuTitles = setOf("message", "contact", "group", "mine")

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val adapter = MainFragmentAdapter(this)
        binding.vpContainer.adapter = adapter
        binding.vpContainer.isUserInputEnabled = false

        binding.bnvBottom.setOnItemSelectedListener {
            return@setOnItemSelectedListener onBottomItemSelected(it)
        }
        binding.bnvBottom.selectedItemId = binding.bnvBottom[chooseMenuIndex].id
        setTitle(bottomMenuTitles.elementAt(chooseMenuIndex))
    }

    private fun onBottomItemSelected(it: MenuItem): Boolean {
        val pos = bottomMenuTitles.indexOf(it.title)
        binding.vpContainer.setCurrentItem(pos, false)
        return true
    }

}