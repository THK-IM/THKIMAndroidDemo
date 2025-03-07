package com.thinking.im.demo.repository.api.group.vo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.thinking.im.demo.repository.api.user.vo.BasisUserVo
import com.thk.im.android.core.db.entity.Group
import kotlinx.parcelize.Parcelize

@Parcelize
open class GroupVo(
    @SerializedName("id")
    val id: Long,
    @SerializedName("display_id")
    val displayId: String,
    @SerializedName("session_id")
    val sessionId: Long,
    @SerializedName("owner_id")
    val ownerId: Long,
    @SerializedName("name")
    var name: String,
    @SerializedName("announce")
    val announce: String,
    @SerializedName("introduce")
    var introduce: String,
    @SerializedName("avatar")
    var avatar: String,
    @SerializedName("topic")
    val topic: String,
    @SerializedName("member_count")
    val memberCount: Int,
    @SerializedName("enter_flag")
    var enterFlag: Int,
    @SerializedName("ext_data")
    val extData: String?,
    @SerializedName("create_time")
    val createTime: Long,
    @SerializedName("update_time")
    val updateTime: Long,
    @SerializedName("online_count")
    var onlineCount: Long?,
    @SerializedName("apply_count")
    var applyCount: Long?,
    @SerializedName("recommend_reason_1")
    var recommendReason1: String?,
    @SerializedName("recommend_reason_2")
    var recommendReason2: String?,
    @SerializedName("creator")
    val creator: BasisUserVo?,
) : Parcelable {

    fun toGroup(): Group {
        return Group(
            id, displayId, name, sessionId, ownerId, avatar, announce, "",
            enterFlag, memberCount, extData, createTime, updateTime
        )
    }

}