package com.thinking.im.demo.ui.message.contact

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.thinking.im.demo.R
import com.thinking.im.demo.databinding.ActivityContactSettingBinding
import com.thinking.im.demo.repository.ContactRelation
import com.thinking.im.demo.repository.DataRepository
import com.thinking.im.demo.repository.UIEvent
import com.thinking.im.demo.repository.api.contact.vo.SetNotNameReq
import com.thinking.im.demo.repository.api.user.vo.BasisUserVo
import com.thinking.im.demo.ui.base.BaseRelationActivity
import com.thinking.im.demo.ui.text.TextEditActivity
import com.thk.im.android.core.IMCoreManager
import com.thk.im.android.core.SessionStatus
import com.thk.im.android.core.base.BaseSubscriber
import com.thk.im.android.core.base.RxTransform
import com.thk.im.android.core.base.extension.setShape
import com.thk.im.android.core.db.entity.Contact
import com.thk.im.android.core.db.entity.Session
import com.thk.im.android.core.db.entity.User
import com.thk.im.android.core.event.XEventBus
import io.reactivex.Flowable

class ContactChatSettingActivity : BaseRelationActivity() {

    companion object {
        fun start(ctx: Context, user: User, session: Session) {
            val intent = Intent(ctx, ContactChatSettingActivity::class.java)
            intent.putExtra("session", session)
            intent.putExtra("user", user)
            ctx.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityContactSettingBinding
    private lateinit var launcher: ActivityResultLauncher<Intent>

    private var contact: Contact? = null

    private fun user(): User? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("user", User::class.java)
        } else {
            intent.getParcelableExtra("user")
        }
    }

