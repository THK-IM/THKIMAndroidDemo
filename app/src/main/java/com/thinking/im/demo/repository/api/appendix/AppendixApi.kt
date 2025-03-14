package com.thinking.im.demo.repository.api.appendix

import com.thinking.im.demo.repository.api.appendix.vo.PostAppendixParamsReq
import com.thinking.im.demo.repository.api.appendix.vo.PostAppendixParamsRes
import io.reactivex.Flowable
import retrofit2.http.Body
import retrofit2.http.POST

interface AppendixApi {

    @POST("/appendix")
    fun postAppendixParams(@Body req: PostAppendixParamsReq): Flowable<PostAppendixParamsRes>
}