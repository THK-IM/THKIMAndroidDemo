package com.thinking.im.demo.module.im

import android.content.Context
import com.thk.im.android.core.db.entity.Group
import com.thk.im.android.core.db.entity.Message
import com.thk.im.android.core.db.entity.Session
import com.thk.im.android.core.db.entity.User
import com.thk.im.android.ui.protocol.IMPageRouter

class IMExternalPageRouter : IMPageRouter {

    override fun openSession(ctx: Context, session: Session) {
    }

    override fun openUserPage(ctx: Context, user: User, session: Session) {
    }

    override fun openGroupPage(ctx: Context, group: Group, session: Session) {

    }


    override fun openLiveCall(ctx: Context, session: Session) {
    }

    override fun openMsgReadStatusPage(ctx: Context, session: Session, message: Message) {
    }

}