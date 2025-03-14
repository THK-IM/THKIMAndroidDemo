package com.thinking.im.demo.ui.message.contact

import com.thinking.im.demo.repository.api.contact.vo.ContactVo

interface ContactSelectDelegate {

    fun onContactSelected(contactVo: ContactVo)

}