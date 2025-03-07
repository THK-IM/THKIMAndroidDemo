package com.thinking.im.demo.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.WindowInsets
import android.view.WindowManager


object ScreenUtils {

    fun getStatusBarHeight(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        if (wm != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowInsets = wm.currentWindowMetrics.getWindowInsets()
            val insets = windowInsets.getInsetsIgnoringVisibility(
                WindowInsets.Type.navigationBars().or(WindowInsets.Type.displayCutout())
            )
            return insets.top
        } else {
            var statusBarH = 0
            //获取status_bar_height资源的ID
            val resourceId: Int =
                context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                //根据资源ID获取响应的尺寸值
                statusBarH = context.resources.getDimensionPixelSize(resourceId)
            }
            return statusBarH
        }
    }

    /**
     * 获取导航栏高度
     */
    fun getNavigationBarHeight(context: Context): Int {
        var navigationBarHeight = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // Android 11+
            val insets = (context as Activity).window.decorView.rootWindowInsets
            if (insets != null) {
                navigationBarHeight = insets.getInsets(WindowInsets.Type.navigationBars()).bottom
            }
        } else {
            // 旧版本的获取方式
            val resourceId =
                context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
            if (resourceId > 0) {
                navigationBarHeight = context.resources.getDimensionPixelSize(resourceId)
            }
        }
        return navigationBarHeight
    }
}