    private fun session(): Session? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("session", Session::class.java)
        } else {
            intent.getParcelableExtra("session")
        }
    }

    override fun getToolbar(): Toolbar {
        return binding.tbTop.toolbar
    }

    override fun toolbarTitle(): String {
        return ContextCompat.getString(this, R.string.chat_setting)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityContactSettingBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        XEventBus.observe(this, UIEvent.RelationUpdate.value, observer = Observer<Contact> {
            val old = contact ?: return@Observer
            if (old.id == it.id) {
                this@ContactChatSettingActivity.contact = it
                updateContactView()
            }
        })
        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: androidx.activity.result.ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val text = result.data?.getStringExtra("text") ?: return@registerForActivityResult
                updateContactNoteName(text)
            }
        }
        setContentView(binding.root)
        initView()
        initData()
    }

    private fun initView() {
        binding.lyUserContainer.setShape(
            Color.WHITE, floatArrayOf(10f, 10f, 10f, 10f), false
        )
        binding.lyUserContainer.setOnClickListener {
            previewUser()
        }

        binding.lyRemark.setShape(
            Color.WHITE, floatArrayOf(10f, 10f, 0f, 0f), false
        )
        binding.lyTop.setShape(
            Color.WHITE, floatArrayOf(0f, 0f, 0f, 0f), false
        )
        binding.lySilence.setShape(
            Color.WHITE, floatArrayOf(0f, 0f, 10f, 10f), false
        )

        binding.lyRemark.setTextBold(titleBold = false, contentBold = false)
        binding.lyRemark.setColor(
            ContextCompat.getColor(this, R.color.primary_text_color),
            ContextCompat.getColor(this, R.color.third_text_color),
            ContextCompat.getColor(this, R.color.primary_text_color)
        )
        binding.lyRemark.setTextSize(14f, 14f, 14f)
        binding.lyRemark.setOnClickListener {
            onRemarkClick()
        }

        binding.lyTop.setTextSize(14f)
        binding.lyTop.setColor(ContextCompat.getColor(this, R.color.primary_text_color))
        binding.lyTop.setTextBold(false)

        binding.lySilence.setTextSize(14f)
        binding.lySilence.setColor(ContextCompat.getColor(this, R.color.primary_text_color))
        binding.lySilence.setTextBold(false)

        binding.lyTop.setItemClickListener {
            val session = session() ?: return@setItemClickListener
            if (binding.lyTop.isSwitchOn()) {
                session.topTimestamp = 0
            } else {
                session.topTimestamp = IMCoreManager.severTime
            }
            updateSession(session)
        }
        binding.lySilence.setItemClickListener {
            val session = session() ?: return@setItemClickListener
            session.status = session.status.xor(SessionStatus.Silence.value)
            updateSession(session)
        }

        binding.tvFollow.setShape(
            Color.WHITE, floatArrayOf(10f, 10f, 0f, 0f), false
        )

        binding.tvBlack.setShape(
            Color.WHITE, floatArrayOf(0f, 0f, 10f, 10f), false
        )

        binding.tvBlack.setOnClickListener {
            onBlackClick()
        }

        binding.tvFollow.setOnClickListener {
            onFollowClick()
        }

        val reportText = ContextCompat.getString(this, R.string.report_this_user)
        val spanReportText = SpannableString(reportText)
        val clickSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#888888")
                ds.isUnderlineText = true
            }

            override fun onClick(widget: View) {
                onReportClick()
            }
        }

        spanReportText.setSpan(
            clickSpan,
            0,
            reportText.length,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        binding.tvReport.text = spanReportText
        binding.tvReport.movementMethod = LinkMovementMethod.getInstance()


        val user = user() ?: return
        if (user.id == IMCoreManager.uId) {
            binding.lyOperate.visibility = View.GONE
        }
    }

    private fun updateUserView() {
        val user = user() ?: return
        binding.lyUser.setUserInfo(BasisUserVo.fromUser(user))
        binding.lyUser.hideLevel(true)
        binding.lyUser.hideId(false)
    }

    private fun updateContactView() {
        val contact = contact ?: return
        var remark = contact.noteName
        if (remark.isNullOrEmpty()) {
            remark = ContextCompat.getString(this, R.string.no_remark)
        }
        binding.lyRemark.setText(
            ContextCompat.getString(this, R.string.remark_of_him),
            remark
        )
        if (contact.relation.and(ContactRelation.Black.value) != 0) {
            binding.tvBlack.text = ContextCompat.getString(this, R.string.remove_out_blacklist)
        } else {
            binding.tvBlack.text = ContextCompat.getString(this, R.string.pull_to_black)
        }

        if (contact.relation.and(ContactRelation.Fellow.value) != 0) {
            if (contact.relation.and(ContactRelation.BeFellow.value) != 0) {
                binding.tvFollow.text = ContextCompat.getString(this, R.string.follow_each_other)
            } else {
                binding.tvFollow.text = ContextCompat.getString(this, R.string.had_followed)
            }
        } else {
            binding.tvFollow.text = ContextCompat.getString(this, R.string.to_follow)
        }
    }

    private fun updateSessionView() {
        val session = session() ?: return
        binding.lyTop.setContent(
            ContextCompat.getString(this, R.string.session_set_top),
            session.topTimestamp > 0
        )
        binding.lySilence.setContent(
            ContextCompat.getString(this, R.string.session_silence_set),
            session.status.and(SessionStatus.Silence.value) > 0,
        )
    }

    private fun initData() {
        updateUserView()
        updateSessionView()
        val user = user() ?: return
        val subscriber = object : BaseSubscriber<Contact>() {
            override fun onNext(t: Contact?) {
                dismissLoading()
                t?.let {
                    this@ContactChatSettingActivity.contact = it
                    updateContactView()
                }
            }

            override fun onError(t: Throwable?) {
                dismissLoading()
                t?.let {
                    showError(it)
                }
            }
        }
        IMCoreManager.contactModule.queryContactByUserId(user.id)
            .compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
        addDispose(subscriber)
        updateUser()
    }

    private fun updateUser() {
        val user = user() ?: return
        val subscriber = object : BaseSubscriber<User>() {
            override fun onNext(t: User?) {
                t?.let {
                    intent.putExtra("user", it)
                    updateUserView()
                }
            }

            override fun onError(t: Throwable?) {
                t?.let {
                    showError(it)
                }
            }
        }
        IMCoreManager.userModule.queryServerUser(user.id)
            .compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
        addDispose(subscriber)

    }

    private fun onFollowClick() {
        val contact = contact ?: return
        if (contact.relation.and(ContactRelation.Fellow.value) != 0) {
            follow(contact, false)
        } else {
            follow(contact, true)
        }
    }

    private fun onBlackClick() {
        val contact = contact ?: return
        if (contact.relation.and(ContactRelation.Black.value) != 0) {
            black(contact, false)
        } else {
            black(contact, true)
        }
    }

    private fun onRemarkClick() {
        val noteName = contact?.noteName ?: ""
        TextEditActivity.start(
            this, ContextCompat.getString(this, R.string.remark_of_him),
            noteName, 12, launcher
        )
    }

    private fun updateContactNoteName(name: String) {
        val contact = contact ?: return
        showLoading()
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
                updateContactNoteNameSuccess(name)
            }
        }
        val req = SetNotNameReq(IMCoreManager.uId, contact.id, name)
        DataRepository.contactApi.setNotName(req)
            .compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
        addDispose(subscriber)
    }

    private fun updateContactNoteNameSuccess(name: String) {
        val contact = contact ?: return
        contact.noteName = name
        val subscriber = object : BaseSubscriber<Contact>() {
            override fun onNext(t: Contact?) {
                t?.let {
                    this@ContactChatSettingActivity.contact = it
                    updateContactView()
                }
            }

            override fun onError(t: Throwable?) {
                t?.let {
                    showError(it)
                }
            }
        }
        Flowable.just(contact)
            .flatMap {
                IMCoreManager.getImDataBase().contactDao().insertOrReplace(listOf(contact))
                XEventBus.post(UIEvent.RelationUpdate.value, contact)
                Flowable.just(contact)
            }.compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
        addDispose(subscriber)
    }

    private fun previewUser() {
        val user = user() ?: return
        showToast("TODO")
    }

    private fun updateSession(session: Session) {
        showLoading()
        val subscriber = object : BaseSubscriber<Void>() {
            override fun onNext(t: Void?) {}

            override fun onError(t: Throwable?) {
                dismissLoading()
                t?.let {
                    showError(it)
                }
            }

            override fun onComplete() {
                super.onComplete()
                dismissLoading()
                updateSessionView()
            }
        }
        IMCoreManager.messageModule.updateSession(session, true)
            .compose(RxTransform.flowableToMain()).subscribe(subscriber)
        addDispose(subscriber)
    }

    private fun onReportClick() {
        showToast("TODO")
    }
}