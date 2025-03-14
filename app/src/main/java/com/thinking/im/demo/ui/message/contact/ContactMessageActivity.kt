package com.thinking.im.demo.ui.message.contact

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.thinking.im.demo.R
import com.thinking.im.demo.databinding.ActivityContactMessageBinding
import com.thinking.im.demo.repository.ContactRelation
import com.thinking.im.demo.repository.UIEvent
import com.thinking.im.demo.ui.message.BaseMessageActivity
import com.thk.im.android.core.IMCoreManager
import com.thk.im.android.core.IMEvent
import com.thk.im.android.core.SessionStatus
import com.thk.im.android.core.base.BaseSubscriber
import com.thk.im.android.core.base.RxTransform
import com.thk.im.android.core.db.entity.Contact
import com.thk.im.android.core.db.entity.Session
import com.thk.im.android.core.db.entity.User
import com.thk.im.android.core.event.XEventBus
import com.thk.im.android.ui.manager.IMChatFunction
import com.thk.im.android.ui.manager.IMUIManager
import io.reactivex.Flowable


class ContactMessageActivity : BaseMessageActivity() {

    private lateinit var binding: ActivityContactMessageBinding
    private var contact: Contact? = null
    private var user: User? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityContactMessageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        XEventBus.observe(this, IMEvent.SessionUpdate.value, observer = Observer<Session> {
            val old = session() ?: return@Observer
            if (old.id == it.id) {
                intent.putExtra("session", it)
                updateSessionStatusView()
            }
        })

        XEventBus.observe(this, UIEvent.RelationUpdate.value, observer = Observer<Contact> {
            val old = contact ?: return@Observer
            if (old.id == it.id) {
                this@ContactMessageActivity.contact = it
                updateContactView()
            }
        })

        initView()
        initData()
    }

    private fun initView() {
        IMUIManager.uiResourceProvider?.inputBgColor()?.let {
            binding.root.setBackgroundColor(it)
        }
        initMessageFragment(binding.fragmentMessageContainer.id)
        binding.tbTop.tbIvMore.setOnClickListener {
            onMoreClick()
        }
        binding.tbTop.tbTvRelation.setOnClickListener {
            onRelationClick()
        }
        binding.tbTop.tbIvBack.setOnClickListener {
            finish()
        }
        updateSessionStatusView()
    }

    private fun initData() {
        val session = session() ?: return
        if (session.functionFlag.and(IMChatFunction.BaseInput.value) == 0L) {
            binding.tbTop.tbIvMore.visibility = View.GONE
            binding.tbTop.tbTvRelation.visibility = View.GONE
        }
        val subscriber = object : BaseSubscriber<Pair<User, Contact>>() {
            override fun onNext(t: Pair<User, Contact>?) {
                t?.let { p ->
                    this@ContactMessageActivity.user = p.first
                    this@ContactMessageActivity.contact = p.second
                    updateContactView()
                }
            }

            override fun onError(t: Throwable?) {
                super.onError(t)
                t?.let { tr ->
                    showError(tr)
                }
            }
        }
        IMCoreManager.userModule.queryUser(session.entityId)
            .flatMap { user ->
                IMCoreManager.contactModule.queryContactByUserId(user.id).flatMap { contact ->
                    Flowable.just(Pair(user, contact))
                }
            }
            .compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
        addDispose(subscriber)
    }

    private fun updateSessionStatusView() {
        val session = session() ?: return
        binding.tbTop.tbIvStatus.visibility =
            if (session.status.and(SessionStatus.Silence.value) > 0) View.VISIBLE else View.GONE
    }


    private fun updateContactView() {
        val user = user ?: return
        val contact = contact ?: return

        var nickname = user.nickname
        if (contact.noteName != null && contact.noteName!!.isNotEmpty()) {
            nickname = contact.noteName!!
        }
        binding.tbTop.tbTvTitle.text = nickname
        if (contact.relation.and(ContactRelation.Black.value) != 0) {
            binding.tbTop.tbTvRelation.text =
                ContextCompat.getString(this, R.string.remove_out_blacklist)
        } else {
            if (contact.relation.and(ContactRelation.Fellow.value) != 0) {
                if (contact.relation.and(ContactRelation.BeFellow.value) != 0) {
                    binding.tbTop.tbTvRelation.text =
                        ContextCompat.getString(this, R.string.follow_each_other)
                } else {
                    binding.tbTop.tbTvRelation.text =
                        ContextCompat.getString(this, R.string.had_followed)
                }
            } else {
                binding.tbTop.tbTvRelation.text = ContextCompat.getString(this, R.string.to_follow)
            }
        }
    }

    private fun onRelationClick() {
        val contact = contact ?: return
        if (contact.relation.and(ContactRelation.Black.value) != 0) {
            black(contact, false)
        } else {
            if (contact.relation.and(ContactRelation.Fellow.value) != 0) {
                follow(contact = contact, toFollow = false)
            } else {
                follow(contact, true)
            }
        }
    }

    private fun onMoreClick() {
        val user = user ?: return
        val session = session() ?: return
        ContactChatSettingActivity.start(this, user, session)
    }
}

