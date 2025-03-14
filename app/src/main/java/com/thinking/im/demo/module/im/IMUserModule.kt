package com.thinking.im.demo.module.im

import com.thinking.im.demo.repository.DataRepository
import com.thk.im.android.core.IMCoreManager
import com.thk.im.android.core.db.entity.User
import com.thk.im.android.core.module.internal.DefaultUserModule
import io.reactivex.Flowable

class IMUserModule : DefaultUserModule() {
    override fun queryServerUser(id: Long): Flowable<User> {
        return DataRepository.userApi.queryUsers("$id").flatMap {
            val user = it.users["$id"]?.toUser()
            user?.let {
                IMCoreManager.getImDataBase().userDao().insertOrReplace(listOf(user))
            }
            return@flatMap Flowable.just(user)
        }
    }

    override fun queryServerUsers(ids: Set<Long>): Flowable<Map<Long, User>> {
        var i = 0
        var idStr = ""
        for (id in ids) {
            if (i == 0) {
                i += 1
            } else {
                idStr += ","
            }
            idStr += "$id"
        }
        return DataRepository.userApi.queryUsers(idStr)
            .flatMap {
                val insertUsers = mutableListOf<User>()
                val userMap = mutableMapOf<Long, User>()
                for ((_, u) in it.users) {
                    val user = u.toUser()
                    userMap[user.id] = user
                    insertUsers.add(user)
                }
                IMCoreManager.getImDataBase().userDao().insertOrReplace(insertUsers)
                Flowable.just(userMap)
            }
    }
}