# IMCore API介绍

## 数据模型

### Message

```kotlin
data class Message(
    @SerializedName("id")
    @ColumnInfo(name = "id")
    var id: Long = 0, // 消息id
    @SerializedName("from_u_id")
    @ColumnInfo(name = "from_u_id")
    var fUid: Long = 0, // 发件人id
    @SerializedName("session_id")
    @ColumnInfo(name = "session_id")
    var sid: Long = 0,  // 会话id
    @SerializedName("msg_id")
    @ColumnInfo(name = "msg_id")
    var msgId: Long = 0, // 服务端生成的消息id
    @SerializedName("type")
    @ColumnInfo(name = "type")
    var type: Int = 0, // 消息类型
    @SerializedName("content")
    @ColumnInfo(name = "content", typeAffinity = ColumnInfo.TEXT)
    var content: String? = null,      // 消息原始内容
    @SerializedName("data")
    @ColumnInfo(name = "data", typeAffinity = ColumnInfo.TEXT)
    var data: String? = null,            // 消息本地内容
    @SerializedName("send_status")
    @ColumnInfo(name = "send_status")
    var sendStatus: Int = 0,      // 消息状态  参见枚举: MsgSendStatus
    @SerializedName("opr_status")
    @ColumnInfo(name = "opr_status")
    var oprStatus: Int = 0,        // 消息操作状态 参见枚举: MsgOperateStatus
    @SerializedName("r_users")
    @ColumnInfo(name = "r_users", typeAffinity = ColumnInfo.TEXT)
    var rUsers: String? = null,       // 已读用户uid
    @SerializedName("r_msg_id")
    @ColumnInfo(name = "r_msg_id")
    var rMsgId: Long? = null,        // 引用消息id
    @SerializedName("at_users")
    @ColumnInfo(name = "at_users", typeAffinity = ColumnInfo.TEXT)
    var atUsers: String? = null,     // @用户uid1#uid2
    @SerializedName("ext_data")
    @ColumnInfo(name = "ext_data", typeAffinity = ColumnInfo.TEXT)
    var extData: String?,   // 扩展字段
    @SerializedName("c_time")
    @ColumnInfo(name = "c_time")
    var cTime: Long = 0,
    @SerializedName("m_time")
    @ColumnInfo(name = "m_time")
    var mTime: Long = 0,
) : Parcelable {
    
}
```

### Session
```kotlin
data class Session(
    @SerializedName("id")
    @PrimaryKey @ColumnInfo(name = "id")
    var id: Long,  // id
    @SerializedName("parent_id")
    @ColumnInfo(name = "parent_id")
    var parentId: Long, // 父session id
    @SerializedName("type")
    @ColumnInfo(name = "type")
    var type: Int,  // 类型 1单聊 2群聊 3超级群
    @SerializedName("entity_id")
    @ColumnInfo(name = "entity_id")
    var entityId: Long,  // session对应的实体id：用户id或者群id
    @SerializedName("name")
    @ColumnInfo(name = "name")
    var name: String,   // session名称
    @SerializedName("note_name")
    @ColumnInfo(name = "note_name")
    var noteName: String?,   // 【额外设置】自己在这个session下的昵称
    @ColumnInfo(name = "note_avatar")
    var noteAvatar: String?,  // 【额外设置】 自己在这个session下的头像
    @SerializedName("remark")
    @ColumnInfo(name = "remark")
    var remark: String, // 备注
    @SerializedName("mute")
    @ColumnInfo(name = "mute")
    var mute: Int,  // 1 禁言 0 正常
    @SerializedName("status")
    @ColumnInfo(name = "status")
    var status: Int, // 1 静音 2 拒收
    @SerializedName("role")
    @ColumnInfo(name = "role")
    var role: Int, // session 角色 4 owner 3 superAdmin 2 admin 1 member
    @SerializedName("top_timestamp")
    @ColumnInfo(name = "top_timestamp")
    var topTimestamp: Long, // 置顶时间戳 用户session置顶和排序 越大代表越后置顶 排在最上面
    @SerializedName("ext_data")
    @ColumnInfo(name = "ext_data", typeAffinity = ColumnInfo.TEXT)
    var extData: String?,   // 扩展字段
    @SerializedName("unread_count")
    @ColumnInfo(name = "unread_count")
    var unReadCount: Int, // session 未读数
    @SerializedName("draft")
    @ColumnInfo(name = "draft")
    var draft: String?, // session 草稿
    @SerializedName("last_msg")
    @ColumnInfo(name = "last_msg", typeAffinity = ColumnInfo.TEXT)
    var lastMsg: String?, // session 最后一条消息内容
    @SerializedName("msg_sync_time")
    @ColumnInfo(name = "msg_sync_time")
    var msgSyncTime: Long = 0, // session 消息上一次同步的时间
    @SerializedName("member_sync_time")
    @ColumnInfo(name = "member_sync_time")
    var memberSyncTime: Long = 0, // session 成员上一次同步的时间
    @SerializedName("member_count")
    @ColumnInfo(name = "member_count")
    var memberCount: Int,
    @SerializedName("function_flag")
    @ColumnInfo(name = "function_flag")
    var functionFlag: Long,
    @SerializedName("deleted")
    @ColumnInfo(name = "deleted")
    var deleted: Int = 0,
    @SerializedName("c_time")
    @ColumnInfo(name = "c_time")
    val cTime: Long,
    @SerializedName("m_time")
    @ColumnInfo(name = "m_time")
    var mTime: Long,
) : Parcelable {

}

```

