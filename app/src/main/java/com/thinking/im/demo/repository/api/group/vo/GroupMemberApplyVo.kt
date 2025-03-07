package com.thinking.im.demo.repository.api.group.vo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.thinking.im.demo.repository.api.user.vo.BasisUserVo
import kotlinx.parcelize.Parcelize

@Parcelize
open class GroupMemberApplyVo(
    @SerializedName("id")
    val id: Long,
    @SerializedName("apply_user")
    val applyUser: BasisUserVo,
    @SerializedName("group_id")
    val groupId: Long,
    @SerializedName("group_name")
    val groupName: String,
    @SerializedName("apply_content")
    val applyContent: String,
    @SerializedName("review_status")
    var reviewStatus: Int,
    @SerializedName("create_time")
    val createTime: Long,
    @SerializedName("update_time")
    val updateTime: Long,
) : Parcelable