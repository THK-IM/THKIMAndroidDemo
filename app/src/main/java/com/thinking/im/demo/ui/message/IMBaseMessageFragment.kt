package com.thinking.im.demo.ui.message

import android.view.View
import com.thk.im.android.core.db.entity.Message
import com.thk.im.android.ui.fragment.IMMessageFragment

class IMBaseMessageFragment : IMMessageFragment() {

    override fun showLoading(text: String) {
        (requireActivity() as? BaseMessageActivity)?.showLoading(false, text)
    }

    override fun dismissLoading() {
        (requireActivity() as? BaseMessageActivity)?.dismissLoading()
    }

    override fun showToast(text: String) {
        (requireActivity() as? BaseMessageActivity)?.showToast(text)
    }

    override fun showError(throwable: Throwable) {
        (requireActivity() as? BaseMessageActivity)?.showError(throwable)
    }

    override fun showMessage(text: String, success: Boolean) {
        (requireActivity() as? BaseMessageActivity)?.showToast(text)
    }

    override fun onMsgClick(msg: Message, position: Int, originView: View) {
//        if (msg.type == MarsMsgType.GroupMemberApply.value) {
//            val body = msg.content ?: return
//            val groupMemberApplyVo = Gson().fromJson(body, GroupMemberApplyVo::class.java)
//            FamilyMemberReviewActivity.start(requireActivity(), groupMemberApplyVo)
//        } else {
        super.onMsgClick(msg, position, originView)
//        }
    }
}