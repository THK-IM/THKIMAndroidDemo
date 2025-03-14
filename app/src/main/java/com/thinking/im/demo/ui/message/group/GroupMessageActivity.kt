package com.thinking.im.demo.ui.message.group

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.thinking.im.demo.R
import com.thinking.im.demo.databinding.ActivityGroupMessageBinding
import com.thk.im.android.core.IMCoreManager
import com.thk.im.android.core.IMEvent
import com.thk.im.android.core.SessionStatus
import com.thk.im.android.core.base.BaseSubscriber
import com.thk.im.android.core.base.RxTransform
import com.thk.im.android.core.db.entity.Group
import com.thk.im.android.core.db.entity.Session
import com.thk.im.android.core.event.XEventBus
import com.thk.im.android.ui.manager.IMUIManager
import com.thinking.im.demo.ui.message.BaseMessageActivity


class GroupMessageActivity : BaseMessageActivity() {

    private lateinit var binding: ActivityGroupMessageBinding

    private var group: Group? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityGroupMessageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        XEventBus.observe(this, IMEvent.SessionUpdate.value, observer = Observer<Session> {
            val old = session() ?: return@Observer
            if (old.id == it.id) {
                intent.putExtra("session", it)
                initData()
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
        binding.tbTop.tbIvBack.setOnClickListener {
            finish()
        }
    }

    private fun onMoreClick() {
        val session = session() ?: return
        GroupChatSettingActivity.start(this, session)
    }

    private fun initData() {
        val session = session() ?: return
        binding.tbTop.tvTitle.text = session.name
        binding.tbTop.tbIvStatus.visibility =
            if (session.status.and(SessionStatus.Silence.value) > 0) View.VISIBLE else View.GONE
        binding.tbTop.tvMemberCount.text = String.format(
            ContextCompat.getString(this, R.string.x_member_chat),
            session.memberCount
        )

        val subscriber = object : BaseSubscriber<Group>() {
            override fun onNext(t: Group?) {
                t?.let {
                    group = it
                    binding.tbTop.tvTitle.text = it.name
                }
            }
        }
        IMCoreManager.groupModule.findById(session.entityId)
            .compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
        addDispose(subscriber)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}

