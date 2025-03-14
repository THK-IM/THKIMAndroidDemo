package com.thinking.im.demo.ui.component.widget

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.thinking.im.demo.R
import com.thinking.im.demo.databinding.LayoutSettingItemBinding
import com.thk.im.android.core.base.extension.setShape
import com.thk.im.android.core.base.utils.AppUtils

class SettingItemLayout : RelativeLayout {

    private val binding: LayoutSettingItemBinding

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        val view =
            LayoutInflater.from(context).inflate(R.layout.layout_setting_item, this, true)
        binding = LayoutSettingItemBinding.bind(view)
    }

    fun setText(title: String?, content: String?) {
        title?.let {
            binding.tvTitle.text = it
        }
        content?.let {
            binding.tvContent.text = it
        }
    }

    fun setTextSize(titleSize: Float?, contentSize: Float?, requiredSize: Float?) {
        titleSize?.let {
            binding.tvTitle.textSize = it
        }
        contentSize?.let {
            binding.tvContent.textSize = it
        }
        requiredSize?.let {
            binding.tvRequired.textSize = it
        }
    }

    fun setColor(titleColor: Int?, contentColor: Int?, requiredColor: Int?) {
        titleColor?.let {
            binding.tvTitle.setTextColor(it)
        }
        contentColor?.let {
            binding.tvContent.setTextColor(it)
        }
        requiredColor?.let {
            binding.tvRequired.setTextColor(it)
        }
    }

    fun show(showArrow: Boolean, showRequired: Boolean) {
        binding.ivArrow.visibility = if (showArrow) VISIBLE else GONE
        binding.tvRequired.visibility = if (showRequired) VISIBLE else GONE
    }

    fun setTextBold(titleBold: Boolean, contentBold: Boolean) {
        if (titleBold) {
            binding.tvTitle.setTypeface(Typeface.DEFAULT_BOLD, Typeface.DEFAULT.style)
        } else {
            binding.tvTitle.setTypeface(Typeface.DEFAULT, Typeface.DEFAULT.style)
        }
        if (contentBold) {
            binding.tvContent.setTypeface(Typeface.DEFAULT_BOLD, Typeface.DEFAULT.style)
        } else {
            binding.tvContent.setTypeface(Typeface.DEFAULT, Typeface.DEFAULT.style)
        }
    }

    fun setContentColor(textColor: Int, bgColor: Int, padding: Int) {
        binding.tvContent.setShape(bgColor, floatArrayOf(15f, 15f, 15f, 15f), false)
        binding.tvContent.setTextColor(textColor)
        binding.tvContent.setPadding(
            AppUtils.dp2px(padding.toFloat()),
            0,
            AppUtils.dp2px(padding.toFloat()),
            0
        )
    }

    fun setArrowResId(resId: Int) {
        binding.ivArrow.setImageResource(resId)
    }

    fun setContentGravity(gravity: Int) {
        binding.tvContent.gravity = gravity
    }

    fun setContentImage(resId: Int) {
        binding.ivContent.setImageResource(resId)
        binding.ivContent.visibility = View.VISIBLE
    }
}