package com.thinking.im.demo.repository.api.group.vo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.thinking.im.demo.repository.api.group.vo.GroupVo
import kotlinx.parcelize.Parcelize

@Parcelize
open class QueryGroupsRes(
    @SerializedName("groups")
    val groups: List<GroupVo>,
) : Parcelable