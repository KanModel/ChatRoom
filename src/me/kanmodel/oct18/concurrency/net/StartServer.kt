package me.kanmodel.oct18.concurrency.net

import me.kanmodel.oct18.concurrency.util.Log
import me.kanmodel.oct18.concurrency.Main
import me.kanmodel.oct18.concurrency.gui.ChatLogRefresh
import me.kanmodel.oct18.concurrency.net.DataManager.chatHistories
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.util.ArrayList
import java.util.Vector

import javax.swing.JOptionPane

/**
 * @param port 端口
 * @description 启动服务端接收客户端的线程，仅有一个线程
 */
class StartServer @Throws(IOException::class)
constructor(private val port: Int) : Runnable {
    override fun run() {
        var s: Socket?

        try {
            serverSocket = ServerSocket(port)//启动服务端
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        Thread(ChatLogRefresh()).start()
        Log.log("刷新线程 创建完成！")
        while (flag) {//开启循环，等待接收客户端
            try {
                s = serverSocket!!.accept()//接收客户端

                userSockets.add(s)//将客户端的socket添加到容器中

                //打印客户端信息
                val id = s!!.inetAddress.hostName
                Log.log("$id 连接，当前客户端个数为：" + userSockets.size)

                //启动与客户端相对应的信息接收线程
                Log.log("为该客户端启动信息接受线程")
                Thread(ReceiveServer(s)).start()
                for (info in chatHistories) {
                    SendServer(s, info, "1")//发送聊天记录
                    Thread.sleep(10)
                }
                if (chatHistories.isNotEmpty()) {
                    SendServer(s, "---以上为未读记录---", "1")//发送聊天记录
                }

            } catch (e: IOException) {
                JOptionPane.showMessageDialog(Main.mainFrame, "服务端退出！")
            }

        }
    }

    companion object {
        val userSockets: Vector<Socket> = Vector()
        val userNames: Vector<String> = Vector()
        var serverSocket: ServerSocket? = null
        var flag = true
    }
}


