package com.thinking.im.demo.module.im

import com.thk.im.android.core.db.entity.Group
import com.thk.im.android.core.module.internal.DefaultGroupModule
import com.thinking.im.demo.repository.DataRepository
import io.reactivex.Flowable

class IMGroupModule : DefaultGroupModule() {

    override fun queryServerGroup(id: Long): Flowable<Group?> {
        return DataRepository.groupApi.queryGroup(id)
            .flatMap { vo ->
                val group = vo.group.toGroup()
                Flowable.just(group)
            }
    }
}