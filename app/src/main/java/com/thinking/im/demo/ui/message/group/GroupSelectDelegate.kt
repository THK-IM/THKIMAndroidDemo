package com.thinking.im.demo.ui.message.group

import com.thinking.im.demo.repository.api.group.vo.GroupVo


interface GroupSelectDelegate {

    fun onGroupSelected(group: GroupVo)

}