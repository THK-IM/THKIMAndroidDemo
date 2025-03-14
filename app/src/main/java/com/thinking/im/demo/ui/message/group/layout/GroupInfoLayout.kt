package com.thinking.im.demo.ui.message.group.layout

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.material.shape.ShapeAppearanceModel
import com.thinking.im.demo.R
import com.thinking.im.demo.databinding.LayoutGroupInfoBinding
import com.thinking.im.demo.repository.DataRepository
import com.thinking.im.demo.repository.UIEvent
import com.thinking.im.demo.repository.api.group.vo.GroupVo
import com.thinking.im.demo.repository.api.group.vo.UpdateGroupReq
import com.thinking.im.demo.ui.base.BaseActivity
import com.thinking.im.demo.ui.text.TextEditActivity
import com.thk.im.android.core.IMCoreManager
import com.thk.im.android.core.IMEvent
import com.thk.im.android.core.SessionRole
import com.thk.im.android.core.base.BaseSubscriber
import com.thk.im.android.core.base.IMImageLoader
import com.thk.im.android.core.base.RxTransform
import com.thk.im.android.core.base.extension.setGradientShape
import com.thk.im.android.core.base.utils.AppUtils
import com.thk.im.android.core.db.entity.Session
import com.thk.im.android.core.event.XEventBus
import io.reactivex.disposables.CompositeDisposable

class GroupInfoLayout : LinearLayout {

