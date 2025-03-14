package com.thinking.im.demo.ui.fragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thinking.im.demo.R
import com.thinking.im.demo.repository.api.user.vo.BasisUserVo
import com.thk.im.android.core.db.entity.Contact

class RelationAdapter : RecyclerView.Adapter<RelationVH>() {

    var relationListener: RelationListener? = null

    private val contactUsers = mutableListOf<Pair<Contact, BasisUserVo>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RelationVH {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.itemview_relation, parent, false)
        return RelationVH(view)
    }

    override fun getItemCount(): Int {
        return contactUsers.size
    }

    override fun onBindViewHolder(holder: RelationVH, position: Int) {
        holder.relationListener = relationListener
        holder.bind(contactUsers[position])
    }


    fun setData(contactUserVos: List<Pair<Contact, BasisUserVo>>) {
        val oldSize = contactUsers.size
        if (oldSize != 0) {
            contactUsers.clear()
            notifyItemRangeRemoved(0, oldSize)
        }

        contactUsers.addAll(contactUserVos)
        notifyItemRangeInserted(0, contactUserVos.size)

    }

    fun addData(contactUserVos: List<Pair<Contact, BasisUserVo>>) {
        val oldSize = contactUsers.size
        contactUsers.addAll(contactUserVos)
        notifyItemRangeInserted(oldSize, contactUserVos.size)
    }

    fun updateData(contact: Contact) {
        var index: Int? = null
        for (i in 0 until this.contactUsers.count()) {
            if (this.contactUsers[i].first.id == contact.id) {
                index = i
            }
        }
        index?.let {
            val old = this.contactUsers[it]
            this.contactUsers[it] = Pair(contact, old.second)
            notifyItemChanged(it)
        }
    }

    fun clearData() {
        val count = contactUsers.count()
        if (count == 0) return
        contactUsers.clear()
        notifyItemRangeRemoved(0, count)
    }

}