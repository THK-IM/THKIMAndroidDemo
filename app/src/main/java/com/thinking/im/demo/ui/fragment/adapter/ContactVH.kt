package com.thinking.im.demo.ui.fragment.adapter

import android.view.View
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import com.thinking.im.demo.databinding.ItemviewContactBinding
import com.thk.im.android.core.IMCoreManager
import com.thk.im.android.core.base.BaseSubscriber
import com.thk.im.android.core.base.IMImageLoader
import com.thk.im.android.core.base.RxTransform
import com.thk.im.android.core.db.entity.Contact
import com.thk.im.android.core.db.entity.User
import com.thk.im.android.ui.fragment.viewholder.IMBaseVH
import io.reactivex.disposables.CompositeDisposable


class ContactVH(liftOwner: LifecycleOwner, itemView: View) :
    IMBaseVH(liftOwner, itemView) {

    private val disposable = CompositeDisposable()
    private val binding = ItemviewContactBinding.bind(itemView)

    fun onBind(contact: Contact, selected: Boolean, operator: ContactItemOperator) {
        showUserInfo(contact)
        contact.noteName?.let {
            binding.tvNickname.text = it
        }
        setRelationText(contact.relation)
        itemView.setOnClickListener {
            operator.onItemClick(contact)
        }
        if (selected) {
            binding.ivSelected.visibility = View.VISIBLE
        } else {
            binding.ivSelected.visibility = View.GONE
        }
    }

    private fun showUserInfo(contact: Contact) {
        val subscriber = object : BaseSubscriber<User>() {
            override fun onNext(t: User) {
                t.avatar?.let {
                    displayAvatar(binding.ivAvatar, it)
                }
                if (contact.noteName == null) {
                    binding.tvNickname.text = t.nickname
                }
            }

            override fun onComplete() {
                super.onComplete()
                disposable.remove(this)
            }
        }
        IMCoreManager.userModule.queryUser(contact.id)
            .compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
        disposable.add(subscriber)
    }

    private fun displayAvatar(imageView: ImageView, url: String) {
        IMImageLoader.displayImageUrl(imageView, url)
    }

    private fun setRelationText(relation: Int) {
        val builder = StringBuilder()
        if (relation.and(8) > 0) {
            builder.append("我关注了他; ")
        }
        if (relation.and(16) > 0) {
            builder.append(" 他关注了我; ")
        }
        if (relation.and(32) > 0) {
            builder.append(" 好友关系; ")
        }
        binding.tvRelation.text = builder.toString()
    }

    override fun onViewDetached() {
        super.onViewDetached()
        disposable.clear()
    }
}