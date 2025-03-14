package com.thinking.im.demo.ui.message.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.shape.ShapeAppearanceModel
import com.thinking.im.demo.databinding.ItemviewGroupBinding
import com.thinking.im.demo.repository.api.group.vo.GroupVo
import com.thk.im.android.core.base.IMImageLoader
import com.thk.im.android.core.base.utils.AppUtils


class GroupVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding = ItemviewGroupBinding.bind(itemView)

    init {
        val shapeModel = ShapeAppearanceModel.builder()
            .setAllCornerSizes(AppUtils.dp2px(23f).toFloat())
            .build()
        binding.ivAvatar.shapeAppearanceModel = shapeModel
        binding.tvRemark.visibility = View.GONE
    }

    fun bind(group: GroupVo) {
        IMImageLoader.displayImageUrl(binding.ivAvatar, group.avatar)
        binding.tvNickname.text = group.name
    }
}