### session成员

```kotlin
data class SessionMember(
    @SerializedName("session_id")
    @ColumnInfo(name = "session_id")
    var sessionId: Long,
    @SerializedName("user_id")
    @ColumnInfo(name = "user_id")
    var userId: Long,
    @SerializedName("role")
    @ColumnInfo(name = "role")
    var role: Int,
    @SerializedName("status")
    @ColumnInfo(name = "status")
    var status: Int, // 消息接受状态 1 拒收 0 正常
    @SerializedName("mute")
    @ColumnInfo(name = "mute")
    var mute: Int, // 禁言状态 1 禁言 0 正常
    @SerializedName("note_name")
    @ColumnInfo(name = "note_name")
    var noteName: String?, // 用户这个session下的昵称
    @SerializedName("note_avatar")
    @ColumnInfo(name = "note_avatar")
    var noteAvatar: String?, // 用户在这个session下的昵称
    @SerializedName("ext_data")
    @ColumnInfo(name = "ext_data", typeAffinity = ColumnInfo.TEXT)
    var extData: String?,   //扩展字段
    @SerializedName("c_time")
    @ColumnInfo(name = "c_time")
    var cTime: Long,
    @SerializedName("m_time")
    @ColumnInfo(name = "m_time")
    var mTime: Long,
    @ColumnInfo(name = "deleted")
    @SerializedName("deleted")
    var deleted: Int,
) : Parcelable
```

## 本地数据库

### 消息Dao

