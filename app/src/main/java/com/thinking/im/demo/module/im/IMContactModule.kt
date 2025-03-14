package com.thinking.im.demo.module.im

import android.content.Context
import com.thinking.im.demo.repository.DataRepository
import com.thinking.im.demo.repository.api.contact.vo.ContactVo
import com.thk.im.android.core.IMCoreManager
import com.thk.im.android.core.api.vo.ListVo
import com.thk.im.android.core.base.BaseSubscriber
import com.thk.im.android.core.base.RxTransform
import com.thk.im.android.core.db.entity.Contact
import com.thk.im.android.core.module.internal.DefaultContactModule
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable

class IMContactModule : DefaultContactModule() {
    private val spName = "Contact"
    private val lastSyncContactTime = "Last_Sync_Contact_Time"
    private val disposables = CompositeDisposable()

    private fun setContactSyncTime(time: Long): Boolean {
        val app = IMCoreManager.app
        val sp = app.getSharedPreferences(spName, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putLong("${lastSyncContactTime}_${IMCoreManager.uId}", time)
        return editor.commit()
    }

    private fun getContactLastSyncTime(): Long {
        val app = IMCoreManager.app
        val sp = app.getSharedPreferences(spName, Context.MODE_PRIVATE)
        return sp.getLong("${lastSyncContactTime}_${IMCoreManager.uId}", 0)
    }

    override fun syncContacts() {
        val uId = IMCoreManager.uId
        val mTime = getContactLastSyncTime()
        val count = 100
        val offset = 0

        val subscribe = object : BaseSubscriber<ListVo<ContactVo>>() {
            override fun onNext(t: ListVo<ContactVo>?) {
                t?.let { vo ->
                    val contactList = vo.data.map {
                        it.toContact()
                    }
                    IMCoreManager.db.contactDao().insertOrReplace(contactList)
                    if (t.data.isNotEmpty()) {
                        val success = setContactSyncTime(t.data.last().updateTime)
                        if (success && t.data.size >= count) {
                            syncContacts() // 递归调用
                        }
                    }


                }
            }
        }

        DataRepository.contactApi.queryLatestContactList(uId, mTime, count, offset)
            .compose(RxTransform.flowableToIo())
            .subscribe(subscribe)
        disposables.add(subscribe)
    }

    override fun queryContactsByUserIds(ids: List<Long>): Flowable<List<Contact>> {
        var contactIds = ""
        for ((index, id) in ids.withIndex()) {
            if (index < (ids.size - 1)) {
                contactIds += "$id,"
            } else {
                contactIds += "$id"
            }
        }
        val selfId = IMCoreManager.uId
        return DataRepository.contactApi.queryContactByUserIds(contactIds, selfId).flatMap { res ->
            val contacts = mutableListOf<Contact>()
            for (vo in res.data) {
                contacts.add(vo.toContact())
            }
            return@flatMap Flowable.just(contacts)
        }
    }

    override fun queryServerContactByUserId(id: Long): Flowable<Contact> {
        return DataRepository.contactApi.queryContactByUserId(id)
            .flatMap {
                Flowable.just(it.contact.toContact())
            }
    }

    override fun reset() {
        super.reset()
        disposables.clear()
    }
}