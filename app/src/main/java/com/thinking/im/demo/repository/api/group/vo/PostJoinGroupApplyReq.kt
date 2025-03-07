package com.thinking.im.demo.repository.api.group.vo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
open class PostJoinGroupApplyReq(
    @SerializedName("group_id")
    val groupId: Long,
    @SerializedName("apply_content")
    val applyContent: String,
) : Parcelable