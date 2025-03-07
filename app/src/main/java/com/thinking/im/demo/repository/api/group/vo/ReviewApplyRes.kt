package com.thinking.im.demo.repository.api.group.vo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
open class ReviewApplyRes(
    @SerializedName("apply_count")
    val applyCount: Long,
) : Parcelable