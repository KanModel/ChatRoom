package me.kanmodel.oct18.concurrency.net

import java.io.IOException
import java.io.PrintWriter
import java.net.Socket

/**
 * description: 给所有客户端发送数据（标记+信息）
 * @param message 信息
 * @param info 标记
 */
class SendServer @Throws(IOException::class)
constructor() {

    constructor(message: Any, info: String) : this() {
        val messages = info + message//添加信息头标记
        var pwOut: PrintWriter?

        for (s in StartServer.userSockets) {//将信息发送给每个客户端
            pwOut = PrintWriter(s.getOutputStream(), true)
            pwOut.println(messages)
        }
    }

    constructor(socket: Socket, message: Any, info: String) : this() {
        val messages = info + message//添加信息头标记

        val pwOut = PrintWriter(socket.getOutputStream(), true)
        pwOut.println(messages)
    }
}