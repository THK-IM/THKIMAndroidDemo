package com.thinking.im.demo.repository.api.user.vo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class QueryUsersRes(
    @SerializedName("users")
    val users: Map<String, BasisUserVo>,
) : Parcelable