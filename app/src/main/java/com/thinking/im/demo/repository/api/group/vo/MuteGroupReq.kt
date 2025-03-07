package com.thinking.im.demo.repository.api.group.vo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
open class MuteGroupReq(
    @SerializedName("group_id")
    val groupId: Long,
    @SerializedName("mute_user_id")
    val muteUserId: Long?,
    @SerializedName("mute")
    val mute: Int, // 1 禁言 2 取消禁言
) : Parcelable