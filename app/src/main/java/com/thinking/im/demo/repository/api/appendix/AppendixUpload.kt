package com.thinking.im.demo.repository.api.appendix

import com.thinking.im.demo.repository.DataRepository
import com.thinking.im.demo.repository.api.ApiFactory
import com.thinking.im.demo.repository.api.appendix.vo.PostAppendixParamsReq
import com.thinking.im.demo.repository.api.appendix.vo.PostAppendixParamsRes
import com.thk.im.android.core.IMCoreManager
import com.thk.im.android.core.base.BaseSubscriber
import com.thk.im.android.core.base.RxTransform
import com.thk.im.android.core.exception.CodeMsgException
import com.thk.im.android.core.fileloader.internal.FileProgressRequestBody
import io.reactivex.disposables.CompositeDisposable
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException

class AppendixUpload(
    private val business: String,
    private val path: String,
    private val block: UploadProgressBlock,
) {

    private val disposes = CompositeDisposable()
    private var running = true
    private var uploadCall: Call? = null


    fun start() {
        this.notify(0)
        val pathPair = IMCoreManager.storageModule.getPathsFromFullPath(path)
        val req = PostAppendixParamsReq(business, pathPair.second)

        val subscriber = object : BaseSubscriber<PostAppendixParamsRes>() {
            override fun onNext(t: PostAppendixParamsRes?) {
                t?.let {
                    startUpload(it)
                }
            }

            override fun onError(t: Throwable?) {
                super.onError(t)
                t?.let {
                    notify(0, null, Exception(it))
                }
            }

        }
        DataRepository.appendixApi.postAppendixParams(req).compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
        disposes.add(subscriber)
    }

    private fun startUpload(res: PostAppendixParamsRes) {
        if (!running) {
            this.notify(0)
            return
        }
        val requestBodyBuilder = MultipartBody.Builder()
        requestBodyBuilder.setType(MultipartBody.FORM)
        for ((k, v) in res.params) {
            requestBodyBuilder.addFormDataPart(k, v)
        }
        val file = File(path)
        val fileBody: RequestBody =
            FileProgressRequestBody(file, object : FileProgressRequestBody.ProgressListener {
                override fun transferred(size: Long, progress: Int) {
                    notify(progress)
                }
            })
        requestBodyBuilder.addFormDataPart("file", null, fileBody)
        val request = Request.Builder().method(res.method.uppercase(), requestBodyBuilder.build())
            .url(res.url).build()
        uploadCall = ApiFactory.uploadHttpClient.newCall(request)
        uploadCall?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                notify(0, null, e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    notify(100, res.uploadKey, null)
                } else {
                    val msg = response.body?.string() ?: "unknown"
                    notify(0, null, CodeMsgException(response.code, msg))
                }
            }
        })
    }

    fun cancel() {
        running = false
        disposes.clear()
        uploadCall?.let {
            if (it.isExecuted() && !it.isCanceled()) {
                it.cancel()
            }
        }
    }

    private fun notify(progress: Int, url: String? = null, err: Exception? = null) {
        block.callback(progress, url, err)
    }
}