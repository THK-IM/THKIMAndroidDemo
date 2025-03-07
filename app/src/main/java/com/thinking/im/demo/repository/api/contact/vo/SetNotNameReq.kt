package com.thinking.im.demo.repository.api.contact.vo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
open class SetNotNameReq(
    @SerializedName("u_id")
    val uId: Long,
    @SerializedName("contact_id")
    val contactId: Long,
    @SerializedName("note_name")
    val noteName: String,
) : Parcelable
