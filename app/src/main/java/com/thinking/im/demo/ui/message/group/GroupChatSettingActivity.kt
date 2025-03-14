package com.thinking.im.demo.ui.message.group

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.thinking.im.demo.R
import com.thinking.im.demo.databinding.ActivityGroupSettingBinding
import com.thinking.im.demo.repository.DataRepository
import com.thinking.im.demo.repository.UIEvent
import com.thinking.im.demo.repository.api.group.vo.DisbandGroupReq
import com.thinking.im.demo.repository.api.group.vo.GroupVo
import com.thinking.im.demo.repository.api.group.vo.LeaveGroupReq
import com.thinking.im.demo.ui.base.BaseMediaUploadActivity
import com.thk.im.android.core.IMCoreManager
import com.thk.im.android.core.IMEvent
import com.thk.im.android.core.SessionRole
import com.thk.im.android.core.base.BaseSubscriber
import com.thk.im.android.core.base.RxTransform
import com.thk.im.android.core.base.extension.setShape
import com.thk.im.android.core.db.entity.Session
import com.thk.im.android.core.event.XEventBus
import io.reactivex.Flowable

class GroupChatSettingActivity : BaseMediaUploadActivity() {

    companion object {
        fun start(ctx: Context, session: Session) {
            val intent = Intent(ctx, GroupChatSettingActivity::class.java)
            intent.putExtra("session", session)
            ctx.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityGroupSettingBinding

    private var groupVo: GroupVo? = null

    private fun session(): Session? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("session", Session::class.java)
        } else {
            intent.getParcelableExtra("session")
        }
    }

    override fun getToolbar(): Toolbar {
        return binding.tbTop.toolbar
    }

    override fun toolbarTitle(): String {
        return ContextCompat.getString(this, R.string.chat_setting)
    }

    override fun appendixBusiness(): String {
        return "group_avatar"
    }

    override fun compressSize(): Int {
        return 200 * 1024
    }

    override fun uploadSuccess(url: String) {
        binding.lyGroupInfo.uploadSuccess(url)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityGroupSettingBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        XEventBus.observe(this, IMEvent.SessionUpdate.value, Observer<Session> {
            val session = session() ?: return@Observer
            if (it.id == session.id) {
                intent.putExtra("session", it)
                updateSessionView()
            }
        })

        XEventBus.observe(this, UIEvent.GroupUpdate.value, Observer<GroupVo> {
            if (it.id == groupVo?.id) {
                groupVo = it
                updateView()
            }
        })

        initView()
        initData()
    }

    private fun initView() {
        binding.lyGroupInfo.setShape(
            Color.WHITE, floatArrayOf(10f, 10f, 10f, 10f), false
        )
        binding.lyGroupMember.setShape(
            Color.WHITE, floatArrayOf(10f, 10f, 10f, 10f), false
        )
        binding.lySessionSetting.setShape(
            Color.WHITE, floatArrayOf(10f, 10f, 10f, 10f), false
        )

        binding.tvExit.setShape(
            Color.WHITE, floatArrayOf(10f, 10f, 10f, 10f), false
        )

        binding.tvExit.setOnClickListener {
            requestExitGroup()
        }
    }

    private fun updateView() {
        val groupVo = groupVo ?: return
        val session = session() ?: return
        binding.lyGroupMember.bind(groupVo, session)
        updateSessionView()
        binding.svContainer.visibility = View.VISIBLE
    }

    private fun updateSessionView() {
        val groupVo = groupVo ?: return
        val session = session() ?: return
        binding.lyGroupInfo.bind(groupVo, session)
        binding.lySessionSetting.bind(groupVo, session)

        val reportText = ContextCompat.getString(this, R.string.report_this_group)
        val spanReportText = SpannableString(reportText)
        val clickSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#888888")
                ds.isUnderlineText = true
            }

            override fun onClick(widget: View) {
                onReportClick()
            }
        }

        spanReportText.setSpan(
            clickSpan,
            0,
            reportText.length,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        binding.tvReport.text = spanReportText
        binding.tvReport.movementMethod = LinkMovementMethod.getInstance()

        if (session.role == SessionRole.Owner.value) {
            binding.tvExit.text = ContextCompat.getString(this, R.string.disband_group)
        } else {
            binding.tvExit.text = ContextCompat.getString(this, R.string.exit_group)
        }
    }


    private fun initData() {
        val session = session() ?: return
        val subscriber = object : BaseSubscriber<GroupVo>() {
            override fun onError(t: Throwable?) {
                super.onError(t)
                t?.let {
                    showError(it)
                }
            }

            override fun onNext(t: GroupVo) {
                this@GroupChatSettingActivity.groupVo = t
                updateView()
            }
        }
        DataRepository.groupApi.queryGroup(session.entityId, 1, 1)
            .flatMap { res ->
                IMCoreManager.db.groupDao().insertOrReplace(listOf(res.group.toGroup()))
                Flowable.just(res.group)
            }
            .compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
        addDispose(subscriber)
    }


    private fun requestExitGroup() {
        val session = session() ?: return
        val groupVo = groupVo ?: return
        val title = if (session.role == SessionRole.Owner.value) {
            ContextCompat.getString(
                this@GroupChatSettingActivity,
                R.string.ready_disband_group
            )
        } else {
            ContextCompat.getString(
                this@GroupChatSettingActivity,
                R.string.ready_exit_group
            )
        }
        showYesOrNoDialog(title) {
            if (it.id == R.id.tv_dialog_ok) {
                exitGroup()
            }
        }
    }

    private fun exitGroup() {
        val group = groupVo ?: return
        val session = session() ?: return
        showLoading()
        val subscriber = object : BaseSubscriber<Void>() {
            override fun onNext(t: Void?) {}

            override fun onError(t: Throwable?) {
                dismissLoading()
                t?.let {
                    showError(it)
                }
            }

            override fun onComplete() {
                super.onComplete()
                dismissLoading()
                if (session.role == SessionRole.Owner.value) {
                    showToast(
                        ContextCompat.getString(
                            this@GroupChatSettingActivity,
                            R.string.disband_group_success
                        )
                    )
                } else {
                    showToast(
                        ContextCompat.getString(
                            this@GroupChatSettingActivity,
                            R.string.exit_group_success
                        )
                    )
                }
                exitGroupSuccess(group)
            }
        }
        if (session.role == SessionRole.Owner.value) {
            val req = DisbandGroupReq(group.id)
            DataRepository.groupApi.disbandGroup(req)
                .compose(RxTransform.flowableToMain())
                .subscribe(subscriber)
            addDispose(subscriber)
        } else {
            val req = LeaveGroupReq(group.id)
            DataRepository.groupApi.leaveGroup(req)
                .compose(RxTransform.flowableToMain())
                .subscribe(subscriber)
            addDispose(subscriber)
        }
    }

    private fun exitGroupSuccess(group: GroupVo) {
        val subscriber = object : BaseSubscriber<Void>() {
            override fun onNext(t: Void?) {}

            override fun onError(t: Throwable?) {}

            override fun onComplete() {
                super.onComplete()
                finish()
            }
        }
        Flowable.just(group)
            .flatMap {
                IMCoreManager.getImDataBase().groupDao().deleteByIds(setOf(it.id))
                return@flatMap Flowable.just(it)
            }.flatMap {
                IMCoreManager.messageModule.getSession(it.sessionId).flatMap { session ->
                    IMCoreManager.messageModule.deleteSession(session, false)
                }
            }.compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
        addDispose(subscriber)
    }

    private fun onReportClick() {
        showToast("TODO")
    }

}