package com.thinking.im.demo.module.im

import android.app.Application
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import com.thinking.im.demo.utils.withAlpha
import com.thk.im.android.core.base.utils.AppUtils
import com.thk.im.android.core.base.utils.ShapeUtils
import com.thk.im.android.core.base.utils.ToastUtils
import com.thk.im.android.core.db.entity.Message
import com.thk.im.android.core.db.entity.Session
import com.thk.im.android.core.db.entity.User
import com.thk.im.android.ui.manager.IMMsgPosType
import com.thk.im.android.ui.protocol.IMUIResourceProvider

class IMResourceProvider(private val app: Application) : IMUIResourceProvider {

    private var emojis = listOf(
        "😀", "😃", "😄", "😁", "😆", "😅", "🤣", "😂", "🙂", "🙃", "🫠", "😉", "😊", "😇",
        "🥰", "😍", "🤩", "😘", "😗", "😚", "😙", "🥲", "😋", "😛", "😜", "🤪", "😝", "🤑",
        "🤗", "🤭", "🫢", "🫣", "🤫", "🤔", "🫡", "😌", "😔", "😪", "🤤", "😴", "😭", "😱",
        "😖", "😣", "😞", "😓", "😩", "😫", "🥱", "😤", "😡", "🤡", "🤖", "😺", "😸", "😹",
        "😻", "😼", "😽", "🙀", "😿", "😾", "💔", "🩷", "💢", "💥", "💫", "💦", "💋", "💤",
        "✅️", "❎️", "👋", "🤚", "🖐️", "✋️", "🖖", "🫱", "🫲", "🫳", "🫴", "🫷", "🫸", "👌",
        "🤌", "🤏", "✌️", "🤞", "🫰", "🤟", "🤘", "🤙", "👈️", "👉️", "👆️", "🖕", "👇️", "☝️",
        "🫵", "👍️", "👎️", "✊️", "👊", "🤛", "🤜", "👏", "🙌", "🫶", "👐", "🤲", "🤝", "🙏",
        "👄", "🫦", "🐵", "🐒", "🦍", "🦧", "🐶", "🐕️", "🦮", "🐕‍🦺", "🐩", "🐺", "🦊", "🦝",
        "🐱", "🐈️", "🐈‍⬛", "🦁", "🐯", "🐅", "🐆", "🐴", "🫎", "🫏", "🐎", "🦄", "🦓", "🦌",
        "🦬", "🐮", "🐂", "🐃", "🐄", "🐷", "🐖", "🐗", "🐽", "🐏", "🐑", "🐐", "🐪", "🐫",
        "🦙", "🦒", "🐘", "🦣", "🦏", "🦛", "🐭", "🐁", "🐀", "🐹", "🐰", "🐇", "🐿️", "🦫",
        "🦔", "🦇", "🐻‍“, ❄️", "🐨", "🐼", "🦥", "🦦", "🦨", "🦘", "🦡", "🐾", "🦃", "🐔",
        "🐓", "🐣", "🐤", "🐥", "🐦️", "🐧", "🕊️", "🦅", "🦆", "🦢", "🦉", "🦤", "🪶", "🦩",
        "🦚", "🦜", "🍆", "🌶️"
    )

    override fun avatar(user: User): Int? {
        return null
    }

    override fun unicodeEmojis(): List<String> {
        return emojis
    }

    override fun msgContainer(posType: IMMsgPosType): Int? {
        return null
    }

    override fun msgBubble(message: Message, session: Session?): Drawable {
        val corner = AppUtils.dp2px(8f).toFloat()
        when (message.fUid) {
            0L -> {
                return ShapeUtils.createRectangleDrawable(
                    Color.BLACK.withAlpha(0.3f),
                    Color.BLACK.withAlpha(0.3f),
                    0,
                    floatArrayOf(corner, corner, corner, corner)
                )
            }

            else -> {
                return ShapeUtils.createRectangleDrawable(
                    Color.WHITE, Color.WHITE, 1, floatArrayOf(corner, corner, corner, corner)
                )
            }
        }
    }

    override fun tintColor(): Int {
        return Color.parseColor("#FF5580")
    }

    override fun panelBgColor(): Int {
        return Color.parseColor("#FFFFFF")
    }

    override fun showToast(context: Context, toast: String) {
        ToastUtils.showShort(toast)
    }

    override fun layoutBgColor(): Int {
        return Color.parseColor("#F2F2F2")
    }

    override fun inputBgColor(): Int {
        return Color.parseColor("#F2F2F2")
    }

    override fun inputTextColor(): Int {
        return Color.parseColor("#333333")
    }

    override fun tipTextColor(): Int {
        return Color.parseColor("#666666")
    }

    override fun messageSelectImageResource(): Int? {
        return null
    }

    override fun supportFunction(session: Session, functionFlag: Long): Boolean {
        return (session.functionFlag.and(functionFlag) != 0L)
    }

    override fun canAtAll(session: Session): Boolean {
        return true
    }

}