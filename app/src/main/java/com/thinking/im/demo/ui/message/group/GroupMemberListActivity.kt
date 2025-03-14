package com.thinking.im.demo.ui.message.group

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.thinking.im.demo.R
import com.thinking.im.demo.databinding.ActivityGroupMemberListBinding
import com.thinking.im.demo.repository.DataRepository
import com.thinking.im.demo.repository.api.group.vo.GroupVo
import com.thinking.im.demo.repository.api.group.vo.KickoffGroupMemberReq
import com.thinking.im.demo.repository.api.group.vo.UpdateGroupMemberRoleReq
import com.thinking.im.demo.ui.base.BaseActivity
import com.thinking.im.demo.ui.component.OnSubviewTagClickListener
import com.thinking.im.demo.ui.component.dialog.BottomMenuDialog
import com.thinking.im.demo.ui.message.adapter.GroupMemberAdapter
import com.thk.im.android.core.IMCoreManager
import com.thk.im.android.core.SessionRole
import com.thk.im.android.core.base.BaseSubscriber
import com.thk.im.android.core.base.RxTransform
import com.thk.im.android.core.base.extension.setShape
import com.thk.im.android.core.db.entity.Session
import com.thk.im.android.core.db.entity.SessionMember
import com.thk.im.android.core.db.entity.User
import com.thk.im.android.ui.manager.IMUIManager
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class GroupMemberListActivity : BaseActivity() {

    companion object {
        fun start(ctx: Context, session: Session, group: GroupVo) {
            val intent = Intent(ctx, GroupMemberListActivity::class.java)
            intent.putExtra("group", group)
            intent.putExtra("session", session)
            ctx.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityGroupMemberListBinding

    private val keywordPublisher = PublishSubject.create<String>()
    private var keywords: String = ""
    private var allMembers = mutableListOf<Pair<SessionMember, User>>()

    override fun getToolbar(): Toolbar {
        return binding.tbTop.toolbar
    }

    override fun toolbarTitle(): String {
        return ContextCompat.getString(this, R.string.group_member)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityGroupMemberListBinding.inflate(layoutInflater)
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

    private fun myRole(): Int {
        val session = session() ?: return SessionRole.Member.value
        return session.role
    }

    private fun initView() {
        val consumer = Consumer<String> {
            keywords = it
            this@GroupMemberListActivity.runOnUiThread {
                loadData(isRefresh = false, isLoadMore = false)
            }
        }
        val subscriber = keywordPublisher.debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(consumer)
        addDispose(subscriber)

        binding.lySearch.setShape(
            Color.parseColor("#E8E8E8"),
            floatArrayOf(8f, 8f, 8f, 8f),
            false
        )
        binding.ivClear.setShape(
            Color.parseColor("#80999999"),
            floatArrayOf(15f, 15f, 15f, 15f),
            false
        )
        binding.ivClear.setOnClickListener {
            binding.etSearch.setText("")
        }

        binding.etSearch.addTextChangedListener {
            it?.toString()?.let { keywords ->
                if (!TextUtils.isEmpty(keywords)) {
                    binding.ivClear.visibility = View.VISIBLE
                } else {
                    binding.ivClear.visibility = View.INVISIBLE
                }
                keywordPublisher.onNext(keywords)
            }
        }

        val adapter = GroupMemberAdapter()
        adapter.myRole = myRole()
        adapter.listener = object : OnSubviewTagClickListener {
            override fun onClick(data: Any, action: String, view: View?) {
                val sessionMember = data as? Pair<*, *> ?: return
                val user = sessionMember.second as? User ?: return
                when (action) {
                    "user" -> {
                        session()?.let {
                            IMUIManager.pageRouter?.openUserPage(
                                this@GroupMemberListActivity, user, it
                            )
                        }
                    }

                    "remove" -> {
                        val member = sessionMember.first as? SessionMember ?: return
                        removeMember(Pair(member, user))
                    }

                    "more" -> {
                        val member = sessionMember.first as? SessionMember ?: return
                        showMoreMenu(Pair(member, user))
                    }
                }
            }

        }
        binding.rcvMembers.setAdapter(adapter)
        binding.rcvMembers.setLayoutManager(LinearLayoutManager(this))
        binding.rcvMembers.setOnRefreshListener {
            loadData(true, isLoadMore = false)
        }
        binding.rcvMembers.setOnLoadMoreListener {
            loadData(false, isLoadMore = true)
        }

        loadData(false, isLoadMore = false)
    }

    private fun showMoreMenu(member: Pair<SessionMember, User>) {
        val menus = mutableListOf<String>()
        val removeText = ContextCompat.getString(this, R.string.remove_out)
        val setAdminText = ContextCompat.getString(this, R.string.set_as_admin)
        val cancelAdminText = ContextCompat.getString(this, R.string.cancel_admin)
        val myRole = myRole()
        if (myRole == SessionRole.Owner.value) {
            if (member.first.role == SessionRole.Admin.value || member.first.role == SessionRole.SuperAdmin.value) {
                menus.add(cancelAdminText)
            } else if (member.first.role == SessionRole.Member.value) {
                menus.add(setAdminText)
            }
            menus.add(removeText)
        } else if (myRole != SessionRole.Member.value) {
            menus.add(removeText)
        }

        if (menus.isNotEmpty()) {
            BottomMenuDialog.show(this, menus) {
                if (it == removeText) {
                    removeMember(member)
                } else if (it == setAdminText) {
                    updateMemberRole(member, SessionRole.Admin.value)
                } else if (it == cancelAdminText) {
                    updateMemberRole(member, SessionRole.Member.value)
                }
            }
        }
    }

    private fun loadSessionMembers(group: GroupVo): Flowable<List<Pair<SessionMember, User>>> {
        return Flowable.create<List<SessionMember>>({
            val members =
                IMCoreManager.getImDataBase().sessionMemberDao().findBySessionIdSortByRole(
                    group.sessionId, 0, Int.MAX_VALUE
                ).toMutableList()
            members.sortWith(kotlin.Comparator { m1, m2 ->
                // ex: 返回-1 排在前面 返回1 排在后面
                if (m1.userId == IMCoreManager.uId && m2.role != SessionRole.Admin.value) {
                    return@Comparator -1
                }
                if (m2.userId == IMCoreManager.uId && m1.role != SessionRole.Admin.value) {
                    return@Comparator 1
                }
                // 角色小的排在后面
                if (m1.role < m2.role) {
                    return@Comparator 1
                }
                if (m1.cTime > m2.cTime) {
                    return@Comparator 1
                }
                return@Comparator -1
            })
            it.onNext(members)
            it.onComplete()
        }, BackpressureStrategy.LATEST).flatMap { members ->
            val ids = mutableSetOf<Long>()
            for (m in members) {
                ids.add(m.userId)
            }
            return@flatMap IMCoreManager.userModule.queryUsers(ids).flatMap { userMap ->
                val sessionMembers = mutableListOf<Pair<SessionMember, User>>()
                for (m in members) {
                    if (userMap[m.userId] != null) {
                        sessionMembers.add(Pair(m, userMap[m.userId]!!))
                    }
                }
                Flowable.just(sessionMembers)
            }
        }.flatMap { sessionMembers ->
            if (TextUtils.isEmpty(keywords)) {
                Flowable.just(sessionMembers)
            } else {
                val filterMembers = mutableListOf<Pair<SessionMember, User>>()
                sessionMembers.forEach {
                    val displayId = it.second.displayId
                    val nickname = it.second.nickname
                    val noteName = it.first.noteName ?: ""
                    if (displayId.contains(keywords) || nickname.contains(keywords) || noteName.contains(
                            keywords
                        )
                    ) {
                        filterMembers.add(it)
                    }
                }
                Flowable.just(filterMembers)
            }
        }
    }

    private fun loadData(isRefresh: Boolean, isLoadMore: Boolean) {
        val group = group() ?: return
        val subscriber = object : BaseSubscriber<List<Pair<SessionMember, User>>>() {
            override fun onNext(t: List<Pair<SessionMember, User>>?) {
                t?.let {
                    if (!isLoadMore) {
                        allMembers.clear()
                    }
                    allMembers.addAll(it)
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

        this.loadSessionMembers(group)
            .compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
        addDispose(subscriber)

    }

    private fun loadFailed(t: Throwable, isRefresh: Boolean, isLoadMore: Boolean) {
        if (isRefresh) {
            binding.rcvMembers.finishRefresh(false)
        }
        if (isLoadMore) {
            binding.rcvMembers.finishLoadMore(false)
        }
        showError(t)
    }

    private fun loadSuccess(
        members: List<Pair<SessionMember, User>>, isRefresh: Boolean, isLoadMore: Boolean,
    ) {
        val noMoreData = members.size < Int.MAX_VALUE
        if (isLoadMore) {
            binding.rcvMembers.finishLoadMore(success = true, noMoreData)
        } else if (isRefresh) {
            binding.rcvMembers.finishRefresh(true, noMoreData)
        } else if (noMoreData) {
            binding.rcvMembers.finishWithNoMoreData()
        }

        val adapter = binding.rcvMembers.getAdapter() as? GroupMemberAdapter? ?: return
        if (isLoadMore) {
            adapter.addData(members)
        } else {
            adapter.setData(members)
        }
    }

    private fun removeMember(sessionMember: Pair<SessionMember, User>) {
        val group = group() ?: return
        showLoading()
        val req = KickoffGroupMemberReq(group.id, sessionMember.second.id)
        val subscriber = object : BaseSubscriber<Void>() {
            override fun onNext(t: Void?) {}
            override fun onError(t: Throwable?) {
                super.onError(t)
                dismissLoading()
                t?.let {
                    showError(it)
                }
            }

            override fun onComplete() {
                super.onComplete()
                dismissLoading()
                removeSuccess(sessionMember)
            }
        }
        DataRepository.groupApi.kickoffGroupMember(req)
            .compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
        addDispose(subscriber)
    }

    private fun removeSuccess(sessionMember: Pair<SessionMember, User>) {
        allMembers.removeAll(allMembers.filter { it.second.id == sessionMember.second.id })
        val adapter = binding.rcvMembers.getAdapter() as? GroupMemberAdapter ?: return
        adapter.removeData(sessionMember)

        val group = group() ?: return
        IMCoreManager.messageModule.syncSessionMembers(group.sessionId)
    }

    private fun updateMemberRole(sessionMember: Pair<SessionMember, User>, role: Int) {
        val group = group() ?: return
        sessionMember.first.role = role
        showLoading()
        val req = UpdateGroupMemberRoleReq(group.id, sessionMember.second.id, role)
        val subscriber = object : BaseSubscriber<Void>() {
            override fun onNext(t: Void?) {}
            override fun onError(t: Throwable?) {
                super.onError(t)
                dismissLoading()
                t?.let {
                    showError(it)
                }
            }

            override fun onComplete() {
                super.onComplete()
                dismissLoading()
                updateRoleSuccess(sessionMember, role)
            }
        }
        DataRepository.groupApi.updateGroupMemberRole(req)
            .concatWith(Flowable.create({
                IMCoreManager.getImDataBase().sessionMemberDao().insertOrReplace(
                    listOf(sessionMember.first)
                )
                it.onComplete()
            }, BackpressureStrategy.LATEST))
            .compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
        addDispose(subscriber)
    }

    private fun updateRoleSuccess(sessionMember: Pair<SessionMember, User>, role: Int) {
        val adapter = binding.rcvMembers.getAdapter() as? GroupMemberAdapter ?: return
        adapter.updateData(sessionMember)
    }

}