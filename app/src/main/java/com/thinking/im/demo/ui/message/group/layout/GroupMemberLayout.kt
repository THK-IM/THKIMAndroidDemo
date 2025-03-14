package com.thinking.im.demo.ui.message.group.layout

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.thinking.im.demo.R
import com.thinking.im.demo.databinding.LayoutGroupMemberBinding
import com.thinking.im.demo.repository.api.group.vo.GroupVo
import com.thk.im.android.core.IMCoreManager
import com.thk.im.android.core.SessionRole
import com.thk.im.android.core.base.BaseSubscriber
import com.thk.im.android.core.base.RxTransform
import com.thk.im.android.core.base.utils.AppUtils
import com.thk.im.android.core.db.entity.Session
import com.thk.im.android.core.db.entity.SessionMember
import com.thk.im.android.core.db.entity.User
import com.thinking.im.demo.ui.message.group.GroupMemberApplyListActivity
import com.thinking.im.demo.ui.message.group.GroupMemberListActivity
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable

class GroupMemberLayout : LinearLayout {

    private val binding: LayoutGroupMemberBinding
    private var session: Session? = null
    private var groupVo: GroupVo? = null
    private var sessionUserMembers: List<Pair<SessionMember, User>>? = null
    private val compositeDisposable = CompositeDisposable()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        val view =
            LayoutInflater.from(context).inflate(R.layout.layout_group_member, this, true)
        binding = LayoutGroupMemberBinding.bind(view)

        binding.lySessionMemberTitle.setTextBold(titleBold = false, contentBold = false)
        binding.lySessionMemberTitle.setColor(
            ContextCompat.getColor(context, R.color.primary_text_color),
            Color.TRANSPARENT,
            ContextCompat.getColor(context, R.color.primary_text_color)
        )
        binding.lySessionMemberTitle.show(true, showRequired = false)

        binding.lySessionMemberTitle.setOnClickListener {
            val group = groupVo ?: return@setOnClickListener
            val session = session ?: return@setOnClickListener
            if (myRole() > SessionRole.Member.value) {
                GroupMemberApplyListActivity.start(context, session, group)
            } else {
                GroupMemberListActivity.start(context, session, group)
            }
        }

        binding.tvSeeAll.setOnClickListener {
            val group = groupVo ?: return@setOnClickListener
            val session = session ?: return@setOnClickListener
            GroupMemberListActivity.start(context, session, group)
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
        requestUpdateMembers()
    }

    private fun updateSessionMembersView() {
        val session = session ?: return
        binding.lySessionMember.removeAllViews()
        val group = groupVo ?: return
        val sessionUserMembers = sessionUserMembers ?: return
        val maxWidth = AppUtils.instance().screenWidth - AppUtils.dp2px(28f)
        val padding = AppUtils.dp2px(14f)
        var left = padding
        var top = 0
        val rowCount = 5
        val width = (maxWidth - (rowCount + 1) * padding) / rowCount
        val height = AppUtils.dp2px(80f)
        for (member in sessionUserMembers) {
            val view = GroupMemberView(context)
            val lp = RelativeLayout.LayoutParams(width, height)
            lp.leftMargin = left
            lp.topMargin = top
            binding.lySessionMember.addView(view, lp)
            view.bind(group, member.first, member.second)
            left += width + padding
            if (left + width >= maxWidth) {
                left = padding
                top += height + padding
            }
        }

        // 补充一个空视图
        if (left + width >= maxWidth) {
            left = padding
            top += height + padding
        }
        val view = GroupMemberView(context)
        val lp = RelativeLayout.LayoutParams(width, height)
        lp.leftMargin = left
        lp.topMargin = top
        binding.lySessionMember.addView(view, lp)
        view.bindEmpty(group)

        val memberCount = session.memberCount
        val countText = String.format(
            ContextCompat.getString(context, R.string.group_member_x_x),
            group.onlineCount ?: 0, memberCount
        )
        var applyText = ""
        val applyCount = group.applyCount ?: 0
        if (applyCount > 0 && myRole() > SessionRole.Member.value) {
            applyText = String.format(
                ContextCompat.getString(context, R.string.x_group_member_apply), applyCount
            )
            binding.lySessionMemberTitle.setContentColor(Color.WHITE, Color.RED, 4)
        } else {
            binding.lySessionMemberTitle.setContentColor(
                ContextCompat.getColor(
                    context,
                    R.color.secondary_text_color
                ), Color.TRANSPARENT, 0
            )
        }
        binding.lySessionMemberTitle.setText(countText, applyText)
    }

    private fun loadMembers(group: GroupVo): Flowable<List<Pair<SessionMember, User>>> {
        return Flowable.create<List<SessionMember>>({
            val memberCount = 14
            var members = IMCoreManager.getImDataBase().sessionMemberDao()
                .findBySessionIdSortByRole(group.sessionId, 0, memberCount).toMutableList()
            if (members.isNotEmpty()) {
                var meMember: SessionMember? = null
                for (m in members) {
                    if (m.userId == IMCoreManager.uId) {
                        meMember = m
                        break
                    }
                }
                if (meMember == null) {
                    meMember = IMCoreManager.getImDataBase().sessionMemberDao()
                        .findSessionMember(group.sessionId, IMCoreManager.uId)
                    if (meMember != null) {
                        members.add(meMember)
                    }
                }

                members.sortWith(kotlin.Comparator { m1, m2 ->
                    // ex: 返回-1 排在前面 返回1 排在后面
                    if (m1.userId == IMCoreManager.uId && m2.role != SessionRole.Admin.value) {
                        return@Comparator -1
                    }
                    if (m2.userId == IMCoreManager.uId && m1.role != SessionRole.Admin.value) {
                        return@Comparator 1
                    }
                    // 角色小的排在后面
                    if (m1.role < m2.role) {
                        return@Comparator 1
                    }
                    if (m1.cTime > m2.cTime) {
                        return@Comparator 1
                    }
                    return@Comparator -1
                })

                if (members.size > memberCount) {
                    members = members.subList(0, memberCount)
                }
            }
            it.onNext(members)
            it.onComplete()
        }, BackpressureStrategy.LATEST)
            .flatMap { members ->
                val ids = mutableSetOf<Long>()
                for (m in members) {
                    ids.add(m.userId)
                }
                IMCoreManager.userModule.queryUsers(ids)
                    .flatMap { userMap ->
                        val sessionUserMembers = mutableListOf<Pair<SessionMember, User>>()
                        for (m in members) {
                            val user = userMap[m.userId] ?: continue
                            sessionUserMembers.add(Pair(m, user))
                        }
                        Flowable.just(sessionUserMembers)
                    }
            }
    }

    private fun requestUpdateMembers() {
        val groupVo = groupVo ?: return
        val subscriber = object : BaseSubscriber<List<Pair<SessionMember, User>>>() {
            override fun onNext(t: List<Pair<SessionMember, User>>) {
                sessionUserMembers = t
                updateSessionMembersView()
            }
        }
        loadMembers(groupVo).compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
        compositeDisposable.add(subscriber)
    }

}