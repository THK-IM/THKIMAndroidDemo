package com.thinking.im.demo.ui.base

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.thinking.im.demo.R
import com.thinking.im.demo.repository.ContactRelation
import com.thinking.im.demo.repository.DataRepository
import com.thinking.im.demo.repository.UIEvent
import com.thinking.im.demo.repository.api.contact.vo.BlackVo
import com.thinking.im.demo.repository.api.contact.vo.ContactSessionCreateVo
import com.thinking.im.demo.repository.api.contact.vo.FollowVo
import com.thinking.im.demo.ui.component.dialog.BottomMenuDialog
import com.thk.im.android.core.IMCoreManager
import com.thk.im.android.core.base.BaseSubscriber
import com.thk.im.android.core.base.RxTransform
import com.thk.im.android.core.db.entity.Contact
import com.thk.im.android.core.db.entity.Session
import com.thk.im.android.core.db.entity.User
import com.thk.im.android.core.event.XEventBus
import com.thk.im.android.ui.manager.IMUIManager
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

open class BaseRelationFragment : BaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        XEventBus.observe(this, UIEvent.RelationUpdate.value, Observer<Contact> {
            contactRelationUpdated(it)
        })
    }

    fun queryContact(id: Long) {
        val subscriber = object : BaseSubscriber<Contact>() {
            override fun onNext(t: Contact?) {
                t?.let { c ->
                    queryContactSuccess(c)
                }
            }
        }
        IMCoreManager.contactModule.queryContactByUserId(id)
            .compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
        addDispose(subscriber)
    }

    fun black(contact: Contact, toBlack: Boolean) {
        if (contact.id == IMCoreManager.uId) {
            showToast(R.string.can_not_follow_yourself)
            return
        }
        if (toBlack) {
            val activity = requireActivity() as? BaseActivity
            activity?.let { a ->
                a.showYesOrNoDialog(
                    ContextCompat.getString(
                        requireContext(),
                        R.string.is_pull_black
                    )
                ) {
                    if (it.id == R.id.tv_dialog_ok) {
                        doBlack(contact, true)
                    }
                }
                return
            }
        }
        doBlack(contact, toBlack)
    }

    private fun doBlack(contact: Contact, toBlack: Boolean) {
        val blackVo = BlackVo(IMCoreManager.uId, contact.id)
        val subscriber = object : BaseSubscriber<Void>() {
            override fun onNext(t: Void?) {
            }

            override fun onComplete() {
                super.onComplete()
                if (toBlack) {
                    showToast(R.string.pull_black_success)
                    contact.relation = ContactRelation.Black.value
                } else {
                    showToast(R.string.remove_out_blacklist_success)
                    contact.relation = 0
                }
                updateContactSuccess(contact)
            }

            override fun onError(t: Throwable?) {
                super.onError(t)
                t?.let {
                    showError(it)
                }
            }
        }
        if (toBlack) {
            DataRepository.contactApi.black(blackVo)
                .concatWith(updateLocalBlackContact(contact.id, true))
                .compose(RxTransform.flowableToMain())
                .subscribe(subscriber)
            addDispose(subscriber)
        } else {
            DataRepository.contactApi.cancelBlack(blackVo)
                .concatWith(updateLocalBlackContact(contact.id, false))
                .compose(RxTransform.flowableToMain())
                .subscribe(subscriber)
            addDispose(subscriber)
        }
    }

    fun follow(contact: Contact, toFollow: Boolean) {
        if (contact.id == IMCoreManager.uId) {
            showToast(R.string.can_not_follow_yourself)
            return
        }
        if (contact.relation.and(ContactRelation.Black.value) != 0) {
            showToast(R.string.your_black_him_cannot_follow)
            return
        }
        if (!toFollow) {
            val activity = requireActivity() as? BaseActivity
            activity?.let { a ->
                a.showYesOrNoDialog(
                    ContextCompat.getString(
                        requireContext(),
                        R.string.is_cancel_follow
                    )
                ) {
                    if (it.id == R.id.tv_dialog_ok) {
                        doFollow(contact, false)
                    }
                }
                return
            }
        } else {
            doFollow(contact, true)
        }
    }


    private fun doFollow(contact: Contact, toFollow: Boolean) {
        val followVo = FollowVo(IMCoreManager.uId, contact.id)
        val subscriber = object : BaseSubscriber<Void>() {
            override fun onNext(t: Void?) {
            }

            override fun onComplete() {
                super.onComplete()
                if (toFollow) {
                    showToast(R.string.follow_success)
                    contact.relation = contact.relation.or(ContactRelation.Fellow.value)
                } else {
                    showToast(R.string.cancel_follow_success)
                    contact.relation =
                        contact.relation.and(contact.relation.xor(ContactRelation.Fellow.value))
                }
                updateContactSuccess(contact)
            }

            override fun onError(t: Throwable?) {
                super.onError(t)
                t?.let {
                    showError(it)
                }
            }
        }
        if (toFollow) {
            DataRepository.contactApi.follow(followVo)
                .concatWith(updateLocalFollowContact(contact.id, true))
                .compose(RxTransform.flowableToMain())
                .subscribe(subscriber)
            addDispose(subscriber)
        } else {
            DataRepository.contactApi.cancelFollow(followVo)
                .concatWith(updateLocalFollowContact(contact.id, false))
                .compose(RxTransform.flowableToMain())
                .subscribe(subscriber)
            addDispose(subscriber)
        }
    }


    private fun updateLocalFollowContact(userId: Long, toFollow: Boolean): Flowable<Void> {
        return Flowable.create({
            val contact =
                IMCoreManager.getImDataBase().contactDao().findByUserId(userId)
            contact?.let { c ->
                if (toFollow) {
                    c.relation = c.relation.or(ContactRelation.Fellow.value)
                } else {
                    c.relation = c.relation.and(c.relation.xor(ContactRelation.Fellow.value))
                }
                IMCoreManager.getImDataBase().contactDao()
                    .insertOrReplace(listOf(c))
            }
            it.onComplete()
        }, BackpressureStrategy.LATEST)
    }

    private fun updateLocalBlackContact(userId: Long, toBlack: Boolean): Flowable<Void> {
        return Flowable.create({
            val contact =
                IMCoreManager.getImDataBase().contactDao().findByUserId(userId)
            contact?.let { c ->
                if (toBlack) {
                    c.relation = ContactRelation.Black.value
                } else {
                    c.relation = 0
                }
                IMCoreManager.getImDataBase().contactDao()
                    .insertOrReplace(listOf(c))
            }
            it.onComplete()
        }, BackpressureStrategy.LATEST)
    }

    private fun report(id: Long) {
        showLoading()
        val subscriber = object : BaseSubscriber<User>() {
            override fun onNext(t: User?) {
                showToast("TODO")
                dismissLoading()
            }

            override fun onError(t: Throwable?) {
                super.onError(t)
                t?.let {
                    showError(it)
                }
                dismissLoading()
            }
        }

        IMCoreManager.userModule.queryUser(id).compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
        addDispose(subscriber)
    }


    fun showContactMenu(contact: Contact) {
        val pullBlackText = ContextCompat.getString(requireContext(), R.string.pull_to_black)
        val cancelBlackText =
            ContextCompat.getString(requireContext(), R.string.remove_out_blacklist)
        val blackText =
            if (contact.relation.and(ContactRelation.Black.value) != 0) cancelBlackText else pullBlackText
        val reportText = ContextCompat.getString(requireContext(), R.string.report)
        BottomMenuDialog.show(requireContext(), listOf(blackText, reportText)) { text ->
            when (text) {
                blackText -> {
                    black(contact, true)
                }

                cancelBlackText -> {
                    black(contact, false)
                }

                reportText -> {
                    report(contact.id)
                }

                else -> {

                }
            }
        }
    }

    fun chatContact(contact: Contact) {
        val subscriber = object : BaseSubscriber<Session>() {
            override fun onNext(t: Session?) {
                t?.let {
                    openSession(t)
                }
            }

            override fun onError(t: Throwable?) {
                super.onError(t)
                t?.let {
                    showError(it)
                }
            }
        }

        if (contact.sessionId != null) {
            IMCoreManager.messageModule.getSession(contact.sessionId!!)
                .compose(RxTransform.flowableToMain())
                .subscribe(subscriber)
        } else {
            val req = ContactSessionCreateVo(IMCoreManager.uId, contact.id)
            DataRepository.contactApi.createContactSession(req)
                .flatMap { sessionVo ->
                    val session = sessionVo.toSession()
                    contact.sessionId = session.id
                    IMCoreManager.getImDataBase().contactDao().insertOrReplace(listOf(contact))
                    Flowable.just(session)
                }
                .compose(RxTransform.flowableToMain())
                .subscribe(subscriber)
        }

    }

    open fun openSession(session: Session) {
        IMUIManager.pageRouter?.openSession(requireActivity(), session)
    }

    open fun queryContactSuccess(contact: Contact) {

    }

    private fun updateContactSuccess(contact: Contact) {
        XEventBus.post(UIEvent.RelationUpdate.value, contact)
    }

    open fun contactRelationUpdated(contact: Contact) {

    }
}