```kotlin
interface IMMessageDao {

    /**
     * 批量插入或替换
     */
    fun insertOrReplace(messages: List<Message>)

    /**
     * 批量插入或忽略
     */
    fun insertOrIgnore(messages: List<Message>)

    /**
     * 删除startTime以前 endTime以后的消息
     */
    fun deleteByCTimeExclude(sid: Long, startTime: Long, endTime: Long)

    /**
     * 删除startTime和endTime之间的
     */
    fun deleteByCTimeInclude(sid: Long, startTime: Long, endTime: Long)

    /**
     * 删除session下的消息
     */
    fun deleteBySessionId(sid: Long)
    
    /**
     * 删除sessions下的消息
     */
    fun deleteBySessionIds(sids: Set<Long>)

    /**
     * 删除多条消息
     */
    fun delete(messages: List<Message>)
    
    /**
     * 更新多条消息
     */
    fun update(messages: List<Message>)

    /**
     * 更新消息的发送状态
     */
    fun updateSendStatus(
        sId: Long,
        id: Long,
        sendStatus: Int,
        fUId: Long
    )

    /**
     * 更新消息的data内容
     */
    fun updateMsgData(sId: Long, id: Long, fromId: Long, data: String)

    /**
     * 更新会话的所有消息为已读
     */
    fun updateStatusBySessionId(sid: Long, oprStatus: Int)

    /**
     * 更新消息的操作状态
     */
    fun updateOperationStatus(
        sid: Long,
        msgIds: Set<Long>,
        oprStatus: Int
    )

    /**
     * 设置所有消息已读
     */
    fun updateAllMsgRead()

    /**
     *  设置sessionIds下所有消息已读
     */
    fun updateAllMsgReadBySessionIds(
        sessionIds: List<Long>
    )

    /**
     * 重置消息状态
     */
    fun resetSendingMessage(
        status: Int = MsgSendStatus.SendFailed.value,
        successStatus: Int = MsgSendStatus.Success.value
    )
    
    /**
     * 查询发送中的消息
     */
    fun findSendingMessages(successStatus: Int = MsgSendStatus.Success.value): List<Message>

    /**
     * 获取消息未读数
     */
    fun getUnReadCount(id: Long, oprStatus: Int = MsgOperateStatus.ClientRead.value): Int

    /**
     * 查询session下startTime和endTime之间的消息
     */
    fun findByTimeRange(sid: Long, startTime: Long, endTime: Long, count: Int): List<Message>

    /**
     * 通过消息id/发件人id/sessionId查询消息
     */
    fun findById(id: Long, fUId: Long, sid: Long): Message?

    /**
     * 通过服务端消息id/sessionId查询消息
     */
    fun findByMsgId(msgId: Long, sid: Long): Message?

    /**
     * 查询消息被哪些消息引用
     */
    fun findByReferMsgId(referMsgId: Long, sid: Long): List<Message>
    
    /**
     * 查询多条消息
     */
    fun findByMsgIds(msgIds: Set<Long>, sid: Long): List<Message>

    /**
     * 查询cTime之后的消息
     * @sId sessionId
     * @msgId 消息服务端id
     * @types 消息类型数组
     * @cTime 创建时间
     * @count 数量
     */
    fun findOlderMessage(
        sId: Long,
        msgId: Long,
        types: Array<Int>,
        cTime: Long,
        count: Int
    ): List<Message>

    /**
     * 查询cTime之前的消息
     * @sId sessionId
     * @msgId 消息服务端id
     * @types 消息类型数组
     * @cTime 创建时间
     * @count 数量
     */
    fun findNewerMessage(
        sId: Long,
        msgId: Long,
        types: Array<Int>,
        cTime: Long,
        count: Int
    ): List<Message>

    /**
     * 查询session的最早一条未读消息
     */
    fun findOldestUnreadMessage(sid: Long): Message?


    /**
     * 查询session的最后一条消息
     */
    fun findLastMessageBySessionId(sid: Long): Message?


    /**
     * 查询session中At我的未读消息
     */
    fun findSessionAtMeUnreadMessages(sessionId: Long): List<Message>


    /**
     * 查询session下所有未读消息
     */
    fun findAllUnreadMessagesBySessionId(sessionId: Long): List<Message>

    /**
     * 查询所有未读消息
     */
    fun findAllUnreadMessages(): List<Message>

    /**
     * 查询某个类型的最新的消息
     */
    fun findLatestMessagesByType(msgType: Int, offset: Int, count: Int): List<Message>

    /**
     * 搜索消息
     * @sId sessionId
     * @type 类型
     * @keyword 关键字
     * @count 数量
     * @offset 偏移位置
     */
    fun search(sid: Long, type: Int, keyword: String, count: Int, offset: Int): List<Message>


    /**
     * 搜索消息
     * @sId sessionId
     * @type 类型
     * @keyword 关键字
     * @count 数量
     * @offset 偏移位置
     */
    fun search(sid: Long, keyword: String, count: Int, offset: Int): List<Message>


    /**
     * 搜索消息
     * @type 类型
     * @keyword 关键字
     * @count 数量
     * @offset 偏移位置
     */
    fun search(type: Int, keyword: String, count: Int, offset: Int): List<Message>


    /**
     * 搜索消息
     * @keyword 关键字
     * @count 数量
     * @offset 偏移位置
     */
    fun search(keyword: String, count: Int, offset: Int): List<Message>

}
```


### SessionDao

```kotlin
interface IMSessionDao {

    // 批量插入或替换
    fun insertOrReplace(sessions: List<Session>)

    // 批量插入或忽略
    fun insertOrIgnore(sessions: List<Session>)

    // 更新
    fun update(session: Session)

    // 删除
    fun deleteById(id: Long)

    // 批量删除
    fun delete(sessions: List<Session>): Int

    // 设置置顶时间戳
    fun updateTop(id: Long, top: Long)
    
    // 更新session状态
    fun updateStatus(id: Long, status: Int)
    
    // 更新session草稿
    fun updateDraft(id: Long, draft: String)

    // 更新未读数
    fun updateUnread(id: Long, unread: Int)

    // 更新session成员同步时间戳
    fun updateMemberSyncTime(id: Long, time: Long)

    // 更新session消息同步时间戳
    fun updateMsgSyncTime(id: Long, time: Long)

    // 更新session成员数量
    fun updateMemberCount(id: Long, count: Int)

    // 设置session下所有消息已读
    fun updateAllMsgRead()

    // 通过实体id和类型查找session
    fun findByEntityId(entityId: Long, type: Int): Session?

    // 通过id查找session
    fun findById(id: Long): Session?

    // 查询session的消息同步时间
    fun findMsgSyncTimeById(id: Long): Long

    // 查询session的成员同步时间
    fun findMemberSyncTimeById(id: Long): Long

    // 通过父id查找mTime之前的session列表（不包含软删除的）
    fun findByParentId(parentId: Long, count: Int, mTime: Long): List<Session>

    // 通过父id查找mTime之前的session列表（包含软删除的）
    fun findAllByParentId(parentId: Long, count: Int, mTime: Long): List<Session>

    // 通过类型查询所有的session
    fun findAll(type: Int): List<Session>

    // 查询有未读的session
    fun findUnreadSessions(parentId: Long): List<Session>
}
```

