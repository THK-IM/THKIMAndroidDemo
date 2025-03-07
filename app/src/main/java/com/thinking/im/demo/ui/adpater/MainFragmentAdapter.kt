package com.thk.im.android.ui.main.adpater

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.thk.im.android.ui.fragment.IMSessionFragment
import com.thinking.im.demo.ui.fragment.ContactFragment
import com.thinking.im.demo.ui.fragment.GroupFragment
import com.thinking.im.demo.ui.fragment.MineFragment

class MainFragmentAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                IMSessionFragment()
            }

            1 -> {
                ContactFragment()
            }

            2 -> {
                GroupFragment()
            }

            3 -> {
                MineFragment()
            }

            else -> {
                Fragment()
            }
        }
    }
}