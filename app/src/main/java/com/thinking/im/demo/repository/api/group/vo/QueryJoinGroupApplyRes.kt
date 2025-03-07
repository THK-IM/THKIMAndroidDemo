package com.thinking.im.demo.repository.api.group.vo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.thinking.im.demo.repository.api.group.vo.GroupMemberApplyVo
import kotlinx.parcelize.Parcelize


@Parcelize
open class QueryJoinGroupApplyRes(
    @SerializedName("apply")
    val apply: GroupMemberApplyVo,
) : Parcelable