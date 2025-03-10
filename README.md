## 集成
- IMCore模块：包含长链接模块/消息模块/用户模块/联系人模块/群模块/本地数据库/本地文件管理等
- IMUI模块: 包含IM会话页面组件/聊天页面组件/消息弹出面板/@人页面等页面，内置了文本消息/语音消息/图片消息/视频消息/会话记录/撤回/时间线等几种消息实现
- IMPreview模块：包含语音/图片/视频消息预览，消息记录预览
- IMProvider模块：包含语音/图片/视频消息内容提供

- IOS/CocoaPods

``` ruby

  pod 'THKIMSDK/IMCore', :git => 'git@github.com:THK-IM/THK-IM-IOS.git', :tag => '0.4.0'
  pod 'THKIMSDK/IMUI', :git => 'git@github.com:THK-IM/THK-IM-IOS.git', :tag => '0.4.0'
  pod 'THKIMSDK/IMPreviewer', :git => 'git@github.com:THK-IM/THK-IM-IOS.git', :tag => '0.4.0'
  pod 'THKIMSDK/IMProvider', :git => 'git@github.com:THK-IM/THK-IM-IOS.git', :tag => '0.4.0'

```

- Android/maven

```gradle

maven() {
    url "https://s01.oss.sonatype.org/content/repositories/releases"
}

implementation "io.github.thk-im:ui:0.3.4"
implementation "io.github.thk-im:preview:0.3.4"
implementation "io.github.thk-im:provider:0.3.4"
implementation "io.github.thk-im:core:0.3.4"

```


## 说明

### 长连接和信令

- signal协议

```swift
public protocol SignalModule: AnyObject {

    func connect()

    func sendSignal(_ signal: String)

    func disconnect(_ reason: String)

    func getSignalStatus() -> SignalStatus

    func setSignalListener(_ listener: SignalListener)
}

```

- 注册singal委托(如需自己实现)

```swift

IMCoreManager.shared.signalModule = yourSignalModule

```

- 加解密协议

```swift
public protocol Crypto {
    func encrypt(_ text: String) -> String?
    func decrypt(_ cipherText: String) -> String?
}
```

- 注册加解密委托（可选， 与服务端保持一致）

```swift
    IMCoreManager.shared.crypto = yourCryptor
```

- 信令定义

```swift
open class Signal: Codable {

    var type: Int
    var body: String

    static var ping: String {
        let signal = Signal(SignalType.SignalHeatBeat.rawValue, "ping")
        let data = try! JSONEncoder().encode(signal)
        return String(data: data, encoding: .utf8)!
    }

    init(_ type: Int, _ body: String) {
        self.type = type
        self.body = body
    }

    enum CodingKeys: String, CodingKey {
        case type = "type"
        case body = "body"
    }

}

public enum SignalType: Int {
    case SignalNewMessage = 0 // 新消息， 服务端单向下行
    case
        SignalHeatBeat = 1 // 心跳，客户端/服务端双向 
    case
        SignalSyncTime = 2 // 时间同步，服务端单向下行
    case
        SignalConnId = 3 // 连接id下发， 服务端单向下行
    case
        SignalKickOffUser = 4 // 踢下线， 服务端单向下行
}

```

- 信令划分，收到新信令交给不同的模块处理

```swift
public func onNewSignal(_ type: Int, _ body: String) {
        if type == SignalType.SignalNewMessage.rawValue {
            messageModule.onSignalReceived(type, body)
        } else if type < 100 {
            commonModule.onSignalReceived(type, body)
        } else if type < 200 {
            userModule.onSignalReceived(type, body)
        } else if type < 300 {
            contactModule.onSignalReceived(type, body)
        } else if type < 400 {
            groupModule.onSignalReceived(type, body)
        } else {
            customModule.onSignalReceived(type, body)
        }
    }
```

- 监听连接状态

