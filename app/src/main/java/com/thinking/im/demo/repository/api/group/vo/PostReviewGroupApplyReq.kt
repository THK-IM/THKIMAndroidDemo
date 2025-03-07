package com.thinking.im.demo.repository.api.group.vo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
open class PostReviewGroupApplyReq(
    @SerializedName("apply_id")
    val applyId: Long,
    @SerializedName("passed")
    val passed: Int,
) : Parcelable