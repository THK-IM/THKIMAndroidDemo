package com.thinking.im.demo.ui.base

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.lxj.xpopup.util.KeyboardUtils
import com.thinking.im.demo.R
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseFragment : Fragment() {

    var requestOffset = 0
    var requestCount = 10

    private val compositeDisposable = CompositeDisposable()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnClickListener {
            KeyboardUtils.hideSoftInput(view)
        }
    }

    override fun onResume() {
        super.onResume()
        if (parentFragment == null) {
            (requireActivity() as? BaseActivity)?.statusBar()
        }
    }

    open fun topPadding(): Boolean {
        return false
    }

    open fun onToolBarMenuClick(view: View) {

    }

    open fun onConnectStatus(status: Int) {

    }

    open fun getToolbar(): Toolbar? {
        return null
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

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    open fun showLoading(
        cancelAble: Boolean = false,
        text: String = ContextCompat.getString(requireContext(), R.string.loading),
    ) {
        (activity as BaseActivity?)?.showLoading(cancelAble, text)
    }

    open fun dismissLoading() {
        (activity as BaseActivity?)?.dismissLoading()
    }

    open fun showToast(text: String) {
        (activity as BaseActivity?)?.showToast(text)
    }

    open fun showToast(resId: Int) {
        (activity as BaseActivity?)?.showToast(resId)
    }

    open fun showError(e: Throwable) {
        (activity as BaseActivity?)?.showError(e)
    }

    fun openAppSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", requireContext().applicationContext.packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    open fun scrollToTop() {

    }

}