package com.thinking.im.demo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.thinking.im.demo.databinding.FragmentRelationListBinding
import com.thinking.im.demo.repository.DataRepository
import com.thinking.im.demo.repository.api.user.vo.BasisUserVo
import com.thinking.im.demo.ui.base.BaseRelationFragment
import com.thinking.im.demo.ui.fragment.adapter.RelationAdapter
import com.thinking.im.demo.ui.fragment.adapter.RelationListener
import com.thk.im.android.core.IMCoreManager
import com.thk.im.android.core.base.BaseSubscriber
import com.thk.im.android.core.base.LLog
import com.thk.im.android.core.base.RxTransform
import com.thk.im.android.core.db.entity.Contact
import io.reactivex.Flowable

class ContactFragment : BaseRelationFragment() {

    private lateinit var binding: FragmentRelationListBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRelationListBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.initView()
    }

    private fun getRelation(): Int {
        LLog.d("getRelation", "${arguments?.getInt("relation")}")
        return arguments?.getInt("relation") ?: 0
    }

    private fun initView() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.xrvRelation.setLayoutManager(layoutManager)
        val adapter = RelationAdapter()
        adapter.relationListener = object : RelationListener {
            override fun onContactClick(contact: Contact, opr: String) {
                when (opr) {
                    "removeBlack" -> {
                        black(contact, false)
                    }

                    "follow" -> {
                        follow(contact, true)
                    }

                    "unFollow" -> {
                        follow(contact, false)
                    }

                    "more" -> {
                        showContactMenu(contact)
                    }

                    else -> {

                    }
                }
            }

        }
        binding.xrvRelation.setAdapter(adapter)
        binding.xrvRelation.setOnRefreshListener {
            loadData(isRefresh = true, isLoadMore = false)
        }
        binding.xrvRelation.setOnLoadMoreListener {
            loadData(isRefresh = false, isLoadMore = true)
        }

        loadData(isRefresh = false, isLoadMore = true)
    }

    private fun loadData(isRefresh: Boolean, isLoadMore: Boolean) {
        val adapter = binding.xrvRelation.getAdapter() as? RelationAdapter? ?: return
        requestOffset = if (isLoadMore) {
            adapter.itemCount
        } else {
            0
        }

        val subscriber = object : BaseSubscriber<List<Pair<Contact, BasisUserVo>>>() {

            override fun onNext(t: List<Pair<Contact, BasisUserVo>>?) {
                t?.let {
                    loadSuccess(it, isRefresh, isLoadMore)
                }
            }

            override fun onError(t: Throwable?) {
                super.onError(t)
                t?.let {
                    loadFailed(it, isRefresh, isLoadMore)
                }
            }
        }

        DataRepository.contactApi.queryContactList(
            IMCoreManager.uId, getRelation(), requestCount, requestOffset,
        ).flatMap {
            val contacts = mutableListOf<Contact>()
            val uIds = mutableSetOf<Long>()
            for (c in it.data) {
                val contact = c.toContact()
                contacts.add(contact)
                uIds.add(c.id)
            }
            if (contacts.size > 0) {
                IMCoreManager.getImDataBase().contactDao().insertOrReplace(contacts)
                IMCoreManager.userModule.queryServerUsers(uIds).flatMap { userMap ->
                    val pairs = mutableListOf<Pair<Contact, BasisUserVo>>()
                    for (c in contacts) {
                        val user = userMap[c.id] ?: continue
                        val pair = Pair(c, BasisUserVo.fromUser(user))
                        pairs.add(pair)
                    }
                    Flowable.just(pairs)
                }
            } else {
                return@flatMap Flowable.just(listOf<Pair<Contact, BasisUserVo>>())
            }
        }.compose(RxTransform.flowableToMain()).subscribe(subscriber)
        addDispose(subscriber)
    }

    private fun loadFailed(t: Throwable, isRefresh: Boolean, isLoadMore: Boolean) {
        if (isRefresh) {
            binding.xrvRelation.finishRefresh(false)
        }
        if (isLoadMore) {
            binding.xrvRelation.finishLoadMore(false)
        }
        showError(t)
    }

    private fun loadSuccess(
        pairs: List<Pair<Contact, BasisUserVo>>,
        isRefresh: Boolean,
        isLoadMore: Boolean,
    ) {
        val noMoreData = pairs.size < requestCount
        if (isRefresh) {
            binding.xrvRelation.finishRefresh(true, noMoreData)
        } else if (isLoadMore) {
            binding.xrvRelation.finishLoadMore(success = true, noMoreData)
        } else if (noMoreData) {
            binding.xrvRelation.finishWithNoMoreData()
        }

        val adapter = binding.xrvRelation.getAdapter() as? RelationAdapter? ?: return
        if (isLoadMore) {
            adapter.addData(pairs)
        } else {
            adapter.setData(pairs)
        }
    }

    override fun contactRelationUpdated(contact: Contact) {
        super.contactRelationUpdated(contact)
        val adapter = binding.xrvRelation.getAdapter() as? RelationAdapter? ?: return
        adapter.updateData(contact)
    }
}