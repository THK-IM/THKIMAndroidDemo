package com.thinking.im.demo.ui.component.dialog.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.thinking.im.demo.databinding.ItemviewBottomMenuBinding

class BottomMenuVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding = ItemviewBottomMenuBinding.bind(itemView)

    fun bind(menuText: String) {
        binding.tvMenu.text = menuText
    }

}