package com.thinking.im.demo.repository.api.user.vo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.thk.im.android.core.IMCoreManager
import com.thk.im.android.core.db.entity.User
import kotlinx.parcelize.Parcelize

@Parcelize
open class BasisUserVo(
    @SerializedName("id")
    val id: Long,
    @SerializedName("display_id")
    var displayId: String,
    @SerializedName("vip_level")
    var vipLevel: Int,
    @SerializedName("nickname")
    var nickname: String?,
    @SerializedName("avatar")
    var avatar: String?,
    @SerializedName("sex")
    var sex: Int?,
    @SerializedName("is_auth")
    var isAuth: Int?,
    @SerializedName("bg_url")
    var bgUrl: String?,
    @SerializedName("signature")
    var signature: String?,
) : Parcelable {

    companion object {
        fun fromUser(user: User): BasisUserVo {
            val vipLevel = 0
            val isAuth = 0
            val bgUrl: String? = null
            val signature: String? = null
            return BasisUserVo(
                user.id, user.displayId, vipLevel, user.nickname,
                user.avatar, user.sex, isAuth, bgUrl, signature
            )
        }
    }

    fun toUser(): User {
        val now = IMCoreManager.severTime
        return User(id, displayId, nickname ?: "", avatar, sex, null, null, now, now)
    }


}