```swift

SwiftEventBus.onMainThread(
        self, name: IMEvent.OnlineStatusUpdate.rawValue
    ) { [weak self] _ in
        self?.updateConnectStatus()
    }

    func updateConnectStatus() {
    let connectStatus = IMCoreManager.shared.signalModule.getSignalStatus()
    ...
}

```


### MessageModule消息模块介绍

- 核心功能：同步离线消息/消息ACK/会话/session成员/消息发送
- 核心数据模型： Message/Session
- 本地数据库接口：MessageDao/SessionDao/SessionMemberDao
- 外部可继承重载

#### MessageModule模块下的消息处理器介绍

- 发送消息的过程

1. 包装消息
2. 消息入库，通知ui消息发送中
3. 消息二次处理（图片缩略图生成/视频抽帧等
4. 消息附件上传 (缩略图/原始图/视频上传等)
5. 消息正文发送 (调用API，发送成功获取消息id，更新本地数据库)

- 接受消息两个主要场景

1. 长链接收到的单条场景
2. 离线批量同步场景

- 基类IMBaseMsgProcessor函数介绍

```swift

// 消息类型
open func messageType() -> Int {
    return 0
}

// 包装消息
open func buildSendMsg(
        _ sessionId: Int64, _ body: Codable?, _ data: Codable?, _ atUIdStr: String? = nil,
        _ rMsgId: Int64? = nil
    ) throws -> Message {
        ...
        ...
        ...
    }

// 图片压缩/视频抽帧等操作二次处理，参考IMVideoMsgProcessor
open func reprocessingObservable(_ message: Message) -> Observable<Message>? {
    return nil
}

// 上传，参考IMVideoMsgProcessor
open func uploadObservable(_ message: Message) -> Observable<Message>? {
    return nil
}

// 发送到服务器
open func sendToServer(_ message: Message) -> Observable<Message> {
    return IMCoreManager.shared.api.sendMessageToServer(msg: message)
}

/**
* 消息内容下载, 参考IMVideoMsgProcessor
*/
open func downloadMsgContent(_ message: Message, resourceType: String) -> Bool {
    return true
}

/**
    * 发送消息,逻辑流程:
    * 1、写入数据库,
    * 2、消息处理，图片压缩/视频抽帧等
    * 3、文件上传
    * 4、调用api发送消息到服务器
    */
open func sendMessage(
    _ sessionId: Int64, _ body: Codable?, _ data: Codable?, _ atUsers: String? = nil,
    _ rMsgId: Int64? = nil, _ sendResult: IMSendMsgResult? = nil
) {
    do {
        let originMsg = try self.buildSendMsg(sessionId, body, data, atUsers, rMsgId)
        self.send(originMsg, false, sendResult)
    } catch {
        DDLogError("sendMessage: \(error)")
    }
}



// 收到消息，撤回消息可以参考IMRevokeMsgProcessor实现
open func received(_ msg: Message) {
    ...
    ...
    ...
}

/**
    * 消息是否需要二次处理，用于拉取同步消息时，不需要二次处理的消息批量入库，需要二次处理的消息单独处理
    */
open func needReprocess(msg: Message) -> Bool {
    return false
}

/**
* 消息文本描述，用户会话列表那里展示，比如 [撤回消息] [红包消息]
*/
open func msgDesc(msg: Message) -> String {
    return ""
}


```

- 每一种消息对应一个消息处理器，以下是Core模块内部定义的消息类型

```swift
/// 消息类型
public enum MsgType: Int {
    case Reedit = -3 // 重编辑消息
    case
        Read = -2    // 已读消息
    case
        Received = -1 // 占位/ACK消息
    case
        UnSupport = 0 // 占位/无意义
    case
        Text = 1  // 文本
    case
        Audio = 3 // 语音
    case
        Image = 4 // 图片
    case
        Video = 6 // 视频
    case
        Record = 7 // 转发消息记录
    case
        Revoke = 100 // 撤回消息
    case
        TimeLine = 9999  // 时间线消息
}

```

