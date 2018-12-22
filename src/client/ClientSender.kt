package client

import java.io.IOException
import java.io.PrintWriter
import java.net.Socket

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: KanModel
 * Date: 2018-12-21-23:31
 */
//客户端发送信息类
class ClientSender @Throws(IOException::class)
constructor(s: Socket, message: Any, info: String) {
    init {
        val messages = info + message
        val pwOut = PrintWriter(s.getOutputStream(), true)
        pwOut.println(messages)
//        pwOut.close()
    }
}