### session成员
```kotlin
interface IMSessionMemberDao {

    // 批量插入或替换
    fun insertOrReplace(members: List<SessionMember>)

    // 批量插入或忽略
    fun insertOrIgnore(members: List<SessionMember>)

    // 批量删除
    fun delete(members: List<SessionMember>)

    // 查询sessionId下的所有session成员
    fun findBySessionId(sessionId: Long): List<SessionMember>

    // 分页查询sessionId下的session成员
    fun findBySessionId(sessionId: Long, offset: Int, count: Int): List<SessionMember>

    // 分页查询sessionId下的session成员 角色从大到小排序
    fun findBySessionIdSortByRole(sessionId: Long, offset: Int, count: Int): List<SessionMember>

    // 根据userId查询sessionId下的某个session成员 
    fun findSessionMember(sessionId: Long, userId: Long): SessionMember?

    // 根据userIds查询sessionId下的某些session成员 
    fun findSessionMembers(sessionId: Long, userIds: Set<Long>): List<SessionMember>

    // 查询sessionId下的成员数量
    fun findSessionMemberCount(sessionId: Long): Int
}
```

### 几个枚举
```kotlin
/**
 * 信令状态
 */
@Keep
enum class SignalStatus(val value: Int) {
    Init(0),
    Connecting(1),
    Connected(2),
    Disconnected(3)
}

/**
 * session 禁言
 */
@Keep
enum class SessionMuted(val value: Int) {
    Normal(0), // 正常
    All(1), // 全局被禁言
    MySelf(2), // 自己被禁言
}

/**
 * session状态
 */
@Keep
enum class SessionStatus(val value: Int) {
    Reject(1),
    Silence(2),
}

/**
 * 会话类型
 */
@Keep
enum class SessionType(val value: Int) {
    Single(1),
    Group(2),
    SuperGroup(3),
    MsgRecord(4) // 会话记录
}

/**
 * session 角色
 */
@Keep
enum class SessionRole(val value: Int) {
    Member(1),
    Admin(2),
    SuperAdmin(3),
    Owner(4),
}


/**
 * 消息发送状态
 */
@Keep
enum class MsgSendStatus(val value: Int) {
    Init(0),                // 初始
    Uploading(1),             // 上传中
    Sending(2),             // 发送中
    SendFailed(3),          // 发送失败
    Success(4),         // 发送或接收成功
}

/**
 * 消息操作状态
 */
@Keep
enum class MsgOperateStatus(var value: Int) {
    Init(0),
    Ack(1),        // 用户已接收
    ClientRead(2), // 用户已读
    ServerRead(4), // 用户已告知服务端已读
    Update(8)     // 用户更新消息（重新编辑等操作）
}


```


## IMMessageModule介绍


