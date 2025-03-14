package com.thinking.im.demo.ui.fragment.adapter

import com.thk.im.android.core.db.entity.Contact

interface RelationListener {

    fun onContactClick(contact: Contact, opr: String)
}