package com.thinking.im.demo.ui.message.group.layout

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.thinking.im.demo.R
import com.thinking.im.demo.databinding.LayoutSessionSettingBinding
import com.thinking.im.demo.repository.ChatFunctionMemberPrivacy
import com.thinking.im.demo.repository.DataRepository
import com.thinking.im.demo.repository.api.group.vo.GroupVo
import com.thinking.im.demo.repository.api.group.vo.UpdateGroupReq
import com.thk.im.android.core.IMCoreManager
import com.thk.im.android.core.IMEvent
import com.thk.im.android.core.SessionMuted
import com.thk.im.android.core.SessionRole
import com.thk.im.android.core.SessionStatus
import com.thk.im.android.core.base.BaseSubscriber
import com.thk.im.android.core.base.RxTransform
import com.thk.im.android.core.db.entity.Session
import com.thk.im.android.core.event.XEventBus
import com.thinking.im.demo.ui.base.BaseActivity
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable

class SessionSettingLayout : LinearLayout {


    private val binding: LayoutSessionSettingBinding
    private var session: Session? = null
    private var groupVo: GroupVo? = null
    private val compositeDisposable = CompositeDisposable()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        val view =
            LayoutInflater.from(context).inflate(R.layout.layout_session_setting, this, true)
        binding = LayoutSessionSettingBinding.bind(view)

        binding.lyTop.setTextSize(14f)
        binding.lyTop.setColor(ContextCompat.getColor(context, R.color.primary_text_color))
        binding.lyTop.setTextBold(false)

        binding.lySilence.setTextSize(14f)
        binding.lySilence.setColor(ContextCompat.getColor(context, R.color.primary_text_color))
        binding.lySilence.setTextBold(false)

        binding.lyMemberMute.setTextSize(14f)
        binding.lyMemberMute.setColor(ContextCompat.getColor(context, R.color.primary_text_color))
        binding.lyMemberMute.setTextBold(false)

        binding.lyMemberPrivacy.setTextSize(14f)
        binding.lyMemberPrivacy.setColor(
            ContextCompat.getColor(
                context,
                R.color.primary_text_color
            )
        )
        binding.lyMemberPrivacy.setTextBold(false)

