package client

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: kgdwhsk
 * Date: 2018-10-05
 * Time: 20:08
 */
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.net.UnknownHostException

import javax.swing.JOptionPane

//启动客户端接收线程
class StartClient @Throws(UnknownHostException::class, IOException::class)
constructor(s: Socket) {
    init {
        Thread(ReceiveClient(s)).start()
    }
}

//客户端接收线程
internal class ReceiveClient(private val s: Socket) : Runnable {
    override fun run() {
        try {
            //信息接收流
            val brIn = BufferedReader(InputStreamReader(s.getInputStream()))
            while (true) {
                val info = brIn.read().toChar()//读取信息流首字符，判断信息类型
//                var temp = brIn.readLine()
                val line = brIn.readLine()//读取信息流内容
//                val temp = brIn.lines().limit(3).toArray()
//                var line = ""
//                for (t in temp) {
//                    line += "$t\r\n"
//                }

                if (info == '1') {//代表发送的是消息
                    if (line != "") {
                        WindowClient.textMessage.append(line + "\r\n")    //将消息添加到文本域中
                        //设置消息显示最新一行，也就是滚动条出现在末尾，显示最新一条输入的信息
                        WindowClient.textMessage.caretPosition = WindowClient.textMessage.text.length
                    }
                }

                if (info == '2' || info == '3') {//有新用户加入或退出，2为加入，3为退出
                    val sub = line.substring(1, line.length - 1)//去掉字符串头尾中括号
                    val data = sub.split(", ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()//分割姓名列表
                    WindowClient.user.clearSelection()
                    WindowClient.user.setListData(data)
                }

                if (info == '4') {//4代表服务端退出
                    WindowClient.link.text = "连接"
                    WindowClient.exit.text = "已退出"
                    WindowClient.socket!!.close()
                    WindowClient.socket = null
                    break
                }
            }
        } catch (e: IOException) {
            JOptionPane.showMessageDialog(WindowClient.window, "客户端已退出连接")
        }

    }
}

//客户端发送信息类
internal class SendClient @Throws(IOException::class)
constructor(s: Socket, message: Any, info: String) {
    init {
        val messages = info + message
        val pwOut = PrintWriter(s.getOutputStream(), true)
        pwOut.println(messages)
    }
}


