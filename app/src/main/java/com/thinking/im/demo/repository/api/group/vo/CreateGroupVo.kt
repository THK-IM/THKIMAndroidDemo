package com.thinking.im.demo.repository.api.group.vo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
open class CreateGroupVo(
    @SerializedName("name")
    val name: String,
    @SerializedName("introduce")
    val introduce: String,
    @SerializedName("avatar")
    val avatar: String,
) : Parcelable