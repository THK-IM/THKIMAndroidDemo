package com.thinking.im.demo.repository.api.contact.vo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
open class QueryContactRes(
    @SerializedName("contact")
    val contact: ContactVo,
) : Parcelable