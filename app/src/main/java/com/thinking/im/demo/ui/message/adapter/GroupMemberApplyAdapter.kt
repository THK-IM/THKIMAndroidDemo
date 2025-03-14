package com.thinking.im.demo.ui.message.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thinking.im.demo.R
import com.thinking.im.demo.repository.api.group.vo.GroupMemberApplyVo
import com.thinking.im.demo.ui.component.OnSubviewTagClickListener

class GroupMemberApplyAdapter : RecyclerView.Adapter<GroupMemberApplyVH>() {

    private val applies = mutableListOf<GroupMemberApplyVo>()
    var onSubviewTagClickListener: OnSubviewTagClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupMemberApplyVH {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.itemview_group_member_apply, parent, false)
        val holder = GroupMemberApplyVH(view)
        holder.listener = onSubviewTagClickListener
        return holder
    }

    override fun getItemCount(): Int {
        return applies.size
    }

    override fun onBindViewHolder(holder: GroupMemberApplyVH, position: Int) {
        holder.bind(applies[position])
    }


    fun setData(applyList: List<GroupMemberApplyVo>) {
        val oldSize = applies.size
        if (oldSize != 0) {
            applies.clear()
            notifyItemRangeRemoved(0, oldSize)
        }

        applies.addAll(applyList)
        notifyItemRangeInserted(0, applies.size)

    }

    fun addData(applyList: List<GroupMemberApplyVo>) {
        val oldSize = applies.size
        applies.addAll(applyList)
        notifyItemRangeInserted(oldSize, applyList.size)
    }

    fun updateData(apply: GroupMemberApplyVo) {
        var pos: Int? = null
        for ((index, a) in applies.withIndex()) {
            if (a.id == apply.id) {
                pos = index
                break
            }
        }
        pos?.let {
            applies[it] = apply
            notifyItemChanged(it)
        }
    }

    fun clearData() {
        val count = applies.count()
        if (count == 0) return
        applies.clear()
        notifyItemRangeRemoved(0, count)
    }
}