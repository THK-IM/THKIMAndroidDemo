package com.thinking.im.demo.repository.api.group.vo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
open class KickoffGroupMemberReq(
    @SerializedName("id")
    val id: Long,
    @SerializedName("kick_user_id")
    val kickUserId: Long,
) : Parcelable