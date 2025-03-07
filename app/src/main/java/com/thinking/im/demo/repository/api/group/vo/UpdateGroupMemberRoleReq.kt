package com.thinking.im.demo.repository.api.group.vo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
open class UpdateGroupMemberRoleReq(
    @SerializedName("group_id") val groupId: Long,
    @SerializedName("member_user_id") val memberUserId: Long,
    @SerializedName("role") val role: Int
) : Parcelable