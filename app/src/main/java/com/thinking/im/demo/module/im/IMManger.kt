package com.thinking.im.demo.module.im

import android.app.Application
import com.thinking.im.demo.module.im.provider.session.GroupSessionProvider
import com.thinking.im.demo.module.im.provider.session.SingleSessionProvider
import com.thinking.im.demo.module.im.provider.session.SuperGroupSessionProvider
import com.thinking.im.demo.repository.Host
import com.thk.im.android.core.IMCoreManager
import com.thk.im.android.core.api.internal.DefaultIMApi
import com.thk.im.android.core.fileloader.internal.DefaultFileLoadModule
import com.thk.im.android.core.signal.inernal.DefaultSignalModule
import com.thk.im.android.media.Provider
import com.thk.im.android.ui.manager.IMUIManager
import com.thk.im.preview.Previewer
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

object IMManger {

    lateinit var app: Application

    fun initIMConfig(app: Application, debug: Boolean) {
        this.app = app
        IMCoreManager.init(app, debug)
        IMCoreManager.crypto = IMCipher()
        IMCoreManager.commonModule = IMCommonModule(app)
        IMCoreManager.userModule = IMUserModule()
        IMCoreManager.contactModule = IMContactModule()
        IMCoreManager.groupModule = IMGroupModule()
        IMCoreManager.messageModule = IMMessageModule()

        IMUIManager.init(app)
        IMUIManager.pageRouter = IMExternalPageRouter()
        IMUIManager.uiResourceProvider = IMResourceProvider(app)
        IMUIManager.registerSessionIVProvider(SingleSessionProvider())
        IMUIManager.registerSessionIVProvider(GroupSessionProvider())
        IMUIManager.registerSessionIVProvider(SuperGroupSessionProvider())

        val mediaProvider = Provider(IMManger.app)
        val mediaPreviewer = Previewer(IMManger.app)
        IMUIManager.mediaProvider = mediaProvider
        IMUIManager.mediaPreviewer = mediaPreviewer
    }

    fun initIMUser(token: String, uId: Long): Flowable<Boolean> {
        return Flowable.create({
            if (IMCoreManager.uId != uId) {
                if (IMCoreManager.uId != 0L) {
                    IMCoreManager.shutdown()
                }
                IMUIManager.mediaPreviewer?.setTokenForEndpoint(Host.endpointFor("msg"), token)
                val signalModule =
                    DefaultSignalModule(app, Host.endpointFor("websocket"), token)
                val fileLoaderModule =
                    DefaultFileLoadModule(app, Host.endpointFor("msg"), token)
                val imApi = DefaultIMApi(token, Host.endpointFor("msg"))
                IMCoreManager.signalModule = signalModule
                IMCoreManager.fileLoadModule = fileLoaderModule
                IMCoreManager.imApi = imApi
                IMCoreManager.initUser(uId)
            }
            it.onNext(true)
            it.onComplete()
        }, BackpressureStrategy.LATEST)
    }
}