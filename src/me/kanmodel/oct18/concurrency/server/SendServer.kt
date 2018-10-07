package me.kanmodel.oct18.concurrency.server

import java.io.IOException
import java.io.PrintWriter
import java.net.Socket
import java.util.ArrayList

/**
 * 服务端发送信息
 */
class SendServer @Throws(IOException::class)
constructor(message: Any, info: String) {
    init {
        val messages = info + message//添加信息头标记
        var pwOut: PrintWriter? = null

//        ReceiveServer.socketListSem.acquire()
        try {
            for (s in StartServer.userSocketList) {//将信息发送给每个客户端
                pwOut = PrintWriter(s.getOutputStream(), true)
                pwOut.println(messages)
            }
        }finally {
//            ReceiveServer.socketListSem.release()
        }
    }
}