- 注册消息处理器

```swift
IMUIManager中默认实现了以下几种消息视图处理器
IMCoreManager.shared.messageModule.registerMsgProcessor(IMReadMsgProcessor())
IMCoreManager.shared.messageModule.registerMsgProcessor(IMUnSupportMsgProcessor())
IMCoreManager.shared.messageModule.registerMsgProcessor(IMTextMsgProcessor())
IMCoreManager.shared.messageModule.registerMsgProcessor(IMImageMsgProcessor())
IMCoreManager.shared.messageModule.registerMsgProcessor(IMAudioMsgProcessor())
IMCoreManager.shared.messageModule.registerMsgProcessor(IMVideoMsgProcessor())
IMCoreManager.shared.messageModule.registerMsgProcessor(IMReeditMsgProcessor())
IMCoreManager.shared.messageModule.registerMsgProcessor(IMRevokeMsgProcessor())
IMCoreManager.shared.messageModule.registerMsgProcessor(IMRecordMsgProcessor())


如需新的消息处理器或替换默认Proccessor，外部调用即可
IMCoreManager.shared.messageModule.registerMsgProcessor(yourProccessor)

```

### UserModule消息模块介绍

- 核心功能：IM用户模块, 本地保存IM用户数据，有必要的时候去服务端获取最新的用户数据
- 核心数据模型: User
- 本地数据库接口: UserDao
- 外部可继承重载

### ContactModule消息模块介绍

- 核心功能：IM联系人模块
- 核心数据模型: Contact
- 本地数据库接口: ContactDao
- 外部可继承重载

### GroupModule消息模块介绍

- 核心功能：IM群模块
- 核心数据模型: Group
- 本地数据库接口: GroupDao
- 外部可继承重载

## THKIMSDK/IMUI

### ui视图模块

#### 消息列表

- 每一种消息需要实现一个IMBaseMessageCellProvider

```swift
// 消息是否可以被长按弹出菜单/多选操作
open func canSelected() -> Bool {
    return true
}

// 消息是否有气泡框
open func hasBubble() -> Bool {
    return false
}

// 消息类型
open func messageType() -> Int {
    return 0
}


// 消息列表中使用的Cell
open func viewCellWithWrapper(_ viewType: Int, _ wrapper: IMMsgCellWrapper) -> IMBaseMsgCell {
    let identifier = self.identifier(viewType)
    return IMBaseMsgCell(identifier, messageType(), wrapper)
}

// 消息最大宽度
open func cellMaxWidth() -> CGFloat {
    return UIScreen.main.bounds.width - IMUIManager.shared.msgCellAvatarLeft
        - IMUIManager.shared.msgCellAvatarWidth
        - IMUIManager.shared.msgCellAvatarRight - 20 - IMUIManager.shared.msgCellPadding
}

// 消息被引用/回复时 使用的视图
open func msgBodyView(_ viewPosition: IMMsgPosType) -> IMsgBodyView {
    let view = IMTextMsgView()
    view.setViewPosition(viewPosition)
    return view
}

// 消息被点击时，触发的函数
open func onMsgContentClick(
    _ vc: UIViewController, _ msg: Message, _ session: Session?, _ originView: UIView
) -> Bool {
    return false
}
```

- 注册消息视图提供起器

```swift
IMUIManager中默认实现了以下几种消息视图提供器
self.registerMsgCellProviders(IMUnSupportMsgCellProvider())
self.registerMsgCellProviders(IMTimeLineMsgCellProvider())
self.registerMsgCellProviders(IMTextMsgCellProvider())
self.registerMsgCellProviders(IMImageMsgCellProvider())
self.registerMsgCellProviders(IMAudioMsgCellProvider())
self.registerMsgCellProviders(IMVideoMsgCellProvider())
self.registerMsgCellProviders(IMRevokeMsgCellProvider())
self.registerMsgCellProviders(IMRecordMsgCellProvider())

如需新的消息视图提供器或替换默认Provider，外部调用即可
IMUIManager.shared.registerMsgCellProviders(yourProvider)

```