        binding.lyTop.setItemClickListener {
            onTopLayoutClick()
        }
        binding.lySilence.setItemClickListener {
            onSilenceLayoutClick()
        }
        binding.lyMemberMute.setItemClickListener {
            onMuteAllLayoutClick()
        }
        binding.lyMemberPrivacy.setItemClickListener {
            onMemberPrivacyLayoutClick()
        }

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        compositeDisposable.clear()
    }

    private fun myRole(): Int {
        val session = session ?: return SessionRole.Member.value
        return session.role
    }

    fun bind(groupVo: GroupVo, session: Session) {
        this.groupVo = groupVo
        this.session = session
        render()
    }

    private fun render() {
        val groupVo = groupVo ?: return
        val session = session ?: return

        Log.v("SessionSettingLayout", "render ${session.mute} ${session.functionFlag}")

        binding.lyTop.setContent(
            ContextCompat.getString(context, R.string.session_set_top),
            session.topTimestamp > 0
        )
        binding.lySilence.setContent(
            ContextCompat.getString(context, R.string.session_silence_set),
            session.status.and(SessionStatus.Silence.value) > 0,
        )
        binding.lyMemberMute.setContent(
            ContextCompat.getString(context, R.string.mute_all_member),
            session.mute.and(SessionMuted.All.value) > 0,
            ContextCompat.getString(context, R.string.mute_all_member_desc),
        )
        binding.lyMemberPrivacy.setContent(
            ContextCompat.getString(context, R.string.member_privacy_protect),
            (session.functionFlag).and(ChatFunctionMemberPrivacy) > 0,
            ContextCompat.getString(context, R.string.member_privacy_protect_desc),
        )

        if (myRole() > SessionRole.Member.value) {
            binding.lyMemberMute.visibility = View.VISIBLE
            binding.lyMemberPrivacy.visibility = View.VISIBLE
        } else {
            binding.lyMemberMute.visibility = View.GONE
            binding.lyMemberPrivacy.visibility = View.GONE
        }
    }

    private fun onTopLayoutClick() {
        val session = session ?: return
        if (binding.lyTop.isSwitchOn()) {
            session.topTimestamp = 0
        } else {
            session.topTimestamp = IMCoreManager.severTime
        }
        updateSession(session)
    }

    private fun onSilenceLayoutClick() {
        val session = session ?: return
        session.status = session.status.xor(SessionStatus.Silence.value)
        updateSession(session)
    }

    private fun onMuteAllLayoutClick() {
        val session = session ?: return
        val mute = if (session.mute.and(SessionMuted.All.value) > 0) 0 else 1
        muteAllMembers(mute, session)
    }

    private fun onMemberPrivacyLayoutClick() {
        val session = session ?: return
        var privacyFlag = session.functionFlag
        if (!binding.lyMemberPrivacy.isSwitchOn()) {
            privacyFlag = privacyFlag.or(ChatFunctionMemberPrivacy)
        } else {
            privacyFlag = privacyFlag.xor(ChatFunctionMemberPrivacy)
        }
        updateGroupPrivacyFlag(privacyFlag, session)
    }

    private fun updateSession(session: Session) {
        (context as? BaseActivity)?.showLoading()
        val subscriber = object : BaseSubscriber<Void>() {
            override fun onNext(t: Void?) {}

            override fun onError(t: Throwable?) {
                (context as? BaseActivity)?.dismissLoading()
                t?.let {
                    (context as? BaseActivity)?.showError(it)
                }
            }

            override fun onComplete() {
                super.onComplete()
                (context as? BaseActivity)?.dismissLoading()
            }
        }
        IMCoreManager.messageModule.updateSession(session, true)
            .compose(RxTransform.flowableToMain()).subscribe(subscriber)
        compositeDisposable.addAll(subscriber)
    }

    private fun muteAllMembers(mute: Int, session: Session) {
        val groupVo = groupVo ?: return
        val subscriber = object : BaseSubscriber<Void>() {
            override fun onNext(t: Void?) {}

            override fun onError(t: Throwable?) {
                (context as? BaseActivity)?.dismissLoading()
                t?.let {
                    (context as? BaseActivity)?.showError(it)
                }
            }

            override fun onComplete() {
                super.onComplete()
                (context as? BaseActivity)?.dismissLoading()
                XEventBus.post(IMEvent.SessionUpdate.value, session)
            }
        }
        val req = UpdateGroupReq(groupVo.id, null, null, null, null, mute, null)
        DataRepository.groupApi.updateGroup(groupVo.id, req)
            .concatWith(Flowable.create({
                if (mute > 0) {
                    session.mute = session.mute.or(SessionMuted.All.value)
                } else {
                    session.mute = session.mute.xor(SessionMuted.All.value)
                }
                IMCoreManager.getImDataBase().sessionDao().insertOrReplace(listOf(session))
                it.onComplete()
            }, BackpressureStrategy.LATEST))
            .compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
        compositeDisposable.add(subscriber)
    }

    private fun updateGroupPrivacyFlag(flag: Long, session: Session) {
        val groupVo = groupVo ?: return
        val subscriber = object : BaseSubscriber<Void>() {
            override fun onNext(t: Void?) {}

            override fun onError(t: Throwable?) {
                (context as? BaseActivity)?.dismissLoading()
                t?.let {
                    (context as? BaseActivity)?.showError(it)
                }
            }

            override fun onComplete() {
                super.onComplete()
                (context as? BaseActivity)?.dismissLoading()
                XEventBus.post(IMEvent.SessionUpdate.value, session)
            }
        }
        val req = UpdateGroupReq(groupVo.id, null, null, null, flag, null, null)
        DataRepository.groupApi.updateGroup(groupVo.id, req)
            .concatWith(Flowable.create({
                session.functionFlag = flag
                IMCoreManager.getImDataBase().sessionDao().insertOrReplace(listOf(session))
                it.onComplete()
            }, BackpressureStrategy.LATEST))
            .compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
        compositeDisposable.add(subscriber)
    }
}