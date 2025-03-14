package com.thinking.im.demo.ui.text

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.thinking.im.demo.R
import com.thinking.im.demo.databinding.ActivityTextEditBinding
import com.thinking.im.demo.ui.base.BaseActivity
import com.thinking.im.demo.ui.component.OnTextChangeListener
import com.thk.im.android.core.base.extension.setShape

class TextEditActivity : BaseActivity() {

    companion object {
        fun start(
            ctx: Context, title: String, placeholder: String, maxCount: Int,
            launcher: ActivityResultLauncher<Intent>,
        ) {
            val intent = Intent(ctx, TextEditActivity::class.java)
            intent.putExtra("title", title)
            intent.putExtra("placeholder", placeholder)
            intent.putExtra("maxCount", maxCount)
            launcher.launch(intent)
        }
    }

    private lateinit var binding: ActivityTextEditBinding

    override fun toolbarTitle(): String? {
        return intent.getStringExtra("title")
    }

    override fun getToolbar(): Toolbar {
        return binding.tbTop.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityTextEditBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        this.initView()
    }

    private fun initView() {
        updateCountView()
        binding.metInput.setShape(
            Color.WHITE,
            floatArrayOf(12f, 12f, 12f, 12f),
            false
        )
        binding.metInput.setUI(
            hintText = hintText(),
            hintTextColor = Color.parseColor("#BBBBBB"),
            textColor = Color.parseColor("#222222"),
            textStyle = Typeface.NORMAL,
            maxCount = maxTextCount(),
            textSize = 14,
        )
        binding.metInput.onTextChangeListener = object : OnTextChangeListener {
            override fun onTextChange(text: String) {
                updateCountView()
            }
        }

        binding.tvSubmit.setShape(
            ContextCompat.getColor(this, R.color.primary),
            floatArrayOf(24f, 24f, 24f, 24f),
            false
        )
        binding.tvSubmit.setOnClickListener {
            submit()
        }
    }

    private fun updateCountView() {
        val count = binding.metInput.getContent().length
        if (count > 0) {
            binding.tvSubmit.alpha = 1f
        } else {
            binding.tvSubmit.alpha = 0.5f
        }
    }

    private fun hintText(): String {
        return intent.getStringExtra("placeholder") ?: ""
    }

    private fun maxTextCount(): Int {
        return intent.getIntExtra("maxCount", 12)
    }

    private fun submit() {
        if (binding.tvSubmit.alpha < 1f) {
            return
        }
        val text = binding.metInput.getContent()
        val intent = Intent()
        intent.putExtra("text", text)
        setResult(RESULT_OK, intent)
        finish()
    }
}