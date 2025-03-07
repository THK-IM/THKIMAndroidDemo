package com.thinking.im.demo.module.im

enum class IMExternalMsgType(val value: Int) {
    GroupMemberApply(20),
    GroupUpdateTextMsg(21),
    SystemMsg(30),
    MomentAssistant(31),
    ShareGroup(42)
}


enum class ExtendSessionType(val value: Int) {
    Plot(5)
}
