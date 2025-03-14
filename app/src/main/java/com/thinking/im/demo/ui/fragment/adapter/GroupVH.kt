package com.thinking.im.demo.ui.fragment.adapter

import android.view.View
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import com.thinking.im.demo.databinding.ItemviewGroupBinding
import com.thk.im.android.core.IMCoreManager
import com.thk.im.android.core.base.BaseSubscriber
import com.thk.im.android.core.base.IMImageLoader
import com.thk.im.android.core.base.RxTransform
import com.thk.im.android.core.db.entity.Group
import com.thk.im.android.core.db.entity.Session
import com.thk.im.android.ui.fragment.viewholder.IMBaseVH
import com.thk.im.android.ui.manager.IMUIManager
import io.reactivex.disposables.CompositeDisposable

class GroupVH(liftOwner: LifecycleOwner, itemView: View) :
    IMBaseVH(liftOwner, itemView) {
    private val disposable = CompositeDisposable()
    private val binding = ItemviewGroupBinding.bind(itemView)

    fun onBind(group: Group) {
        displayAvatar(binding.ivAvatar, group.avatar)
        binding.tvNickname.text = group.name
        itemView.setOnClickListener {
            openSession(group.sessionId)
        }
    }

    private fun displayAvatar(imageView: ImageView, url: String) {
        IMImageLoader.displayImageUrl(imageView, url)
    }

    private fun openSession(sessionId: Long) {
        val subscriber = object : BaseSubscriber<Session>() {
            override fun onNext(t: Session?) {
                t?.let {
                    IMUIManager.pageRouter?.openSession(itemView.context, it)
                }
            }

            override fun onComplete() {
                super.onComplete()
                disposable.remove(this)
            }
        }
        IMCoreManager.messageModule.getSession(sessionId)
            .compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
        disposable.add(subscriber)
    }

    override fun onViewDetached() {
        super.onViewDetached()
        disposable.clear()
    }


}