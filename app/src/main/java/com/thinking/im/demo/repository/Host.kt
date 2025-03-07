package com.thinking.im.demo.repository



object Host {

    fun endpointFor(type: String): String {
        val debug = true
        if (debug) {
            return when (type) {
                "user" -> {
                    "http://206.238.114.18:20000"
                }

                "msg" -> {
                    "http://206.238.114.18:20000"
                }

                "websocket" -> {
                    "ws://206.238.114.18:21000/ws"
                }

                else -> ""
            }
        } else {
            return when (type) {
                "user" -> {
                    "http://206.238.114.18:20000"
                }

                "msg" -> {
                    "http://206.238.114.18:21000"
                }

                "websocket" -> {
                    "ws://206.238.114.18:20000/ws"
                }

                else -> ""
            }
        }
    }

}