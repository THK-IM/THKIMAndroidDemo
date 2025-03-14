package com.thinking.im.demo.ui.message.group

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.thinking.im.demo.R
import com.thinking.im.demo.databinding.FragmentCreateGroupBinding
import com.thinking.im.demo.repository.DataRepository
import com.thinking.im.demo.repository.api.group.vo.CreateGroupRes
import com.thinking.im.demo.repository.api.group.vo.CreateGroupVo
import com.thinking.im.demo.ui.base.BaseFragment
import com.thk.im.android.core.IMCoreManager
import com.thk.im.android.core.base.BaseSubscriber
import com.thk.im.android.core.base.RxTransform
import com.thk.im.android.core.base.extension.setShape
import com.thk.im.android.core.db.entity.Session
import com.thk.im.android.core.event.XEventBus

class CreateGroupFragment : BaseFragment() {

    private lateinit var binding: FragmentCreateGroupBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCreateGroupBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.lyPostContainer.setShape(Color.WHITE, floatArrayOf(10f, 10f, 10f, 10f))
        binding.tvOk.setShape(
            ContextCompat.getColor(requireContext(), R.color.primary),
            floatArrayOf(21f, 21f, 21f, 21f),
            false
        )
        binding.tvOk.alpha = 0.5f
        binding.tvOk.isClickable = false
        binding.tvOk.setOnClickListener {
            submit()
        }

        binding.metGroupName.setUI(
            hintText = ContextCompat.getString(requireContext(), R.string.input_group_name),
            hintTextColor = Color.parseColor("#BBBBBB"),
            textColor = Color.parseColor("#222222"),
            textStyle = Typeface.NORMAL,
            maxCount = 15,
            textSize = 15,
        )
    }


    private fun submit() {
        val groupName = binding.metGroupName.getContent()
        if (groupName.isEmpty()) {
            showToast(R.string.please_input_group_name)
            return
        }

        showLoading()
        val subscriber = object : BaseSubscriber<CreateGroupRes>() {
            override fun onNext(t: CreateGroupRes?) {
                t?.let {
                    querySession(it.group.sessionId)
                }
            }

            override fun onError(t: Throwable?) {
                super.onError(t)
                dismissLoading()
                t?.let {
                    showError(it)
                }
            }
        }

        val req = CreateGroupVo(
            groupName, "", "",
        )

        DataRepository.groupApi.createGroup(req)
            .compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
        addDispose(subscriber)
    }

    private fun querySession(sessionId: Long) {
        val subscriber = object : BaseSubscriber<Session>() {
            override fun onNext(t: Session?) {
                dismissLoading()
                showToast(R.string.group_create_success)
            }

            override fun onError(t: Throwable?) {
                super.onError(t)
                dismissLoading()
                t?.let {
                    showError(it)
                }
            }
        }
        IMCoreManager.messageModule.getSession(sessionId)
            .compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
        addDispose(subscriber)
    }
}