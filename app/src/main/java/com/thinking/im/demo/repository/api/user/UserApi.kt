package com.thinking.im.demo.repository.api.user

import com.thinking.im.demo.repository.api.user.vo.QueryUsersRes
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

interface UserApi {

    @GET("/user/query")
    fun queryUsers(@Query("ids") ids: String): Flowable<QueryUsersRes>
}