package com.thinking.im.demo.ui.message.group

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.thinking.im.demo.R
import com.thinking.im.demo.databinding.ActivityGroupMemberApplyListBinding
import com.thinking.im.demo.repository.DataRepository
import com.thinking.im.demo.repository.MemberApplyStatus
import com.thinking.im.demo.repository.UIEvent
import com.thinking.im.demo.repository.api.group.vo.GroupMemberApplyVo
import com.thinking.im.demo.repository.api.group.vo.GroupVo
import com.thinking.im.demo.repository.api.group.vo.PostReviewGroupApplyReq
import com.thinking.im.demo.repository.api.group.vo.QueryJoinGroupAppliesRes
import com.thinking.im.demo.repository.api.group.vo.ReviewApplyRes
import com.thinking.im.demo.ui.base.BaseActivity
import com.thinking.im.demo.ui.component.OnSubviewTagClickListener
import com.thinking.im.demo.ui.message.adapter.GroupMemberApplyAdapter
import com.thk.im.android.core.IMCoreManager
import com.thk.im.android.core.base.BaseSubscriber
import com.thk.im.android.core.base.RxTransform
import com.thk.im.android.core.db.entity.Session
import com.thk.im.android.core.event.XEventBus
import com.thk.im.android.ui.manager.IMUIManager

class GroupMemberApplyListActivity : BaseActivity() {

    companion object {
        fun start(ctx: Context, session: Session, group: GroupVo) {
            val intent = Intent(ctx, GroupMemberApplyListActivity::class.java)
            intent.putExtra("group", group)
            intent.putExtra("session", session)
            ctx.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityGroupMemberApplyListBinding

    override fun getToolbar(): Toolbar {
        return binding.tbTop.toolbar
    }

    override fun toolbarTitle(): String {
        return ContextCompat.getString(this, R.string.add_group_apply)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityGroupMemberApplyListBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private fun group(): GroupVo? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("group", GroupVo::class.java)
        } else {
            intent.getParcelableExtra("group")
        }
    }

    private fun session(): Session? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("session", Session::class.java)
        } else {
            intent.getParcelableExtra("session")
        }
    }

    private fun reviewApply(apply: GroupMemberApplyVo, pass: Int) {
        showLoading()
        val subscriber = object : BaseSubscriber<ReviewApplyRes>() {
            override fun onNext(t: ReviewApplyRes?) {
                dismissLoading()
                t?.let {
                    reviewSuccess(it.applyCount, apply, pass)
                }
            }

            override fun onError(t: Throwable?) {
                super.onError(t)
                dismissLoading()
            }
        }
        val req = PostReviewGroupApplyReq(apply.id, pass)
        DataRepository.groupApi.reviewGroupApply(req)
            .compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
        addDispose(subscriber)
    }

    private fun reviewSuccess(applyCount: Long, vo: GroupMemberApplyVo, passed: Int) {
        vo.reviewStatus = passed
        val adapter = binding.rcvApplies.getAdapter() as? GroupMemberApplyAdapter
        adapter?.updateData(vo)

        val group = group() ?: return
        if (passed == MemberApplyStatus.Accepted.value) {
            IMCoreManager.messageModule.syncSessionMembers(group.sessionId)
        }
        if (group.applyCount != applyCount) {
            group.applyCount = applyCount
            XEventBus.post(UIEvent.GroupUpdate.value, group)
        }
    }


    private fun initView() {
        val adapter = GroupMemberApplyAdapter()
        adapter.onSubviewTagClickListener = object : OnSubviewTagClickListener {
            override fun onClick(data: Any, action: String, view: View?) {
                val apply = data as? GroupMemberApplyVo ?: return
                when (action) {
                    "user" -> {
                        session()?.let {
                            IMUIManager.pageRouter?.openUserPage(
                                this@GroupMemberApplyListActivity, apply.applyUser.toUser(), it
                            )
                        }
                    }

                    "accept" -> {
                        reviewApply(apply, MemberApplyStatus.Accepted.value)
                    }

                    "reject" -> {
                        reviewApply(apply, MemberApplyStatus.Reject.value)
                    }
                }
            }
        }
        binding.rcvApplies.setAdapter(adapter)
        binding.rcvApplies.setLayoutManager(LinearLayoutManager(this))

        binding.rcvApplies.setOnRefreshListener {
            loadData(true, isLoadMore = false)
        }

        binding.rcvApplies.setOnLoadMoreListener {
            loadData(false, isLoadMore = true)
        }

        loadData(false, isLoadMore = false)
    }

    private fun loadData(isRefresh: Boolean, isLoadMore: Boolean) {
        val group = group() ?: return
        val adapter = binding.rcvApplies.getAdapter() as? GroupMemberApplyAdapter? ?: return
        requestOffset = if (isLoadMore) {
            adapter.itemCount
        } else {
            0
        }

        val subscriber = object : BaseSubscriber<QueryJoinGroupAppliesRes>() {
            override fun onNext(t: QueryJoinGroupAppliesRes?) {
                t?.let {
                    loadSuccess(it.applies, isRefresh, isLoadMore)
                }
            }

            override fun onError(t: Throwable?) {
                super.onError(t)
                t?.let {
                    loadFailed(it, isRefresh, isLoadMore)
                }
            }
        }

        DataRepository.groupApi.queryJoinGroupApplies(group.id, requestCount, requestOffset)
            .compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
        addDispose(subscriber)

    }

    private fun loadFailed(t: Throwable, isRefresh: Boolean, isLoadMore: Boolean) {
        if (isRefresh) {
            binding.rcvApplies.finishRefresh(false)
        }
        if (isLoadMore) {
            binding.rcvApplies.finishLoadMore(false)
        }
        showError(t)
    }

    private fun loadSuccess(
        applies: List<GroupMemberApplyVo>, isRefresh: Boolean, isLoadMore: Boolean,
    ) {
        val noMoreData = applies.size < requestCount
        if (isRefresh) {
            binding.rcvApplies.finishRefresh(true, noMoreData)
        } else if (isLoadMore) {
            binding.rcvApplies.finishLoadMore(success = true, noMoreData)
        } else if (noMoreData) {
            binding.rcvApplies.finishWithNoMoreData()
        }

        val adapter = binding.rcvApplies.getAdapter() as? GroupMemberApplyAdapter? ?: return
        if (isLoadMore) {
            adapter.addData(applies)
        } else {
            adapter.setData(applies)
        }
    }
}