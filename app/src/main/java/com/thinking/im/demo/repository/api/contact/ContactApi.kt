package com.thinking.im.demo.repository.api.contact

import com.thk.im.android.core.api.vo.ListVo
import com.thk.im.android.core.api.vo.SessionVo
import com.thinking.im.demo.repository.api.contact.vo.BlackVo
import com.thinking.im.demo.repository.api.contact.vo.ContactSessionCreateVo
import com.thinking.im.demo.repository.api.contact.vo.ContactSummary
import com.thinking.im.demo.repository.api.contact.vo.ContactVo
import com.thinking.im.demo.repository.api.contact.vo.FollowVo
import com.thinking.im.demo.repository.api.contact.vo.QueryContactRes
import com.thinking.im.demo.repository.api.contact.vo.SetNotNameReq
import io.reactivex.Flowable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ContactApi {

    /**
     * 拉黑
     */
    @POST("/contact/black")
    fun black(
        @Body body: BlackVo
    ): Flowable<Void>

    /**
     * 取消拉黑
     */
    @HTTP(method = "DELETE", path = "/contact/black", hasBody = true)
    fun cancelBlack(
        @Body body: BlackVo
    ): Flowable<Void>


    /**
     * 关注
     */
    @POST("/contact/follow")
    fun follow(
        @Body body: FollowVo
    ): Flowable<Void>

    /**
     * 取消关注
     */
    @HTTP(method = "DELETE", path = "/contact/follow", hasBody = true)
    fun cancelFollow(
        @Body body: FollowVo
    ): Flowable<Void>


    /**
     * 创建会话
     */
    @POST("/contact/session")
    fun createContactSession(
        @Body body: ContactSessionCreateVo
    ): Flowable<SessionVo>


    @GET("/contact/latest")
    fun queryLatestContactList(
        @Query("u_id") uId: Long,
        @Query("m_time") mTime: Long,
        @Query("count") count: Int,
        @Query("offset") offset: Int,
    ): Flowable<ListVo<ContactVo>>

    @GET("/contact")
    fun queryContactList(
        @Query("u_id") uId: Long,
        @Query("relation") relation: Int,
        @Query("count") count: Int,
        @Query("offset") offset: Int,
        @Query("keyword") keyword: String? = null
    ): Flowable<ListVo<ContactVo>>

    @GET("/contact/user/{id}")
    fun queryContactByUserId(@Path("id") id: Long): Flowable<QueryContactRes>

    @GET("/contact/user")
    fun queryContactByUserIds(
        @Query("contact_ids") contactIds: String,
        @Query("u_id") uId: Long
    ): Flowable<ListVo<ContactVo>>


    @GET("/contact/summary")
    fun queryContactSummary(): Flowable<ContactSummary>


    @POST("/contact/note_name")
    fun setNotName(@Body req: SetNotNameReq): Flowable<Void>

}