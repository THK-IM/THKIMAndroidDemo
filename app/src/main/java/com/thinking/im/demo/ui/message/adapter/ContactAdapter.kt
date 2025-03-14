package com.thinking.im.demo.ui.message.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thinking.im.demo.R
import com.thinking.im.demo.repository.api.contact.vo.ContactVo
import com.thinking.im.demo.ui.component.OnObjectViewClickListener

class ContactAdapter : RecyclerView.Adapter<ContactVH>() {

    private val contacts = mutableListOf<ContactVo>()
    var onObjectViewClickListener: OnObjectViewClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactVH {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.itemview_contact, parent, false)
        val holder = ContactVH(view)
        return holder
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(holder: ContactVH, position: Int) {
        holder.bind(contacts[position])
        holder.itemView.setOnClickListener {
            onObjectViewClickListener?.onClick("select", contacts[position])
        }
    }


    fun setData(contactList: List<ContactVo>) {
        val oldSize = contacts.size
        if (oldSize != 0) {
            this.contacts.clear()
            notifyItemRangeRemoved(0, oldSize)
        }
        this.contacts.addAll(contactList)
        notifyItemRangeInserted(0, contactList.size)

    }

    fun addData(contactList: List<ContactVo>) {
        val oldSize = this.contacts.size
        this.contacts.addAll(contactList)
        notifyItemRangeInserted(oldSize, contactList.size)
    }
}