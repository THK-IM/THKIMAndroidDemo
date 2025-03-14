package com.thinking.im.demo.ui.message.adapter

import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.thinking.im.demo.R
import com.thinking.im.demo.databinding.ItemviewGroupMemberApplyBinding
import com.thinking.im.demo.repository.MemberApplyStatus
import com.thinking.im.demo.repository.api.group.vo.GroupMemberApplyVo
import com.thk.im.android.core.base.extension.setShape
import com.thinking.im.demo.ui.component.OnSubviewTagClickListener

class GroupMemberApplyVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var listener: OnSubviewTagClickListener? = null
    private val binding = ItemviewGroupMemberApplyBinding.bind(itemView)
    private var vo: GroupMemberApplyVo? = null

    init {
        binding.tvAccept.setShape(
            ContextCompat.getColor(itemView.context, R.color.primary),
            floatArrayOf(4f, 4f, 4f, 4f),
            false
        )

        binding.tvReject.setShape(
            Color.parseColor("#ECECEC"),
            floatArrayOf(4f, 4f, 4f, 4f),
            false
        )

        binding.tvAccept.setOnClickListener {
            val vo = this.vo ?: return@setOnClickListener
            listener?.onClick(vo, "accept", binding.tvAccept)
        }

        binding.tvReject.setOnClickListener {
            val vo = this.vo ?: return@setOnClickListener
            listener?.onClick(vo, "reject", binding.tvReject)
        }

        binding.lyUser.setOnClickListener {
            val vo = this.vo ?: return@setOnClickListener
            listener?.onClick(vo, "user", binding.lyUser)
        }


        binding.lyUser.hideId(false)
        binding.lyUser.hideLevel(true)
    }

    fun bind(vo: GroupMemberApplyVo) {
        this.vo = vo
        binding.lyUser.setUserInfo(vo.applyUser)
        if (vo.reviewStatus == MemberApplyStatus.Init.value) {
            binding.tvAccept.visibility = View.VISIBLE
            binding.tvReject.visibility = View.VISIBLE
            binding.tvStatus.visibility = View.GONE
        } else {
            binding.tvAccept.visibility = View.GONE
            binding.tvReject.visibility = View.GONE
            binding.tvStatus.visibility = View.VISIBLE
            if (vo.reviewStatus == MemberApplyStatus.Accepted.value) {
                binding.tvStatus.text =
                    ContextCompat.getString(itemView.context, R.string.had_accept_join)
            } else {
                binding.tvStatus.text =
                    ContextCompat.getString(itemView.context, R.string.had_reject_join)
            }
        }
    }
}