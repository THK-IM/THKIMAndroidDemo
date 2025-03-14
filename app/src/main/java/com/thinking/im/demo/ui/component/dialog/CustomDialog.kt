package com.thinking.im.demo.ui.component.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.thinking.im.demo.R
import com.thinking.im.demo.ui.component.OnSubviewClickListener
import com.thk.im.android.core.base.extension.setShape
import com.thk.im.android.core.base.utils.AppUtils

class CustomDialog(
    context: Context, private val title: String, private val content: String,
    private val okString: String, private val cancelString: String,
    private val listener: OnSubviewClickListener? = null,
) : Dialog(context, R.style.XDialog) {


    init {
        setContentView(R.layout.dialog_common)
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        window?.let {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(it.attributes)
            layoutParams.width = AppUtils.instance().screenWidth
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            it.setAttributes(layoutParams)
        }

    }

    override fun show() {
        super.show()
        val ly = findViewById<LinearLayout>(R.id.ly_container)
        ly.setShape(Color.WHITE, floatArrayOf(10f, 10f, 10f, 10f), false)
        val titleView = findViewById<TextView>(R.id.tv_dialog_title)
        val contentView = findViewById<TextView>(R.id.tv_dialog_content)
        val btnOk = findViewById<TextView>(R.id.tv_dialog_ok)
        val btnCancel = findViewById<TextView>(R.id.tv_dialog_cancel)

        titleView.text = title
        btnOk.text = okString
        contentView.text = content

        btnCancel.visibility = View.VISIBLE
        btnCancel.text = cancelString
        btnOk.setShape(
            ContextCompat.getColor(context, R.color.primary),
            floatArrayOf(20f, 20f, 20f, 20f),
            false
        )
        btnOk.setOnClickListener {
            dismiss()
            listener?.onClick("Ok")
        }
        btnCancel.setShape(
            Color.parseColor("#F5F5F5"),
            floatArrayOf(20f, 20f, 20f, 20f),
            false
        )
        btnCancel.setOnClickListener {
            dismiss()
            listener?.onClick("Cancel")
        }
        AppUtils.instance().vibrate(50)
    }
}