package com.thinking.im.demo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.thinking.im.demo.databinding.FragmentGroupBinding
import com.thk.im.android.core.IMCoreManager
import com.thk.im.android.core.base.BaseSubscriber
import com.thk.im.android.core.base.RxTransform
import com.thk.im.android.core.db.entity.Group
import com.thinking.im.demo.ui.base.BaseFragment
import com.thinking.im.demo.ui.fragment.adapter.GroupAdapter

class GroupFragment : BaseFragment() {

    private lateinit var binding: FragmentGroupBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = GroupAdapter(this)
        binding.rcvGroup.layoutManager = LinearLayoutManager(context)
        binding.rcvGroup.adapter = adapter
        queryAllContacts()
    }

    private fun queryAllContacts() {
        val subscriber = object : BaseSubscriber<List<Group>>() {
            override fun onNext(t: List<Group>?) {
                t?.let {
                    setGroupList(it)
                }
            }

            override fun onComplete() {
                super.onComplete()
                removeDispose(this)
            }
        }
        IMCoreManager.groupModule.queryAllGroups()
            .compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
        addDispose(subscriber)
    }

    private fun setGroupList(it: List<Group>) {
        val adapter = binding.rcvGroup.adapter as GroupAdapter
        adapter.setGroupList(it)
    }
}