```kotlin

interface MessageModule : BaseModule {
    /**
     * 注册消息处理器
     */
    fun registerMsgProcessor(processor: IMBaseMsgProcessor)

    /**
     * 获取注册消息处理器
     */
    fun getMsgProcessor(msgType: Int): IMBaseMsgProcessor

    /**
     * 同步离线消息
     */
    fun syncOfflineMessages()

    /**
     * 同步最近session
     */
    fun syncLatestSessionsFromServer()


    /**
     * 同步超级群消息
     */
    fun syncSuperGroupMessages()

    /**
     * 同步超级群消息
     */
    fun syncSuperGroupMessages(session: Session)

    /**
     * 获取session, 先查本地数据库后查服务端
     * @entityId 用户id或者群id
     * @type: 1 单聊 2 普通群 3 超级群
     */
    fun getSession(entityId: Long, type: Int): Flowable<Session>

    /**
     * 获取session, 先查本地数据库后查服务端
     */
    fun getSession(sessionId: Long): Flowable<Session>

    /**
     * 分页获取本地session
     * @parentId 父会话id 0为根级 一般没有分组场景传0
     * @count 父会话id
     * @mTime ms时间戳
     * @excludeDeleted 是否排除掉本地软删除掉的
     */
    fun queryLocalSessions(
        parentId: Long,
        count: Int,
        mTime: Long,
        excludeDeleted: Boolean,
    ): Flowable<List<Session>>

    /**
     * 分页获取本地message
     * @sessionId 会话id
     * @startTime ms 开始时间
     * @endTime ms结束时间
     * @count 数量
     */
    fun queryLocalMessages(
        sessionId: Long,
        startTime: Long,
        endTime: Long,
        count: Int,
    ): Flowable<List<Message>>

    /**
     * 删除Session
     * @session 要删除的session
     * @deleteServer 是否删除服务端的
     */
    fun deleteSession(session: Session, deleteServer: Boolean): Flowable<Void>

    /**
     * 更新session
     * @session 要删除的session
     * @deleteServer 是否删除服务端的
     */
    fun updateSession(session: Session, updateServer: Boolean): Flowable<Void>

    /**
     * 收到新消息
     */
    fun onNewMessage(msg: Message)

    /**
     * 生成新消息id
     */
    fun generateNewMsgId(): Long

    /**
     * 发送消息
     * @sessionId
     * @type 消息类型
     * @body 消息body Any类型 能被Gson序列化成json的类型 或者String类型
     * @data 消息本地数据 Any类型 能被Gson序列化成json的类型 或者String类型 不会被发送到服务端 比如发送图片 data里面存放图片路径，消息处理器从data中拿到图片路径压缩上传后再去生成body数据传到服务端
     * @atUser @人的id字符串 多个用户id#号隔开 所有人传All
     * @replyMsgId 引用或者回复消息id
     * @callback 发送回调函数
     */
    fun sendMessage(
        sessionId: Long,
        type: Int,
        body: Any?,
        data: Any?,
        atUser: String? = null,
        replyMsgId: Long? = null,
        callback: IMSendMsgCallback? = null,
    )

    /**
     * 重发
     * @msg 需要被重发的消息
     * @callback 发送回调函数
     */
    fun resend(msg: Message, callback: IMSendMsgCallback? = null)


    /**
     * 消息发送到服务端
     * @message 需要被发的消息
     */
    fun sendMessageToServer(message: Message): Flowable<Message>

    /**
     * 消息ack:需要ack的消息存入客户端缓存,批量按sessionId进行ack
     */
    fun ackMessageToCache(message: Message)

    /**
     * 消息ack:发送到服务端
     */
    fun ackMessagesToServer()

    /**
     * 批量删除多条消息
     */
    fun deleteMessages(
        sessionId: Long,
        messages: List<Message>,
        deleteServer: Boolean,
    ): Flowable<Void>

    /**
     * 删除本地消息
     */
    fun deleteAllLocalSessionMessage(session: Session): Flowable<Void>

    /**
     * 处理session
     */
    fun processSessionByMessage(msg: Message, forceNotify: Boolean = false)

    /**
     * session下有新消息，发出提示音/震动等通知
     */
    fun notifyNewMessage(session: Session, message: Message)

    /**
     * 查询session下的成员列表
     * @sessionId
     * @
     */
    fun querySessionMembers(sessionId: Long, needUpdate: Boolean): Flowable<List<SessionMember>>

    /**
     * 同步session成员列表
     */
    fun syncSessionMembers(sessionId: Long)

    /**
     * syncSessionMembers 过了多少ms必须从服务器同步
     */
    fun timeoutForSyncSessionMembers(): Long

    /**
     * 清除session下所有已读消息
     */
    fun setAllMessageReadBySessionId(sessionId: Long): Flowable<Void>


    /**
     * 清除所有已读消息
     */
    fun setAllMessageRead(): Flowable<Void>

    /**
     *  处理删除的sessions，服务端同步session时下发应该更新的session数组
     */
    fun onReceivedServerUpdatedSessions(sessions: List<Session>)

    /**
     *  处理删除的sessions，服务端同步session时下发应该删除的session数组
     */
    fun onReceivedServerDeletedSessions(sessions: List<Session>)
}


```


## IM事件定义
```kotlin
/**
 * IM事件
 */
@Keep
enum class IMEvent(val value: String) {
    OnlineStatusUpdate("IMEventOnlineStatusUpdate"), // 连接状态事件
    BatchMsgNew("IMEventBatchMsgNew"), // 批量收到新消息
    MsgNew("IMEventMsgNew"), // 收到新消息
    MsgUpdate("IMEventMsgUpdate"), // 消息状态更新
    MsgDelete("IMEventMsgDelete"), // 消息删除
    BatchMsgDelete("IMEventBatchMsgDelete"), // 批量删除消息
    SessionMessageClear("IMEventSessionMessageClear"), // 清除session消息
    SessionNew("IMEventSessionNew"), // 收到新的session会话
    SessionUpdate("IMEventSessionUpdate"), // session更新
    SessionDelete("IMEventSessionDelete"), // session被删除
    MsgLoadStatusUpdate("IMEventMsgLoadStatusUpdate"), // 消息发送/加载状态更新
}


```

## 发送消息

- 发送图片消息

