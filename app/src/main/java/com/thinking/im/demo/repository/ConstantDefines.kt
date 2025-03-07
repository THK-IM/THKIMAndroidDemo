package com.thinking.im.demo.repository


enum class ContactRelation(val value: Int) {
    Stranger(1), Black(2), BeBlack(4), Fellow(8), BeFellow(16), Friend(24)
}


enum class MemberApplyStatus(val value: Int) {
    Init(0), Reject(1), Accepted(2)
}

