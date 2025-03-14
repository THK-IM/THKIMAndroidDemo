package com.thinking.im.demo.ui.component.widget

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.lxj.xpopup.util.KeyboardUtils
import com.thinking.im.demo.R
import com.thinking.im.demo.databinding.WidgetMaxCountEditTextBinding
import com.thinking.im.demo.ui.component.OnTextChangeListener

class MaxCountEditText : ConstraintLayout {

    private var maxCount = 0

    private val binding: WidgetMaxCountEditTextBinding

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    var onTextChangeListener: OnTextChangeListener? = null

    init {
        val view =
            LayoutInflater.from(context).inflate(R.layout.widget_max_count_edit_text, this, true)
        binding = WidgetMaxCountEditTextBinding.bind(view)
    }

    fun setUI(
        hintText: String, hintTextColor: Int = Color.parseColor("#BDBDBD"),
        text: String? = null, textColor: Int = Color.parseColor("#333333"),
        textStyle: Int, textSize: Int = 16, textGravity: Int = Gravity.TOP.or(Gravity.START),
        maxCount: Int, countTipsTextColor: Int = Color.parseColor("#BDBDBD")
    ) {
        this.maxCount = maxCount
        binding.etContent.gravity = textGravity
        binding.etContent.hint = hintText
        binding.etContent.setHintTextColor(hintTextColor)
        binding.etContent.setText(text)
        binding.etContent.setTextColor(textColor)
        binding.etContent.setTypeface(null, textStyle)
        binding.etContent.textSize = textSize.toFloat()
        binding.tvCountTips.setTextColor(countTipsTextColor)
        val filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxCount))
        binding.etContent.filters = filters
        binding.etContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                updateTextCount()
            }
        })

        updateTextCount()
    }

    private fun updateTextCount() {
        val currentCount = binding.etContent.text.count()
        binding.tvCountTips.text = "$currentCount/${this.maxCount}"
        onTextChangeListener?.onTextChange(binding.etContent.text.toString())
    }

    fun requestInput() {
        KeyboardUtils.showSoftInput(binding.etContent)
    }

    fun getContent(): String {
        return binding.etContent.text.toString()
    }

    fun clearContent() {
        binding.etContent.setText("")
    }

    fun setContent(text: String) {
        binding.etContent.setText(text)
    }
}