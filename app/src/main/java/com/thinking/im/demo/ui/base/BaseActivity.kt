package com.thinking.im.demo.ui.base

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.gyf.immersionbar.ImmersionBar
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.util.KeyboardUtils
import com.thinking.im.demo.R
import com.thinking.im.demo.ui.base.loading.PopupLoading
import com.thk.im.android.core.IMEvent
import com.thk.im.android.core.base.LLog
import com.thk.im.android.core.base.LanguageUtils
import com.thk.im.android.core.base.extension.setShape
import com.thk.im.android.core.base.utils.IMKeyboardUtils
import com.thk.im.android.core.base.utils.ToastUtils
import com.thk.im.android.core.event.XEventBus
import com.thk.im.android.core.exception.CodeMsgException
import com.thinking.im.demo.ui.component.OnSubviewClickListener
import com.thinking.im.demo.ui.component.dialog.CustomDialog
import com.thinking.im.demo.ui.component.dialog.YesOrNoDialog
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.io.IOException

abstract class BaseActivity : AppCompatActivity() {


    var requestOffset = 0
    val requestCount = 10

    private lateinit var loading: BasePopupView
    private lateinit var popupLoading: PopupLoading
    private val compositeDisposable = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LLog.d("BaseActivity", "onCreate: $this ${savedInstanceState == null}")
        popupLoading = PopupLoading(this)
        loading = XPopup.Builder(this).isViewMode(true).hasShadowBg(false).isDestroyOnDismiss(false)
            .asCustom(popupLoading)

        statusBar()

        XEventBus.observe(this, IMEvent.OnlineStatusUpdate.value, Observer<Int> {
            onConnectStatus(it)
        })

        resetToolbar()

        findViewById<View>(android.R.id.content).setOnClickListener {
            KeyboardUtils.hideSoftInput(it)
        }
    }

    private fun resetToolbar() {
        val tb = getToolbar()
        tb?.let {
            val titleView: TextView = it.findViewById(R.id.tb_title)
            titleView.setTextColor(titleColor())
            titleView.text = toolbarTitle()

            val backView: ImageView = tb.findViewById(R.id.tb_iv_back)
            val backIconResId = backIcon()
            if (backIconResId != null) {
                backView.setImageResource(backIconResId)
                backView.visibility = View.VISIBLE
                backView.setOnClickListener {
                    onBackPressedDispatcher.onBackPressed()
                }
            } else {
                backView.visibility = View.GONE
            }

            val menu1: ImageView = tb.findViewById(R.id.tb_menu1)
            menu1.setOnClickListener { view ->
                onToolBarMenuClick(view)
            }
            val menu1Icon = menuIcon(R.id.tb_menu1)
            if (menu1Icon == null) {
                menu1.visibility = View.GONE
            } else {
                menu1.visibility = View.VISIBLE
                menu1.setImageDrawable(menuIcon(R.id.tb_menu1))
            }

            val menu2: ImageView = tb.findViewById(R.id.tb_menu2)
            menu2.setOnClickListener { view ->
                onToolBarMenuClick(view)
            }
            val menu2Icon = menuIcon(R.id.tb_menu2)
            if (menu2Icon == null) {
                menu2.visibility = View.GONE
            } else {
                menu2.visibility = View.VISIBLE
                menu2.setImageDrawable(menuIcon(R.id.tb_menu2))
            }

            val oprBtnTextView = tb.findViewById<TextView>(R.id.tb_btn_opr)
            oprBtnTextView.setOnClickListener { view ->
                onToolBarMenuClick(view)
            }
            val oprBtnText = oprBtnText()
            if (oprBtnText == null) {
                oprBtnTextView.visibility = View.GONE
            } else {
                oprBtnTextView.text = oprBtnText
                oprBtnTextView.setShape(
                    ContextCompat.getColor(this, R.color.primary),
                    floatArrayOf(14f, 14f, 14f, 14f),
                    false
                )
                oprBtnTextView.setTextColor(Color.WHITE)
            }

            val bgColor = toolbarBgColor()
            bgColor?.let { color ->
                it.setBackgroundColor(color)
            }
        }
    }

    open fun toolbarBgColor(): Int? {
        return Color.TRANSPARENT
    }

    open fun menuIcon(id: Int): Drawable? {
        return null
    }

    open fun oprBtnText(): String? {
        return null
    }

    open fun onToolBarMenuClick(view: View) {

    }

    open fun onConnectStatus(status: Int) {

    }

    open fun getToolbar(): Toolbar? {
        return null
    }

    open fun toolbarTitle(): String? {
        return null
    }

    fun updateToolTitle(title: String) {
        val tb = getToolbar()
        tb?.let {
            val titleView: TextView = it.findViewById(R.id.tb_title)
            titleView.text = title
        }
    }

    open fun backIcon(): Int? {
        return R.drawable.icon_black_back
    }

    open fun titleColor(): Int {
        return Color.parseColor("#333333")
    }

    fun addDispose(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    fun removeDispose(disposable: Disposable) {
        compositeDisposable.remove(disposable)
    }

    fun clearDispose() {
        compositeDisposable.clear()
    }

    override fun finish() {
        super.finish()
        IMKeyboardUtils.hideSoftInput(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        LLog.d("BaseActivity", "onDestroy: $this ")
        loading.destroy()
        popupLoading.destroy()
        compositeDisposable.clear()
    }

    open fun showLoading(
        cancelAble: Boolean = false, text: String = ContextCompat.getString(this, R.string.loading),
    ) {
        runOnUiThread {
            if (!loading.isShow) {
                loading.show()
            }
            popupLoading.setIsDismissOnBackPressed(cancelAble)
            popupLoading.setIsDismissOnTouchOutside(cancelAble)
        }
    }

    open fun dismissLoading() {
        runOnUiThread {
            if (loading.isShow) {
                loading.dismiss()
            }
        }
    }

    open fun statusBar() {
        ImmersionBar.with(this).transparentStatusBar().statusBarDarkFont(isDarkFont()).init()
    }

    open fun isDarkFont(): Boolean {
        return true
    }

    open fun showToast(text: String) {
        runOnUiThread {
            ToastUtils.showShort(text)
        }
    }

    open fun showToast(resId: Int) {
        runOnUiThread {
            ToastUtils.showShort(getString(resId))
        }
    }

    /**
     * 是否允许截屏
     */
    fun screenshotSafe(yes: Boolean) {
        if (!yes) window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        else window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    open fun showError(e: Throwable) {
        if (e is CodeMsgException) {
            onError(e)
        } else {
            if (e.message == null) {
                showToast(R.string.error_unknown)
            } else {
                if (e is IOException) {
                    showToast(R.string.error_network)
                } else {
                    showToast(e.message!!)
                }
            }
        }
    }

    open fun onError(e: CodeMsgException) {
        showToast(e.msg)
    }


    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LanguageUtils.attachBaseContext(newBase))
    }


    open fun showYesOrNoDialog(
        title: String,
        okString: String = ContextCompat.getString(this, R.string.confirm),
        cancelString: String = ContextCompat.getString(this, R.string.cancel),
        onOKListener: View.OnClickListener? = null,
    ) {
        YesOrNoDialog(this, title, okString, cancelString, onOKListener).show()
    }

    open fun showCommonDialog(
        title: String, content: String,
        okString: String = ContextCompat.getString(this, R.string.confirm),
        cancelString: String = ContextCompat.getString(this, R.string.cancel),
        listener: OnSubviewClickListener? = null,
    ) {
        CustomDialog(
            this,
            title,
            content,
            okString,
            cancelString,
            listener
        ).show()
    }

}