package com.thinking.im.demo.ui.component.widget

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.thinking.im.demo.R
import com.thinking.im.demo.databinding.LayoutSettingSwitchBinding

class SettingSwitchLayout : RelativeLayout {


    private val binding: LayoutSettingSwitchBinding

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        val view =
            LayoutInflater.from(context).inflate(R.layout.layout_setting_switch, this, true)
        binding = LayoutSettingSwitchBinding.bind(view)
    }

    fun setContent(title: String?, selected: Boolean, desc: String? = null) {
        title?.let {
            binding.tvTitle.text = it
        }
        desc?.let {
            binding.tvTitleDesc.text = it
        }
        binding.ivSwitch.isSelected = selected
    }

    fun setTextSize(titleSize: Float?) {
        titleSize?.let {
            binding.tvTitle.textSize = it
        }
    }

    fun setColor(titleColor: Int?) {
        titleColor?.let {
            binding.tvTitle.setTextColor(it)
        }
    }

    fun setTextBold(titleBold: Boolean) {
        if (titleBold) {
            binding.tvTitle.setTypeface(Typeface.DEFAULT_BOLD, Typeface.DEFAULT.style)
        } else {
            binding.tvTitle.setTypeface(Typeface.DEFAULT, Typeface.DEFAULT.style)
        }
    }

    fun isSwitchOn(): Boolean {
        return binding.ivSwitch.isSelected
    }

    fun setItemClickListener(listener: OnClickListener) {
        binding.ivSwitch.setOnClickListener(listener)
    }
}