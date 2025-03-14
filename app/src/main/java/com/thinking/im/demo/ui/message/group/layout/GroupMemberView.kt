package com.thinking.im.demo.ui.message.group.layout

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.google.android.material.shape.ShapeAppearanceModel
import com.thinking.im.demo.R
import com.thinking.im.demo.databinding.ViewGroupMemberBinding
import com.thinking.im.demo.repository.api.group.vo.GroupVo
import com.thinking.im.demo.ui.component.OnSubviewTagClickListener
import com.thk.im.android.core.IMCoreManager
import com.thk.im.android.core.SessionRole
import com.thk.im.android.core.base.BaseSubscriber
import com.thk.im.android.core.base.IMImageLoader
import com.thk.im.android.core.base.RxTransform
import com.thk.im.android.core.base.extension.setShape
import com.thk.im.android.core.base.utils.AppUtils
import com.thk.im.android.core.db.entity.Session
import com.thk.im.android.core.db.entity.SessionMember
import com.thk.im.android.core.db.entity.User
import com.thk.im.android.ui.manager.IMUIManager
import io.reactivex.disposables.CompositeDisposable

class GroupMemberView : RelativeLayout {

    private val binding: ViewGroupMemberBinding
    var listener: OnSubviewTagClickListener? = null
    private var user: User? = null
    private var sessionMember: SessionMember? = null
    private var groupVo: GroupVo? = null
    private val compositeDisposable = CompositeDisposable()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        val view =
            LayoutInflater.from(context).inflate(R.layout.view_group_member, this, true)
        binding = ViewGroupMemberBinding.bind(view)
        binding.tvLabel.setShape(
            ContextCompat.getColor(context, R.color.primary),
            floatArrayOf(3f, 3f, 3f, 3f),
            false
        )

        val shapeModel = ShapeAppearanceModel().withCornerSize(AppUtils.dp2px(26f).toFloat())
        binding.ivAvatar.shapeAppearanceModel = shapeModel

        binding.lyContainer.setOnClickListener {
            onClick()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        compositeDisposable.clear()
    }

    fun bindEmpty(group: GroupVo?) {
        this.groupVo = group
        binding.ivAvatar.setImageResource(R.drawable.ic_member_add)
        binding.tvNickname.text = ContextCompat.getString(context, R.string.invite)
        binding.tvLabel.visibility = View.GONE
    }

    fun bind(group: GroupVo?, sessionMember: SessionMember?, user: User?) {
        this.groupVo = group
        this.sessionMember = sessionMember
        this.user = user
        render()
    }

    private fun render() {
        val sessionMember = sessionMember ?: return
        val user = user ?: return
        val group = groupVo ?: return
        if (!TextUtils.isEmpty(sessionMember.noteName)) {
            binding.tvNickname.text = sessionMember.noteName
        } else {
            binding.tvNickname.text = user.nickname
        }

        if (!TextUtils.isEmpty(sessionMember.noteAvatar)) {
            IMImageLoader.displayImageUrl(binding.ivAvatar, sessionMember.noteAvatar!!)
        } else if (!TextUtils.isEmpty(user.avatar)) {
            IMImageLoader.displayImageUrl(binding.ivAvatar, user.avatar!!)
        } else {
            IMUIManager.uiResourceProvider?.avatar(user)?.let {
                binding.ivAvatar.setImageResource(it)
            }
        }

        when (sessionMember.role) {
            SessionRole.Owner.value -> {

                binding.tvLabel.visibility = View.VISIBLE
                binding.tvLabel.text = ContextCompat.getString(context, R.string.group_owner)
            }

            SessionRole.SuperAdmin.value,
            SessionRole.Admin.value,
                -> {
                binding.tvLabel.visibility = View.VISIBLE
                binding.tvLabel.text = ContextCompat.getString(context, R.string.group_admin)
            }

            else -> {
                binding.tvLabel.visibility = View.GONE
            }
        }

        if (sessionMember.userId == IMCoreManager.uId) {
            binding.tvLabel.visibility = View.VISIBLE
            binding.tvLabel.text = ContextCompat.getString(context, R.string.is_me)
        }

    }

    private fun onClick() {
        user?.let { u ->
            groupVo?.let { g ->
                openUserPage(g, u)
            }
            return
        }
    }

    private fun openUserPage(group: GroupVo, user: User) {
        val subscriber = object : BaseSubscriber<Session>() {
            override fun onNext(t: Session?) {
                t?.let {
                    IMUIManager.pageRouter?.openUserPage(context, user, it)
                }
            }
        }
        IMCoreManager.messageModule.getSession(group.sessionId)
            .compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
        compositeDisposable.add(subscriber)
    }
}