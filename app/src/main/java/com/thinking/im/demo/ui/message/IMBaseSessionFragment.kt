package com.thinking.im.demo.ui.message

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.thinking.im.demo.R
import com.thinking.im.demo.ui.component.dialog.BottomMenuDialog
import com.thk.im.android.core.IMCoreManager
import com.thk.im.android.core.SessionStatus
import com.thk.im.android.core.SessionType
import com.thk.im.android.core.base.BaseSubscriber
import com.thk.im.android.core.base.LLog
import com.thk.im.android.core.base.RxTransform
import com.thk.im.android.core.db.entity.Contact
import com.thk.im.android.core.db.entity.Session
import com.thk.im.android.ui.fragment.IMSessionFragment

class IMBaseSessionFragment : IMSessionFragment() {

    private val observer = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            onDataChanged()
        }

        override fun onStateRestorationPolicyChanged() {
            super.onStateRestorationPolicyChanged()
            onDataChanged()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            onDataChanged()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            onDataChanged()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount)
            onDataChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            super.onItemRangeChanged(positionStart, itemCount, payload)
            onDataChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            super.onItemRangeChanged(positionStart, itemCount)
            onDataChanged()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionAdapter.registerAdapterDataObserver(observer)


    }

    private fun onContactUpdate(c: Contact) {
        for ((pos, s) in sessionAdapter.getSessionList().withIndex()) {
            if (s.type == SessionType.Single.value) {
                if (s.entityId == c.id) {
                    sessionAdapter.notifyItemChanged(pos)
                }
            }
        }
    }

    private fun clearAllUnread() {
        sessionAdapter.setAllRead()
    }

    override fun deleteSession(session: Session) {
        val subscriber = object : BaseSubscriber<Void>() {
            override fun onNext(t: Void?) {
            }

            override fun onError(t: Throwable?) {
                t?.message?.let {
                    LLog.e(it)
                }
            }
        }
        IMCoreManager.messageModule.deleteSession(session, false)
            .compose(RxTransform.flowableToMain()).subscribe(subscriber)
        disposables.add(subscriber)
    }

    private fun onDataChanged() {
        bgLayout.visibility = if (sessionAdapter.itemCount > 0) View.GONE else View.VISIBLE
    }

    override fun longClickSession(session: Session): Boolean {
        val top = ContextCompat.getString(requireContext(), com.thk.im.android.ui.R.string.top)
        val cancelTop =
            ContextCompat.getString(requireContext(), com.thk.im.android.ui.R.string.cancel_top)
        val silence =
            ContextCompat.getString(requireContext(), com.thk.im.android.ui.R.string.silence)
        val cancelSilence =
            ContextCompat.getString(requireContext(), com.thk.im.android.ui.R.string.cancel_silence)
        val delete =
            ContextCompat.getString(requireContext(), com.thk.im.android.ui.R.string.delete)
        val cancel = ContextCompat.getString(requireContext(), R.string.cancel)
        val menus = mutableListOf<String>()
        if (session.topTimestamp != 0L) {
            menus.add(cancelTop)
        } else {
            menus.add(top)
        }
        if (session.status.and(SessionStatus.Silence.value) != 0) {
            menus.add(cancelSilence)
        } else {
            menus.add(silence)
        }
        menus.add(delete)
        menus.add(cancel)
        BottomMenuDialog.show(requireActivity(), menus) { text ->
            when (text) {
                top, cancelTop -> {
                    if (session.topTimestamp > 0) {
                        session.topTimestamp = 0
                    } else {
                        session.topTimestamp = IMCoreManager.severTime
                    }
                    updateSession(session)
                }

                silence, cancelSilence -> {
                    session.status = session.status.xor(SessionStatus.Silence.value)
                    updateSession(session)
                }

                delete -> {
                    deleteSession(session)
                }

                else -> {
                }
            }
        }
        return true
    }
}