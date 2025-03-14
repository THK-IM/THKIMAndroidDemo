package com.thinking.im.demo.ui.message.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thinking.im.demo.R
import com.thinking.im.demo.repository.api.group.vo.GroupVo
import com.thinking.im.demo.ui.component.OnObjectViewClickListener


class GroupAdapter : RecyclerView.Adapter<GroupVH>() {

    private val groups = mutableListOf<GroupVo>()
    var onObjectViewClickListener: OnObjectViewClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupVH {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.itemview_group, parent, false)
        val holder = GroupVH(view)
        return holder
    }

    override fun getItemCount(): Int {
        return groups.size
    }

    override fun onBindViewHolder(holder: GroupVH, position: Int) {
        holder.bind(groups[position])
        holder.itemView.setOnClickListener {
            onObjectViewClickListener?.onClick("select", groups[position])
        }
    }


    fun setData(groupList: List<GroupVo>) {
        val oldSize = groupList.size
        if (oldSize != 0) {
            this.groups.clear()
            notifyItemRangeRemoved(0, oldSize)
        }
        this.groups.addAll(groupList)
        notifyItemRangeInserted(0, groupList.size)

    }

    fun addData(groupList: List<GroupVo>) {
        val oldSize = this.groups.size
        this.groups.addAll(groupList)
        notifyItemRangeInserted(oldSize, groupList.size)
    }
}