IMUI库中的图片消息的message.data(本地数据)的json对象类
```kotlin
@Keep
class IMImageMsgData(
    @SerializedName("path")
    var path: String? = null,
    @SerializedName("thumbnail_path")
    var thumbnailPath: String? = null,
    @SerializedName("width")
    var width: Int? = null,
    @SerializedName("height")
    var height: Int? = null,
)
```

IMUI库中的图片消息的message.body（发给服务端的消息body）的json对象类
```kotlin
@Keep
class IMImageMsgBody(
    @SerializedName("url")
    var url: String? = null,
    @SerializedName("thumbnail_url")
    var thumbnailUrl: String? = null,
    @SerializedName("width")
    var width: Int? = null,
    @SerializedName("height")
    var height: Int? = null,
    @SerializedName("name")
    var name: String? = null,
)



```


- 注册消息处理器

```kotlin

open class IMImageMsgProcessor : IMBaseMsgProcessor() {

    override fun messageType(): Int {
        return MsgType.Image.value
    }

    // 消息在session列表的描述
    override fun msgDesc(msg: Message): String {
        return IMCoreManager.app.getString(R.string.im_image_msg)
    }

    // 预处理消息，message.data中取出图片路径 进行压缩
    override fun reprocessingFlowable(message: Message): Flowable<Message> {
        try {
            var imageData = Gson().fromJson(message.data, IMImageMsgData::class.java)
            if (imageData.path == null) {
                return Flowable.error(FileNotFoundException())
            }
            val pair = checkDir(IMCoreManager.storageModule, imageData, message)
            imageData = pair.first
            return if (imageData.thumbnailPath == null) {
                compress(IMCoreManager.storageModule, imageData, pair.second)
            } else {
                Flowable.just(pair.second)
            }
        } catch (e: Exception) {
            e.message?.let { LLog.e(it) }
            return Flowable.error(e)
        }
    }

    @Throws(Exception::class)
    private fun checkDir(
        storageModule: StorageModule, imageData: IMImageMsgData, entity: Message
    ): Pair<IMImageMsgData, Message> {
        val isAssignedPath = storageModule.isAssignedPath(
            imageData.path!!, IMFileFormat.Image.value, entity.sid
        )
        val pair = storageModule.getPathsFromFullPath(imageData.path!!)
        if (!isAssignedPath) {
            val dePath = storageModule.allocSessionFilePath(
                entity.sid, pair.second, IMFileFormat.Image.value
            )
            storageModule.copyFile(imageData.path!!, dePath)
            imageData.path = dePath
            entity.data = Gson().toJson(imageData)
        }
        return Pair(imageData, entity)
    }

    private fun compress(
        storageModule: StorageModule, imageData: IMImageMsgData, entity: Message
    ): Flowable<Message> {
        val paths = storageModule.getPathsFromFullPath(imageData.path!!)
        val names = storageModule.getFileExt(paths.second)
        val thumbName = "${names.first}_thumb.${names.second}"
        val thumbPath =
            storageModule.allocSessionFilePath(entity.sid, thumbName, IMFileFormat.Image.value)
        return CompressUtils.compress(
            imageData.path!!, 100 * 1024, thumbPath
        ).flatMap {
            val size = CompressUtils.getBitmapAspect(imageData.path!!)
            imageData.thumbnailPath = thumbPath
            imageData.width = size.first
            imageData.height = size.second
            entity.data = Gson().toJson(imageData)
            return@flatMap Flowable.just(entity)
        }
    }

    // 先上传压缩图，在上传原始图
    override fun uploadFlowable(entity: Message): Flowable<Message>? {
        return this.uploadThumbImage(entity).flatMap {
            return@flatMap this.uploadOriginImage(it)
        }
    }

    private fun uploadThumbImage(entity: Message): Flowable<Message> {
        try {
            LLog.v("uploadThumbImage start")
            val imageData = Gson().fromJson(entity.data, IMImageMsgData::class.java)
            var imageBody = Gson().fromJson(entity.content, IMImageMsgBody::class.java)
            if (imageBody != null) {
                if (!imageBody.thumbnailUrl.isNullOrEmpty()) {
                    return Flowable.just(entity)
                }
            }
            if (imageData == null || imageData.thumbnailPath.isNullOrEmpty()) {
                return Flowable.error(FileNotFoundException())
            } else {
                val pair =
                    IMCoreManager.storageModule.getPathsFromFullPath(imageData.thumbnailPath!!)
                return Flowable.create({
                    IMCoreManager.fileLoadModule.upload(
                        imageData.thumbnailPath!!,
                        entity,
                        object : LoadListener {

                            override fun onProgress(
                                progress: Int,
                                state: Int,
                                url: String,
                                path: String,
                                exception: Exception?
                            ) {
                                XEventBus.post(
                                    IMEvent.MsgLoadStatusUpdate.value, IMLoadProgress(
                                        IMLoadType.Upload.value, url, path, state, progress
                                    )
                                )
                                when (state) {
                                    FileLoadState.Init.value, FileLoadState.Wait.value, FileLoadState.Ing.value -> {
                                    }

                                    FileLoadState.Success.value -> {
                                        if (imageBody == null) {
                                            imageBody = IMImageMsgBody()
                                        }
                                        imageBody.thumbnailUrl = url
                                        imageBody.name = pair.second
                                        imageBody.width = imageData.width
                                        imageBody.height = imageData.height
                                        entity.content = Gson().toJson(imageBody)
                                        insertOrUpdateDb(
                                            entity,
                                            notify = false,
                                            notifySession = false,
                                        )
                                        it.onNext(entity)
                                        it.onComplete()
                                    }

                                    else -> {
                                        if (exception != null) {
                                            it.onError(exception)
                                        } else {
                                            it.onError(RuntimeException())
                                        }
                                    }
                                }
                            }

                            override fun notifyOnUiThread(): Boolean {
                                return false
                            }
                        })
                }, BackpressureStrategy.LATEST)
            }
        } catch (e: Exception) {
            e.message?.let { LLog.e(it) }
            return Flowable.error(e)
        }
    }

    private fun uploadOriginImage(entity: Message): Flowable<Message> {
        LLog.v("uploadOriginImage start")
        try {
            val imageData = Gson().fromJson(entity.data, IMImageMsgData::class.java)
            var imageBody = Gson().fromJson(entity.content, IMImageMsgBody::class.java)
            if (imageBody != null) {
                if (!imageBody.url.isNullOrEmpty()) {
                    return Flowable.just(entity)
                }
            }
            if (imageData == null || imageData.path.isNullOrEmpty()) {
                return Flowable.error(FileNotFoundException())
            } else {
                if (imageData.path.equals(imageData.thumbnailPath)) {
                    imageBody.url = imageBody.thumbnailUrl
                    entity.content = Gson().toJson(imageBody)
                    return Flowable.just(entity)
                }
                val pair = IMCoreManager.storageModule.getPathsFromFullPath(imageData.path!!)
                return Flowable.create({
                    IMCoreManager.fileLoadModule.upload(
                        imageData.path!!,
                        entity,
                        object : LoadListener {

                            override fun onProgress(
                                progress: Int,
                                state: Int,
                                url: String,
                                path: String,
                                exception: Exception?
                            ) {
                                XEventBus.post(
                                    IMEvent.MsgLoadStatusUpdate.value, IMLoadProgress(
                                        IMLoadType.Upload.value, url, path, state, progress
                                    )
                                )

                                when (state) {
                                    FileLoadState.Init.value, FileLoadState.Wait.value, FileLoadState.Ing.value -> {
                                    }

                                    FileLoadState.Success.value -> {
                                        if (imageBody == null) {
                                            imageBody = IMImageMsgBody()
                                        }
                                        imageBody.url = url
                                        imageBody.name = pair.second
                                        entity.content = Gson().toJson(imageBody)
                                        it.onNext(entity)
                                        it.onComplete()
                                    }

                                    else -> {
                                        if (exception != null) {
                                            it.onError(exception)
                                        } else {
                                            it.onError(RuntimeException())
                                        }
                                    }
                                }
                            }

                            override fun notifyOnUiThread(): Boolean {
                                return false
                            }
                        })
                }, BackpressureStrategy.LATEST)
            }
        } catch (e: Exception) {
            e.message?.let { LLog.e(it) }
            return Flowable.error(e)
        }
    }

    // 显示别人发的图片时下载图片，保存到本地路径后更新message的data字段
    override fun downloadMsgContent(entity: Message, resourceType: String): Boolean {
        if (entity.content.isNullOrEmpty()) {
            return false
        }
        var data = Gson().fromJson(entity.data, IMImageMsgData::class.java)
        val body = Gson().fromJson(entity.content, IMImageMsgBody::class.java)

        val downloadUrl = if (resourceType == IMMsgResourceType.Thumbnail.value) {
            body.thumbnailUrl
        } else {
            body.url
        }

        var fileName = body.name
        if (downloadUrl == null || fileName == null) {
            return false
        }

        if (downLoadingUrls.contains(downloadUrl)) {
            return true
        } else {
            downLoadingUrls.add(downloadUrl)
        }

        if (resourceType == IMMsgResourceType.Thumbnail.value) {
            fileName = "thumb_${fileName}"
        }

        val listener = object : LoadListener {
            override fun onProgress(
                progress: Int, state: Int, url: String, path: String, exception: Exception?
            ) {
                XEventBus.post(
                    IMEvent.MsgLoadStatusUpdate.value,
                    IMLoadProgress(IMLoadType.Download.value, url, path, state, progress)
                )
                when (state) {
                    FileLoadState.Init.value, FileLoadState.Wait.value, FileLoadState.Ing.value -> {
                    }

                    FileLoadState.Success.value -> {
                        if (data == null) {
                            data = IMImageMsgData()
                        }
                        val localPath = IMCoreManager.storageModule.allocSessionFilePath(
                            entity.sid, fileName, IMFileFormat.Image.value
                        )
                        IMCoreManager.storageModule.copyFile(path, localPath)
                        data.height = body.height
                        data.width = body.width
                        if (resourceType == IMMsgResourceType.Thumbnail.value) {
                            data.thumbnailPath = localPath
                        } else {
                            data.path = localPath
                        }
                        entity.data = Gson().toJson(data)
                        insertOrUpdateDb(entity, notify = true, notifySession = false)
                        downLoadingUrls.remove(downloadUrl)
                    }

                    else -> {
                        downLoadingUrls.remove(downloadUrl)
                    }
                }
            }

            override fun notifyOnUiThread(): Boolean {
                return false
            }

        }
        IMCoreManager.fileLoadModule.download(downloadUrl, entity, listener)
        return true
    }

}
```