#### sessionProvider 会话列表

- 如需定制化会话列表中不同类型的会话ui, 自定义IMBaseSessionCellProvider

```swift
IMUIManager中默认实现了以下几种session的CellProvider
self.registerSessionCellProvider(IMSingleSessionCellProvider())
self.registerSessionCellProvider(IMGroupSessionCellProvider())
self.registerSessionCellProvider(IMSuperGroupSessionCellProvider())
```

#### IMBaseFunctionCellProvider 聊天界面底部的功能面板

- 如需定制化会话列表中不同类型的会话ui, 自定义IMBaseFunctionCellProvider

```swift
IMUIManager中默认实现了拍照/传相册
self.registerBottomFunctionProvider(IMPhotoFunctionProvider(), IMCameraFunctionProvider())

红包功能可以加在此处
class RedpackageFunctionProvider: IMBaseFunctionCellProvider {
    ....
}

IMUIManager.shared.registerBottomFunctionProvider(RedpackageFunctionProvider())

TODO： 注册红包消息处理器和视图提供器

```

### IMBasePanelViewProvider 面板提供器

- 表情按钮点击后弹出的面板

```swift
IMUIManager中默认实现了unicode的标签面板
self.registerPanelProvider(IMUnicodeEmojiPanelProvider())

如需更多表情，注册IMBasePanelViewProvider实现

```

### 页面ui定义: IMUIResourceProvider

```swift
public protocol IMUIResourceProvider {

    /// 头像(用户没有头像时取的默认头像)
    func avatar(user: User) -> UIImage?

    /// unicode表情字符串数组
    func unicodeEmojis() -> [String]?

    /// 消息气泡图
    func msgBubble(message: Message, session: Session?) -> UIImage?

    /// 主题色
    func tintColor() -> UIColor?
    
    /// 底部输入，表情/更多/弹出面板背景颜色
    func panelBgColor() -> UIColor?

    /// 输入区域背景颜色
    func inputBgColor() -> UIColor?

    /// 页面背景颜色+文本输入位置背景颜色
    func layoutBgColor() -> UIColor?

    /// 输入文字颜色
    func inputTextColor() -> UIColor?

    /// 界面提示文字颜色
    func tipTextColor() -> UIColor?

    /// 是否支持某个功能
    func supportFunction(_ session: Session, _ functionFlag: Int64) -> Bool

    /// 是否可以At所有人
    func canAtAll(_ session: Session) -> Bool
    
    /// session row 高度
    func sessionRowHeight() -> CGFloat
}
```

- 自定义

```swift
IMUIManager.shared.uiResourceProvider = YourUIResourceProvider()

class YourUIResourceProvider: IMUIResourceProvider {
    ....
}

### 页面路由定义 IMPageRouter

```swift
public protocol IMPageRouter {

    func openSession(controller: UIViewController, session: Session)

    func openUserPage(controller: UIViewController, user: User, session: Session)

    func openGroupPage(controller: UIViewController, group: Group, session: Session)

