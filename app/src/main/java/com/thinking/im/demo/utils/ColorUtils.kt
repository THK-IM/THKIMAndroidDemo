package com.thinking.im.demo.utils


fun Int.withAlpha(alpha: Float): Int {
    val newAlpha = (alpha * 255).toInt().coerceIn(0, 255) // 确保在 0~255 范围内
    return (this and 0x00FFFFFF) or (newAlpha shl 24) // 重新设置 Alpha 通道
}