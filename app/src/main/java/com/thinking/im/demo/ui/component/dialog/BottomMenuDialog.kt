package com.thinking.im.demo.ui.component.dialog

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.thinking.im.demo.R
import com.thinking.im.demo.databinding.DialogBottomMenuBinding
import com.thk.im.android.core.base.extension.setShape
import com.thinking.im.demo.ui.component.OnSubviewClickListener
import com.thinking.im.demo.ui.component.dialog.adapter.BottomMenuAdapter


class BottomMenuDialog(
    ctx: Context,
    private val menus: List<String>,
    private val listener: (String) -> Unit
) :
    BottomSheetDialog(ctx, R.style.BottomSheetDialogStyle) {

    companion object {
        fun show(ctx: Context, menus: List<String>, listener: (String) -> Unit) {
            BottomMenuDialog(ctx, menus, listener).show()
        }
    }

    private val binding: DialogBottomMenuBinding

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_bottom_menu, null)
        binding = DialogBottomMenuBinding.bind(view)
        setContentView(view)
        initView()
    }

    private fun initView() {
        binding.lyContainer.setShape(
            Color.WHITE,
            floatArrayOf(12f, 12f, 0f, 0f),
            false
        )

        val adapter = BottomMenuAdapter(context)
        adapter.onSubviewClickListener = object : OnSubviewClickListener {
            override fun onClick(tag: String) {
                listener.invoke(tag)
                dismiss()
            }
        }
        binding.rcvMenus.layoutManager = LinearLayoutManager(context)
        binding.rcvMenus.adapter = adapter
        adapter.setMenus(menus)
        resetBehaviorHeight()
    }

    private fun resetBehaviorHeight() {
        binding.lyContainer.measure(0, 0)
        val height = binding.lyContainer.measuredHeight
        behavior.peekHeight = height
    }

}