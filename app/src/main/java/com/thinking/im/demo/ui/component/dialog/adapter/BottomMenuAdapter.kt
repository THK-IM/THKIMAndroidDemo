package com.thinking.im.demo.ui.component.dialog.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thinking.im.demo.R
import com.thinking.im.demo.ui.component.OnSubviewClickListener

class BottomMenuAdapter(private val ctx: Context) : RecyclerView.Adapter<BottomMenuVH>() {

    private val menuTexts = mutableListOf<String>()
    var onSubviewClickListener: OnSubviewClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomMenuVH {
        val view = LayoutInflater.from(ctx).inflate(R.layout.itemview_bottom_menu, parent, false)
        return BottomMenuVH(view)
    }

    override fun getItemCount(): Int {
        return menuTexts.count()
    }

    override fun onBindViewHolder(holder: BottomMenuVH, position: Int) {
        val menuText = menuTexts[position]
        holder.bind(menuText)
        holder.itemView.setOnClickListener {
            onSubviewClickListener?.onClick(menuText)
        }
    }

    fun setMenus(menus: List<String>) {
        val oldSize = menus.size
        menuTexts.clear()
        notifyItemMoved(0, oldSize)
        menuTexts.addAll(menus)
        notifyItemRangeInserted(0, menus.count())
    }
}