    func openMsgReadStatusPage(controller: UIViewController, session: Session, message: Message)
}
```

- IMUI模块提供了IMMessageController/IMMessageFragment对外，但完整的路由仍交给由外部管理，当点击用户头像/群头像/会话列表/消息已读状态时，会调用IMPageRouter的相关函数
- 在ios上你可以继承IMMessageController或者使用子视图方式打开消息页面，并根据不同的session定义标题栏
- 在Android上你可以将IMMessageFragment放在你的某个activity里或者父fragment下

### session功能定义

- 通过修改session的属性，可以定制一些聊天页面的功能

ex1. 系统用户和普通用户的聊天页面（系统通知页面），创建session时， 将session的functionFlag字段至为0，则不会出现输入内容；
ex2. 如果不希望在群里能图片，可通过修改session的FunctionFlag设置Image标记为0实现；

```swift
/// 功能，1基础功能 2语音 4 表情  8 图片 16视频  32转发 64已读
public enum IMChatFunction: Int64 {
    case BaseInput = 1
    case
        Audio = 2
    case
        Emoji = 4
    case
        Image = 8
    case
        Video = 16
    case
        Forward = 32
    case
        Read = 64
    case
        ALL = 127
}
``` 



## 整体调用

### IOS

```swift
// app启动时调用
func initIMConfig(_ debug: Bool) {
        self.debugMode = debug
        IMCoreManager.shared.crypto = IMCipher()
        IMCoreManager.shared.initApplication(self.debugMode)
        IMCoreManager.shared.userModule = YourUserModule() // 自实现user
        IMCoreManager.shared.contactModule = YourContactModule()
        IMCoreManager.shared.groupModule = YourGroupModule()
        IMCoreManager.shared.commonModule = YourCommonModule()
        IMCoreManager.shared.messageModule = YourMessageModule()

        IMUIManager.shared.pageRouter = IMExternalPageRouter()
        IMUIManager.shared.uiResourceProvider = IMResourceProvider()

        IMUIManager.shared.registerSessionCellProvider(SingleSessionProvider())
        IMUIManager.shared.registerSessionCellProvider(PlotSessionProvider())
        IMUIManager.shared.registerSessionCellProvider(GroupSessionProvider())
        
        IMUIManager.shared.registerMsgCellProviders(TextMsgProvider())
    }

    // 用户登录成功后调用
    func initIM(token: String, uId: Int64) -> Observable<Bool> {
        return Observable.just(true)
            .flatMap({ it in
                let apiEndpoint = Host.shared.endpointFor(type: "msg")
                let wsEndpoint = Host.shared.endpointFor(
                    type: "websocket")
                IMCoreManager.shared.api = DefaultIMApi(
                    token: token, endpoint: apiEndpoint)
                IMCoreManager.shared.signalModule = DefaultSignalModule(
                    token, wsEndpoint)
                IMCoreManager.shared.fileLoadModule = DefaultFileLoadModule(
                    token, apiEndpoint)

                IMUIManager.shared.initConfig()
                let provider = Provider()
                let previewer = Previewer()
                previewer.setTokenForEndpoint(
                    endPoint: apiEndpoint, token: token)
                IMUIManager.shared.contentProvider = provider
                IMUIManager.shared.contentPreviewer = previewer
                IMCoreManager.shared.initUser(uId)
                return Observable.just(it)
            })
    }

    // 退出时调用 
    func showDown() -> Observable<Bool> {
        return Observable.just(true).flatMap({ it in
            IMCoreManager.shared.shutDown()
            return Observable.just(it)
        })
    }

```

### Android

```kotlin

fun initIMConfig(app: Application, debug: Boolean) {
        this.app = app
        IMCoreManager.init(app, debug)
        IMCoreManager.crypto = IMCipher()
        IMCoreManager.userModule = YourUserModule()
        IMCoreManager.commonModule = YourCommonModule(app)
        IMCoreManager.contactModule = YourContactModule()
        IMCoreManager.groupModule = YourGroupModule()
        IMCoreManager.messageModule = YourMessageModule()

        IMUIManager.init(app)
        IMUIManager.pageRouter = IMExternalPageRouter()

        val mediaProvider = Provider(app)
        val mediaPreviewer = Previewer(app)
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

    fun exit(reason: String?) {
        val subscriber = object : BaseSubscriber<Boolean>() {
            override fun onNext(t: Boolean?) {
                t?.let {
                    if (it) {
                        XEventBus.post(UIEvent.UserExit.value, reason)
                    }
                }
            }
        }
        Flowable.just(true).flatMap {
            DataRepository.userData.clearUserInfo()
            IMCoreManager.shutdown()
            Flowable.just(true)
        }.compose(RxTransform.flowableToMain())
            .subscribe(subscriber)
    }
```
