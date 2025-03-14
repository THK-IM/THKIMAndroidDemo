package com.thinking.im.demo.ui.component.widget

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.google.android.material.shape.ShapeAppearanceModel
import com.thinking.im.demo.R
import com.thinking.im.demo.databinding.LayoutBasisUserBinding
import com.thinking.im.demo.repository.api.user.vo.BasisUserVo
import com.thk.im.android.core.base.IMImageLoader
import com.thk.im.android.core.base.extension.setShape

class BasisUserLayout : RelativeLayout {

    private val binding: LayoutBasisUserBinding

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_basis_user, this, true)
        binding = LayoutBasisUserBinding.bind(view)
        binding.tvLabel.setShape(
            ContextCompat.getColor(context, R.color.primary),
            floatArrayOf(3f, 3f, 3f, 3f),
            false,
        )
    }

    fun setUserInfo(basisUserVo: BasisUserVo) {
        if (basisUserVo.avatar != null) {
            IMImageLoader.displayImageUrl(binding.ivAvatar, basisUserVo.avatar!!)
        }
        val size = (binding.root as ViewGroup).layoutParams.height
        binding.ivAvatar.layoutParams = LayoutParams(size, size)
        val shapeMode = ShapeAppearanceModel().withCornerSize((size / 2).toFloat())
        binding.ivAvatar.shapeAppearanceModel = shapeMode
        binding.tvNickname.text = basisUserVo.nickname
    }

    fun setUserLabel(text: String?) {
        if (TextUtils.isEmpty(text)) {
            binding.tvLabel.visibility = View.GONE
        } else {
            binding.tvLabel.visibility = View.VISIBLE
        }
        binding.tvLabel.text = text
    }

    fun updateNickName(text: String?) {
        binding.tvNickname.text = text
    }
}