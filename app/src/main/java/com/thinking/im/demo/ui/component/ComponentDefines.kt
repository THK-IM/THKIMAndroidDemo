package com.thinking.im.demo.ui.component

import android.view.View

interface OnTextChangeListener {
    fun onTextChange(text: String)
}

interface OnSubviewClickListener {
    fun onClick(tag: String)
}

interface OnSubviewTagClickListener {
    fun onClick(data: Any, action: String, view: View?)
}

interface OnObjectViewClickListener {
    fun onClick(tag: String, ob: Any)
}

interface OnContentChange {
    fun onChange()
}
