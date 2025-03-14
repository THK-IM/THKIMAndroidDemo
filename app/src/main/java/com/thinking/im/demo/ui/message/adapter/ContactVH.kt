package com.thinking.im.demo.ui.message.adapter

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.shape.ShapeAppearanceModel
import com.thinking.im.demo.R
import com.thinking.im.demo.databinding.ItemviewContactBinding
import com.thinking.im.demo.repository.api.contact.vo.ContactVo
import com.thk.im.android.core.base.IMImageLoader
import com.thk.im.android.core.base.utils.AppUtils

class ContactVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding = ItemviewContactBinding.bind(itemView)

    init {
        val shapeModel = ShapeAppearanceModel.builder()
            .setAllCornerSizes(AppUtils.dp2px(23f).toFloat())
            .build()
        binding.ivAvatar.shapeAppearanceModel = shapeModel
    }

    fun bind(contact: ContactVo) {
        IMImageLoader.displayImageUrl(binding.ivAvatar, contact.avatar)
        binding.tvNickname.text = contact.nickname
        if (contact.noteName != null) {
            val text = ContextCompat.getString(
                itemView.context,
                R.string.remark_of_him
            ) + ": " + contact.noteName
            binding.tvRemark.text = text
        } else {
            binding.tvRemark.text = ContextCompat.getString(itemView.context, R.string.no_remark)
        }

    }
}