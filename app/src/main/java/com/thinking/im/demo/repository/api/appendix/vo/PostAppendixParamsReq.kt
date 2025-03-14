package com.thinking.im.demo.repository.api.appendix.vo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
open class PostAppendixParamsReq(
    @SerializedName("business")
    val business: String,
    @SerializedName("filename")
    val filename: String,
) : Parcelable