package com.thinking.im.demo.repository.api.contact.vo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
open class ContactSummary(
    @SerializedName("follow_count")
    val followCount: Long,
    @SerializedName("be_follow_count")
    val beFollowCount: Long,
) : Parcelable