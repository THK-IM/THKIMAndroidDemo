package com.thinking.im.demo.ui.fragment.adapter

import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.thinking.im.demo.R
import com.thinking.im.demo.databinding.ItemviewRelationBinding
import com.thinking.im.demo.repository.ContactRelation
import com.thinking.im.demo.repository.api.user.vo.BasisUserVo
import com.thk.im.android.core.base.extension.setShape
import com.thk.im.android.core.base.extension.setShapeWithStroke
import com.thk.im.android.core.db.entity.Contact

class RelationVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var relationListener: RelationListener? = null
    private val binding = ItemviewRelationBinding.bind(itemView)
    private lateinit var pair: Pair<Contact, BasisUserVo>

    fun bind(pair: Pair<Contact, BasisUserVo>) {
        this.pair = pair
        binding.lyUser.setUserInfo(pair.second)
        binding.lyUser.hideId(false)
        binding.lyUser.hideLevel(true)
        if (pair.first.relation.and(ContactRelation.Black.value) != 0) {
            binding.tvRelationOpr.setShapeWithStroke(
                Color.TRANSPARENT,
                Color.parseColor("#ECECEC"),
                1,
                floatArrayOf(14f, 14f, 14f, 14f)
            )
            binding.tvRelationOpr.setTextColor(Color.parseColor("#555555"))
            binding.tvRelationOpr.text =
                ContextCompat.getString(itemView.context, R.string.remove_out_blacklist)
        } else if (pair.first.relation.and(ContactRelation.Fellow.value) != 0) {
            binding.tvRelationOpr.setShapeWithStroke(
                Color.TRANSPARENT,
                Color.parseColor("#ECECEC"),
                1,
                floatArrayOf(14f, 14f, 14f, 14f)
            )
            if (pair.first.relation.and(ContactRelation.BeFellow.value) != 0) {
                binding.tvRelationOpr.setTextColor(Color.parseColor("#B4B4B4"))
                binding.tvRelationOpr.text =
                    ContextCompat.getString(itemView.context, R.string.follow_each_other)
            } else {
                binding.tvRelationOpr.setShapeWithStroke(
                    Color.TRANSPARENT,
                    Color.parseColor("#ECECEC"),
                    1,
                    floatArrayOf(14f, 14f, 14f, 14f)
                )
                binding.tvRelationOpr.setTextColor(Color.parseColor("#B4B4B4"))
                binding.tvRelationOpr.text =
                    ContextCompat.getString(itemView.context, R.string.had_followed)
            }
        } else {
            binding.tvRelationOpr.setShape(
                ContextCompat.getColor(itemView.context, R.color.primary),
                floatArrayOf(14f, 14f, 14f, 14f),
                false
            )
            binding.tvRelationOpr.setTextColor(Color.parseColor("#222222"))
            binding.tvRelationOpr.text =
                ContextCompat.getString(itemView.context, R.string.to_follow)
        }

        binding.tvRelationOpr.setOnClickListener {
            oprClick()
        }
    }

    private fun oprClick() {
        val contact = this.pair.first
        if (contact.relation.and(ContactRelation.Black.value) != 0) {
            relationListener?.onContactClick(contact, "removeBlack")
        } else {
            if (contact.relation.and(ContactRelation.Fellow.value) != 0) {
                relationListener?.onContactClick(contact, "unFollow")
            } else {
                relationListener?.onContactClick(contact, "follow")
            }
        }
    }
}