- 调用发送图片

```kotlin
 private fun onMediaResult(result: List<IMFile>) {
        try {
            for (media in result) {
                if (media.mimeType.startsWith("video", true)) {
                    if (IMUIManager.uiResourceProvider?.supportFunction(
                            session!!,
                            IMChatFunction.Video.value
                        ) == false
                    ) {
                        showMessage(getString(R.string.do_not_allow_send_video), false)
                        return
                    }
                    val videoMsgData = IMVideoMsgData()
                    videoMsgData.path = media.path
                    sendMessage(MsgType.Video.value, null, videoMsgData)
                } else if (media.mimeType.startsWith("image", true)) {
                    if (IMUIManager.uiResourceProvider?.supportFunction(
                            session!!,
                            IMChatFunction.Image.value
                        ) == false
                    ) {
                        showMessage(getString(R.string.do_not_allow_send_image), false)
                        return
                    }
                    val imageMsgData = IMImageMsgData()
                    imageMsgData.path = media.path
                    sendMessage(MsgType.Image.value, null, imageMsgData)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

     fun sendMessage(type: Int, body: Any?, data: Any?, atUser: String?) {
        val callback = object : IMSendMsgCallback {
            override fun onResult(message: Message, e: Throwable?) {
                e?.let {
                    showError(it)
                }
            }
        }
        val referMsg = binding.llInputLayout.getReplyMessage()
        if (referMsg != null) {
            binding.llInputLayout.clearReplyMessage()
        }
        IMCoreManager.messageModule.sendMessage(
            session!!.id,
            type,
            body,
            data,
            atUser,
            referMsg?.msgId,
            callback
        )
    }
```

