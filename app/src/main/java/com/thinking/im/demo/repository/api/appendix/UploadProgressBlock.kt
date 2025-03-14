package com.thinking.im.demo.repository.api.appendix

interface UploadProgressBlock {
    fun callback(progress: Int, url: String?, err: Exception?)
}