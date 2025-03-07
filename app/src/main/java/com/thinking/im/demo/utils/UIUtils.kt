package com.thinking.im.demo.utils

import android.annotation.SuppressLint
import android.content.Context
import com.thk.im.android.core.base.utils.AppUtils


object UIUtils {

    @SuppressLint("InternalInsetResource")
    fun statusBarPxHeight(ctx: Context): Int {
        var height = 0
        val resourceId = ctx.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            height = ctx.resources.getDimensionPixelSize(resourceId)
        }
        return height
    }

    @SuppressLint("InternalInsetResource")
    fun statusBarDpHeight(ctx: Context): Int {
        var height = 0
        val resourceId = ctx.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            height = ctx.resources.getDimensionPixelSize(resourceId)
        }
        return AppUtils.instance().px2dp(height.toFloat()).toInt()
    }

}