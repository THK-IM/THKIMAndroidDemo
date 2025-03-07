package com.thinking.im.demo.repository.api.group

import com.thinking.im.demo.repository.api.group.vo.CreateGroupRes
import com.thinking.im.demo.repository.api.group.vo.CreateGroupVo
import com.thinking.im.demo.repository.api.group.vo.DisbandGroupReq
import com.thinking.im.demo.repository.api.group.vo.KickoffGroupMemberReq
import com.thinking.im.demo.repository.api.group.vo.LeaveGroupReq
import com.thinking.im.demo.repository.api.group.vo.MuteGroupReq
import com.thinking.im.demo.repository.api.group.vo.PostJoinGroupApplyReq
import com.thinking.im.demo.repository.api.group.vo.JoinGroupReq
import com.thinking.im.demo.repository.api.group.vo.PostReviewGroupApplyReq
import com.thinking.im.demo.repository.api.group.vo.QueryGroupRes
import com.thinking.im.demo.repository.api.group.vo.QueryGroupsRes
import com.thinking.im.demo.repository.api.group.vo.QueryJoinGroupAppliesRes
import com.thinking.im.demo.repository.api.group.vo.QueryJoinGroupApplyRes
import com.thinking.im.demo.repository.api.group.vo.QueryJoinGroupsRes
import com.thinking.im.demo.repository.api.group.vo.ReviewApplyRes
import com.thinking.im.demo.repository.api.group.vo.UpdateGroupMemberRoleReq
import com.thinking.im.demo.repository.api.group.vo.UpdateGroupReq
import io.reactivex.Flowable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface GroupApi {

    @GET("/group/joins")
    fun queryJoinGroups(
        @Query("count") count: Int,
        @Query("offset") offset: Int,
        @Query("keyword") keyword: String?,
    ): Flowable<QueryJoinGroupsRes>

    @GET("/group/{id}")
    fun queryGroup(
        @Path("id") id: Long,
        @Query("need_online_count") needOnlineCount: Int? = null,
        @Query("need_apply_count") needApplyCount: Int? = null,
    ): Flowable<QueryGroupRes>

    /**
     * 创建群
     */
    @POST("/group")
    fun createGroup(
        @Body body: CreateGroupVo
    ): Flowable<CreateGroupRes>

    @PUT("/group/{id}")
    fun updateGroup(
        @Path("id") id: Long,
        @Body body: UpdateGroupReq
    ): Flowable<Void>

    @GET("/group/search")
    fun searchGroups(
        @Query("keywords") keywords: String,
        @Query("count") count: Int,
        @Query("offset") offset: Int
    ): Flowable<QueryGroupsRes>

    @GET("/group/recommend")
    fun queryRecommendGroups(
        @Query("count") count: Int,
        @Query("offset") offset: Int
    ): Flowable<QueryGroupsRes>


    @GET("/group")
    fun queryGroupsByIds(
        @Query("ids") ids: String
    ): Flowable<QueryGroupsRes>



    @GET("/group/apply")
    fun queryJoinGroupApply(
        @Query("apply_id") applyId: Long,
    ): Flowable<QueryJoinGroupApplyRes>

    @GET("/group/applies")
    fun queryJoinGroupApplies(
        @Query("group_id") groupId: Long,
        @Query("count") count: Int,
        @Query("offset") offset: Int,
    ): Flowable<QueryJoinGroupAppliesRes>

    @POST("/group/join")
    fun joinGroup(@Body req: JoinGroupReq): Flowable<Void>

    @POST("/group/apply")
    fun applyJoinGroup(@Body req: PostJoinGroupApplyReq): Flowable<Void>

    @POST("/group/apply/review")
    fun reviewGroupApply(@Body req: PostReviewGroupApplyReq): Flowable<ReviewApplyRes>

    @POST("/group/leave")
    fun leaveGroup(@Body req: LeaveGroupReq): Flowable<Void>

    @HTTP(method = "DELETE", path = "/group/member", hasBody = true)
    fun kickoffGroupMember(@Body req: KickoffGroupMemberReq): Flowable<Void>

    @POST("/group/disband")
    fun disbandGroup(@Body req: DisbandGroupReq): Flowable<Void>

    @POST("/group/mute")
    fun muteGroup(@Body req: MuteGroupReq): Flowable<Void>

    @POST("/group/member/role")
    fun updateGroupMemberRole(@Body req: UpdateGroupMemberRoleReq): Flowable<Void>
}