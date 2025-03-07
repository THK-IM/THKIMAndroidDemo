package com.thinking.im.demo.repository.api.group.vo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
open class DisbandGroupReq(
    @SerializedName("id")
    val id: Long,
) : Parcelable