    private val binding: LayoutGroupInfoBinding
    private var session: Session? = null
    private var groupVo: GroupVo? = null
    private val compositeDisposable = CompositeDisposable()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private var updateType: String? = null
    private val launcher: ActivityResultLauncher<Intent>

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_group_info, this, true)
        binding = LayoutGroupInfoBinding.bind(view)
        launcher = (context as FragmentActivity).registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: androidx.activity.result.ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val text = result.data?.getStringExtra("text") ?: return@registerForActivityResult
                when (updateType) {
                    "updateGroupName" -> {
                        updateGroupName(text)
                    }

                    "updateNotName" -> {
                        updateMemberNoteName(text)
                    }

                    "updateGroupIntroduce" -> {
                        updateGroupIntroduce(text)
                    }
                }
            }
        }

        binding.ivAvatar.shapeAppearanceModel =
            ShapeAppearanceModel.builder().setAllCornerSizes(AppUtils.dp2px(8f).toFloat()).build()
        binding.tvUpdateAvatar.setGradientShape(
            Color.parseColor("#40000000"), Color.BLACK, floatArrayOf(0f, 0f, 8f, 8f), false
        )

        binding.lyIntroduce.setTextBold(titleBold = false, contentBold = false)
        binding.lyIntroduce.setColor(
            ContextCompat.getColor(context, R.color.primary_text_color),
            ContextCompat.getColor(context, R.color.third_text_color),
            ContextCompat.getColor(context, R.color.primary_text_color)
        )
        binding.lyIntroduce.setTextSize(14f, 14f, 14f)
        binding.lyIntroduce.show(true, showRequired = false)

        binding.lyRemark.setTextBold(titleBold = false, contentBold = false)
        binding.lyRemark.setColor(
            ContextCompat.getColor(context, R.color.primary_text_color),
            ContextCompat.getColor(context, R.color.third_text_color),
            ContextCompat.getColor(context, R.color.primary_text_color)
        )
        binding.lyRemark.setTextSize(14f, 14f, 14f)


        binding.lyIntroduce.setOnClickListener {
            onIntroduceLayoutClick()
        }
        binding.tvIntroduce.setOnClickListener {
            onIntroduceLayoutClick()
        }


        binding.lyRemark.setOnClickListener {
            onRemarkClick()
        }

        binding.lyHeader.setOnClickListener {
            if (myRole() > SessionRole.Member.value) {
                onGroupNameClick()
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        compositeDisposable.clear()
    }


    private fun myRole(): Int {
        val session = session ?: return SessionRole.Member.value
        return session.role
    }

    fun bind(groupVo: GroupVo, session: Session) {
        this.groupVo = groupVo
        this.session = session
        render()
    }

    private fun render() {
        val groupVo = groupVo ?: return
        val session = session ?: return

        IMImageLoader.displayImageUrl(binding.ivAvatar, groupVo.avatar)
        binding.tvName.text = groupVo.name
        binding.tvId.text = "ID: ${groupVo.displayId}"
        if (myRole() == SessionRole.Member.value) {
            binding.tvUpdateAvatar.visibility = View.GONE
            binding.ivArrow.visibility = View.GONE
        } else {
            binding.tvUpdateAvatar.visibility = View.VISIBLE
            binding.ivArrow.visibility = View.VISIBLE
        }


        binding.lyIntroduce.setText(
            ContextCompat.getString(context, R.string.group_introduce),
            "",
        )
        binding.tvIntroduce.text = groupVo.introduce

        binding.lyRemark.setText(
            ContextCompat.getString(context, R.string.mine_group_nick), session.noteName
        )
    }

    private fun onGroupNameClick() {
        val groupVo = groupVo ?: return
        updateType = "updateGroupName"
        TextEditActivity.start(
            context,
            ContextCompat.getString(context, R.string.group_chat_name),
            groupVo.name,
            12,
            launcher
        )
    }


    private fun updateGroupName(text: String) {
        val groupVo = groupVo ?: return
        (context as? BaseActivity)?.showLoading()
        val subscriber = object : BaseSubscriber<Void>() {
            override fun onNext(t: Void?) {
            }

            override fun onError(t: Throwable?) {
                super.onError(t)
                (context as? BaseActivity)?.dismissLoading()
                t?.let {
                    (context as? BaseActivity)?.showError(it)
                }
            }

            override fun onComplete() {
                super.onComplete()
                (context as? BaseActivity)?.dismissLoading()
                groupVo.name = text
                XEventBus.post(UIEvent.GroupUpdate.value, groupVo)
            }
        }
        val req = UpdateGroupReq(groupVo.id, text, null, null, null, null, null)
        DataRepository.groupApi.updateGroup(groupVo.id, req).compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
        compositeDisposable.addAll(subscriber)
    }

    private fun onIntroduceLayoutClick() {
        val groupVo = groupVo ?: return
        if (groupVo.ownerId == IMCoreManager.uId) {
            updateType = "updateGroupIntroduce"
            TextEditActivity.start(
                context,
                ContextCompat.getString(context, R.string.group_introduce),
                groupVo.introduce,
                300,
                launcher
            )
        }
    }

    private fun updateGroupIntroduce(text: String) {
        val groupVo = groupVo ?: return
        (context as? BaseActivity)?.showLoading()
        val subscriber = object : BaseSubscriber<Void>() {
            override fun onNext(t: Void?) {
            }

            override fun onError(t: Throwable?) {
                super.onError(t)
                (context as? BaseActivity)?.dismissLoading()
                t?.let {
                    (context as? BaseActivity)?.showError(it)
                }
            }

            override fun onComplete() {
                super.onComplete()
                (context as? BaseActivity)?.dismissLoading()
                groupVo.introduce = text
                binding.tvIntroduce.text = text
            }
        }
        val req = UpdateGroupReq(groupVo.id, null, text, null, null, null, null)
        DataRepository.groupApi.updateGroup(groupVo.id, req).compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
        compositeDisposable.addAll(subscriber)
    }


    private fun onRemarkClick() {
        val session = session ?: return
        val noteName = session.noteName ?: ""
        updateType = "updateNotName"
        TextEditActivity.start(
            context,
            ContextCompat.getString(context, R.string.mine_group_nick),
            noteName,
            12,
            launcher
        )
    }

    private fun updateMemberNoteName(name: String) {
        val session = session ?: return
        session.noteName = name
        updateSession(session)
    }

    private fun updateSession(session: Session) {
        (context as? BaseActivity)?.showLoading()
        val subscriber = object : BaseSubscriber<Void>() {
            override fun onNext(t: Void?) {}

            override fun onError(t: Throwable?) {
                (context as? BaseActivity)?.dismissLoading()
                t?.let {
                    (context as? BaseActivity)?.showError(it)
                }
            }

            override fun onComplete() {
                super.onComplete()
                (context as? BaseActivity)?.dismissLoading()
                XEventBus.post(IMEvent.SessionUpdate.value, session)
            }
        }
        IMCoreManager.messageModule.updateSession(session, true)
            .compose(RxTransform.flowableToMain()).subscribe(subscriber)
        compositeDisposable.addAll(subscriber)
    }

    fun uploadSuccess(url: String) {
        val groupVo = groupVo ?: return
        val req = UpdateGroupReq(
            groupVo.id, name = null, introduce = null, avatar = url, null, null, null
        )
        ((context as? BaseActivity))?.showLoading()
        val subscriber = object : BaseSubscriber<Void>() {
            override fun onNext(t: Void?) {
            }

            override fun onComplete() {
                super.onComplete()
                ((context as? BaseActivity))?.dismissLoading()
                groupVo.avatar = url
                XEventBus.post(UIEvent.GroupUpdate.value, groupVo)
            }

            override fun onError(t: Throwable?) {
                super.onError(t)
                ((context as? BaseActivity))?.dismissLoading()
                t?.let {
                    ((context as? BaseActivity))?.showError(it)
                }
            }
        }
        DataRepository.groupApi.updateGroup(groupVo.id, req).compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
        compositeDisposable.add(subscriber)
    }

}