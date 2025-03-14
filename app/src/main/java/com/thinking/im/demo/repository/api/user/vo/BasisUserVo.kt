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
    @SerializedName("nickname")
    var nickname: String?,
    @SerializedName("avatar")
    var avatar: String?,
    @SerializedName("sex")
    var sex: Int?,
) : Parcelable {

    companion object {
        fun fromUser(user: User): BasisUserVo {
            return BasisUserVo(
                user.id, user.nickname, user.avatar, 0,
            )
        }
    }

    fun toUser(): User {
        val now = IMCoreManager.severTime
        return User(id, "", nickname ?: "", avatar, sex, null, null, now, now)
    }


}