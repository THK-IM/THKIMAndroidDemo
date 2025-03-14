package com.thinking.im.demo.repository.api.appendix.vo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
open class PostAppendixParamsRes(
    @SerializedName("url")
    val url: String,
    @SerializedName("method")
    val method: String,
    @SerializedName("upload_key")
    val uploadKey: String,
    @SerializedName("params")
    val params: Map<String, String>,
) : Parcelable