package com.thinking.im.demo.ui.base

import androidx.core.content.ContextCompat
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.thinking.im.demo.R
import com.thinking.im.demo.repository.api.appendix.AppendixUpload
import com.thinking.im.demo.repository.api.appendix.UploadProgressBlock
import com.thinking.im.demo.ui.component.dialog.BottomMenuDialog
import com.thk.im.android.core.IMCoreManager
import com.thk.im.android.core.IMFileFormat
import com.thk.im.android.core.base.BaseSubscriber
import com.thk.im.android.core.base.RxTransform
import com.thk.im.android.core.base.utils.CompressUtils
import com.thk.im.android.ui.manager.IMFile
import com.thk.im.android.ui.manager.IMUIManager
import com.thk.im.android.ui.protocol.IMContentResult


abstract class BaseMediaUploadActivity : BaseActivity() {

    private var appendixUpload: AppendixUpload? = null

    fun showChooseMediaMenu(title: String? = null) {
        val albumText = ContextCompat.getString(this, R.string.album)
        val cameraText = ContextCompat.getString(this, R.string.camera)

        BottomMenuDialog.show(this, listOf(albumText, cameraText)) { text ->
            if (text == albumText) {
                openAlbum()
            } else {
                openCamera()
            }
        }
    }

    private fun openAlbum() {
        XXPermissions.with(this)
            .permission(Permission.READ_MEDIA_VIDEO, Permission.READ_MEDIA_IMAGES)
            .request { _, all ->
                if (all) {
                    albumMedia()
                } else {
                    showToast(R.string.album_permission_deny)
                }
            }
    }

    private fun albumMedia() {
        IMUIManager.mediaProvider?.pick(this, listOf(IMFileFormat.Image), object : IMContentResult {
            override fun onResult(result: List<IMFile>) {
                onChooseResult(result)
            }

            override fun onCancel() {
                showToast(R.string.user_cancel)
            }

        })
    }

    private fun openCamera() {
        XXPermissions.with(this).permission(Permission.CAMERA).request { _, all ->
            if (all) {
                cameraMedia()
            } else {
                showToast(R.string.camera_permission_deny)
            }
        }
    }

    private fun cameraMedia() {
        IMUIManager.mediaProvider?.openCamera(this,
            listOf(IMFileFormat.Image),
            object : IMContentResult {
                override fun onResult(result: List<IMFile>) {
                    onChooseResult(result)
                }

                override fun onCancel() {
                    showToast(R.string.user_cancel)
                }

            })
    }

    open fun onChooseResult(result: List<IMFile>) {
        if (result.isNotEmpty()) {
            val compressSize = compressSize()
            compressSize?.let {
                val ext = IMCoreManager.storageModule.getFileExt(result.first().path)
                val tmpPath =
                    cacheDir.path + "/upload_" + System.currentTimeMillis() / 1000 + "." + ext.second
                val subscriber = object : BaseSubscriber<String>() {
                    override fun onNext(t: String?) {
                        t?.let {
                            startUpload(it, appendixBusiness())
                        }
                    }

                    override fun onError(t: Throwable?) {
                        super.onError(t)
                        t?.let {
                            showError(it)
                        }
                    }
                }
                CompressUtils.compress(result.first().path, compressSize, tmpPath)
                    .compose(RxTransform.flowableToMain())
                    .subscribe(subscriber)
                addDispose(subscriber)
                return
            }
            startUpload(result.first().path, appendixBusiness())
        }
    }

    abstract fun appendixBusiness(): String

    abstract fun compressSize(): Int?

    fun startUpload(path: String, business: String) {
        appendixUpload = AppendixUpload(business, path, object : UploadProgressBlock {
            override fun callback(progress: Int, url: String?, err: Exception?) {
                runOnUiThread {
                    if (url != null) {
                        dismissLoading()
                        uploadSuccess(url)
                    } else if (err != null) {
                        dismissLoading()
                        showError(err)
                    } else {
                        val loadingText = ContextCompat.getString(
                            this@BaseMediaUploadActivity,
                            R.string.uploading
                        ) + progress + "%"
                        showLoading(text = loadingText)
                    }
                }
            }
        })
        appendixUpload?.start()
    }

    open fun uploadSuccess(url: String) {

    }

    override fun onDestroy() {
        super.onDestroy()
        appendixUpload?.cancel()
    }


}