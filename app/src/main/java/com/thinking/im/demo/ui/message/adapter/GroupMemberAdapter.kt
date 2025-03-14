package com.thinking.im.demo.ui.message.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thinking.im.demo.R
import com.thk.im.android.core.SessionRole
import com.thk.im.android.core.db.entity.SessionMember
import com.thk.im.android.core.db.entity.User
import com.thinking.im.demo.ui.component.OnSubviewTagClickListener

class GroupMemberAdapter : RecyclerView.Adapter<GroupMemberVH>() {

    private val sessionUsers = mutableListOf<Pair<SessionMember, User>>()
    var listener: OnSubviewTagClickListener? = null
    var myRole = SessionRole.Member.value

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupMemberVH {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.itemview_group_member, parent, false)
        val holder = GroupMemberVH(view)
        holder.listener = listener
        return holder
    }

    override fun getItemCount(): Int {
        return sessionUsers.size
    }

    override fun onBindViewHolder(holder: GroupMemberVH, position: Int) {
        holder.bind(sessionUsers[position], myRole)
    }


    fun setData(sessionUserList: List<Pair<SessionMember, User>>) {
        val oldSize = sessionUsers.size
        if (oldSize != 0) {
            sessionUsers.clear()
            notifyItemRangeRemoved(0, oldSize)
        }

        sessionUsers.addAll(sessionUserList)
        notifyItemRangeInserted(0, sessionUserList.size)

    }

    fun addData(sessionUserList: List<Pair<SessionMember, User>>) {
        val oldSize = sessionUsers.size
        sessionUsers.addAll(sessionUserList)
        notifyItemRangeInserted(oldSize, sessionUserList.size)
    }

    fun removeData(sessionUser: Pair<SessionMember, User>) {
        var pos: Int? = null
        for ((index, a) in sessionUsers.withIndex()) {
            if (a.second.id == sessionUser.second.id) {
                pos = index
                break
            }
        }
        pos?.let {
            sessionUsers.removeAt(it)
            notifyItemRemoved(it)
        }
    }

    fun clearData() {
        val count = sessionUsers.count()
        if (count == 0) return
        sessionUsers.clear()
        notifyItemRangeRemoved(0, count)
    }

    fun updateData(sessionUser: Pair<SessionMember, User>) {
        var pos: Int? = null
        for ((index, a) in sessionUsers.withIndex()) {
            if (a.second.id == sessionUser.second.id) {
                pos = index
                break
            }
        }
        pos?.let {
            notifyItemChanged(it)
        }
    }
}