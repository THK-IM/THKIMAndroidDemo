package com.thinking.im.demo.ui.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.thinking.im.demo.databinding.FragmentMineBinding
import com.thinking.im.demo.repository.DataRepository
import com.thinking.im.demo.repository.api.contact.vo.ContactSessionCreateVo
import com.thinking.im.demo.ui.base.BaseFragment
import com.thinking.im.demo.ui.message.group.CreateGroupActivity
import com.thk.im.android.core.IMCoreManager
import com.thk.im.android.core.api.vo.SessionVo
import com.thk.im.android.core.base.BaseSubscriber
import com.thk.im.android.core.base.RxTransform
import com.thk.im.android.core.base.extension.setShape
import com.thk.im.android.ui.manager.IMUIManager

class MineFragment : BaseFragment() {

    private lateinit var binding: FragmentMineBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMineBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etId.setShape(
            Color.WHITE,
            floatArrayOf(20f, 20f, 20f, 20f),
            false
        )

        binding.btChat.setShape(
            Color.WHITE,
            floatArrayOf(20f, 20f, 20f, 20f),
            false
        )

        binding.btGroup.setShape(
            Color.WHITE,
            floatArrayOf(20f, 20f, 20f, 20f),
            false
        )

        binding.btChat.setOnClickListener {
            val userId =
                binding.etId.text.trim().toString().toLongOrNull() ?: return@setOnClickListener
            chatWith(userId)
        }

        binding.btGroup.setOnClickListener {
            createGroup()
        }

    }

    private fun chatWith(id: Long) {
        val subscriber = object : BaseSubscriber<SessionVo>() {
            override fun onNext(t: SessionVo?) {
                t?.let {
                    IMUIManager.pageRouter?.openSession(requireActivity(), it.toSession())
                }
            }

            override fun onError(t: Throwable?) {
                super.onError(t)
                t?.let {
                    showError(it)
                }
            }
        }

        val body = ContactSessionCreateVo(IMCoreManager.uId, id)
        DataRepository.contactApi.createContactSession(body)
            .compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
        addDispose(subscriber)
    }

    private fun createGroup() {
        val intent = Intent(requireActivity(), CreateGroupActivity::class.java)
        startActivity(intent)
    }

}