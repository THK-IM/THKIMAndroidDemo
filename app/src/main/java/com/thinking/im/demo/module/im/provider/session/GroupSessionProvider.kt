package com.thinking.im.demo.module.im.provider.session

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.thk.im.android.core.SessionType
import com.thk.im.android.ui.fragment.viewholder.session.IMBaseSessionVH
import com.thk.im.android.ui.protocol.IMBaseSessionIVProvider
import com.thk.im.android.ui.provider.session.viewholder.GroupSessionVH

class GroupSessionProvider : IMBaseSessionIVProvider() {
    override fun sessionType(): Int {
        return SessionType.Group.value
    }

    override fun viewHolder(
        lifecycleOwner: LifecycleOwner,
        viewType: Int,
        parent: ViewGroup,
    ): IMBaseSessionVH {
        return GroupSessionVH(lifecycleOwner, parent)
    }
}