开发者可以自己实现自己的图片发送逻辑，提前构造message.body， 不在msg processor中处理，直接发送
```kotlin
IMCoreManager.messageModule.sendMessage(
            session!!.id,
            type,
            yourmessageBody,
            null,
            atUser,
            referMsg?.msgId,
            callback
        )
```

- 监听消息

```kotlin

    XEventBus.observe(this, IMEvent.BatchMsgNew.value, Observer<Pair<Long, List<Message>>> {
            if (it.sid == this.session!!.id) {
                // TODO 更新ui
            }
        })
        XEventBus.observe(this, IMEvent.MsgNew.value, Observer<Message> {
            if (it.sid == this.session!!.id) {
                // TODO 更新ui
            }
        })
        XEventBus.observe(this, IMEvent.MsgUpdate.value, Observer<Message> {
            if (it.sid == this.session!!.id) {
                // TODO 更新ui
            }
        })
        XEventBus.observe(this, IMEvent.MsgDelete.value, Observer<Message> {
            if (it.sid == this.session!!.id) {
                // TODO 更新ui
            }
        })
        XEventBus.observe(this, IMEvent.BatchMsgDelete.value, Observer<List<Message>> {
            if (it.sid == this.session!!.id) {
                // TODO 更新ui
            }
        })

```

- 监听session更新
```kotlin

        XEventBus.observe(this, IMEvent.SessionUpdate.value, Observer<Session> {
            updateSession(it)
        })

        XEventBus.observe(this, IMEvent.SessionNew.value, Observer<Session> {
            updateSession(it)
        })

        XEventBus.observe(this, IMEvent.SessionDelete.value, Observer<Session> {
            updateSession(it)
        })
```





