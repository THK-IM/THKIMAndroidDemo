package com.thinking.im.demo.ui.message.adapter

import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.thinking.im.demo.R
import com.thinking.im.demo.databinding.ItemviewGroupMemberBinding
import com.thinking.im.demo.repository.api.user.vo.BasisUserVo
import com.thinking.im.demo.ui.component.OnSubviewTagClickListener
import com.thk.im.android.core.IMCoreManager
import com.thk.im.android.core.SessionRole
import com.thk.im.android.core.db.entity.SessionMember
import com.thk.im.android.core.db.entity.User

class GroupMemberVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var listener: OnSubviewTagClickListener? = null
    private val binding = ItemviewGroupMemberBinding.bind(itemView)
    private var vo: Pair<SessionMember, User>? = null

    init {
//        binding.tvRemove.setShape(
//            Color.parseColor("#ECECEC"),
//            floatArrayOf(4f, 4f, 4f, 4f),
//            false
//        )

        binding.lyUser.setOnClickListener {
            val vo = this.vo ?: return@setOnClickListener
            listener?.onClick(vo, "user", binding.lyUser)
        }

        binding.ivMore.setOnClickListener {
            val vo = this.vo ?: return@setOnClickListener
            listener?.onClick(vo, "more", binding.lyUser)
        }
    }

    fun bind(vo: Pair<SessionMember, User>, myRole: Int) {
        this.vo = vo
        binding.lyUser.setUserInfo(BasisUserVo.fromUser(vo.second))
        if (!TextUtils.isEmpty(vo.first.noteName)) {
            binding.lyUser.updateNickName(vo.first.noteName)
        }
        if (myRole > vo.first.role) {
            binding.ivMore.visibility = View.VISIBLE
        } else {
            binding.ivMore.visibility = View.GONE
        }
        when (vo.first.role) {
            SessionRole.Owner.value -> {
                binding.lyUser.setUserLabel(
                    ContextCompat.getString(
                        itemView.context,
                        R.string.group_owner
                    )
                )
            }

            SessionRole.SuperAdmin.value,
            SessionRole.Admin.value,
                -> {
                binding.lyUser.setUserLabel(
                    ContextCompat.getString(
                        itemView.context,
                        R.string.group_admin
                    )
                )
            }

            else -> {
                binding.lyUser.setUserLabel(null)
            }
        }

        if (vo.second.id == IMCoreManager.uId) {
            binding.lyUser.setUserLabel(ContextCompat.getString(itemView.context, R.string.is_me))
        }
    }
}