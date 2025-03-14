package com.thinking.im.demo.module.im

import android.content.Context
import android.content.Intent
import com.thinking.im.demo.R
import com.thinking.im.demo.repository.ChatFunctionMemberPrivacy
import com.thinking.im.demo.ui.base.BaseActivity
import com.thinking.im.demo.ui.message.contact.ContactChatSettingActivity
import com.thinking.im.demo.ui.message.contact.ContactMessageActivity
import com.thinking.im.demo.ui.message.group.GroupMessageActivity
import com.thk.im.android.core.SessionRole
import com.thk.im.android.core.SessionType
import com.thk.im.android.core.base.utils.ToastUtils
import com.thk.im.android.core.db.entity.Group
import com.thk.im.android.core.db.entity.Message
import com.thk.im.android.core.db.entity.Session
import com.thk.im.android.core.db.entity.User
import com.thk.im.android.ui.protocol.IMPageRouter

class IMExternalPageRouter : IMPageRouter {

    override fun openSession(ctx: Context, session: Session) {
        if (session.type == SessionType.Single.value) {
            val intent = Intent(ctx, ContactMessageActivity::class.java)
            intent.putExtra("session", session)
            ctx.startActivity(intent)
        } else if (session.type == SessionType.SuperGroup.value || session.type == SessionType.Group.value) {
            val intent = Intent(ctx, GroupMessageActivity::class.java)
            intent.putExtra("session", session)
            ctx.startActivity(intent)
        }
    }

    override fun openUserPage(ctx: Context, user: User, session: Session) {
        if (session.functionFlag != 0L) {
            if (session.functionFlag.and(ChatFunctionMemberPrivacy) == 0L || session.role > SessionRole.Member.value) {
                if (session.type == SessionType.Single.value) {
                    ContactChatSettingActivity.start(ctx, user, session)
                } else {
                    ToastUtils.show("打开他人主页")
                }
            } else {
                (ctx as? BaseActivity)?.showToast(R.string.group_privacy_protect)
            }
        }
    }

    override fun openGroupPage(ctx: Context, group: Group, session: Session) {

    }


    override fun openLiveCall(ctx: Context, session: Session) {
    }

    override fun openMsgReadStatusPage(ctx: Context, session: Session, message: Message) {
    }

}