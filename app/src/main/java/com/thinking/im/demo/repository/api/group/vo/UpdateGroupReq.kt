package com.thinking.im.demo.repository.api.group.vo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
open class UpdateGroupReq(
    @SerializedName("group_id")
    val groupId: Long,
    @SerializedName("name")
    val name: String?,
    @SerializedName("introduce")
    val introduce: String?,
    @SerializedName("avatar")
    val avatar: String?,
    @SerializedName("function_flag")
    val functionFlag: Long?,
    @SerializedName("mute")
    val mute: Int?,
    @SerializedName("enter_flag")
    val enterFlag: Int?,
) : Parcelable {

}