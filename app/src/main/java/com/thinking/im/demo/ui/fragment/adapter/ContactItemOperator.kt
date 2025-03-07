package com.thinking.im.demo.ui.fragment.adapter

import com.thk.im.android.core.db.entity.Contact

interface ContactItemOperator {
    fun onItemClick(contact: Contact)
}