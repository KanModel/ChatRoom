package me.kanmodel.oct18.concurrency.net

import me.kanmodel.oct18.concurrency.util.Log
import me.kanmodel.oct18.concurrency.Main
import me.kanmodel.oct18.concurrency.gui.ChatLogRefresh
import me.kanmodel.oct18.concurrency.net.DataManager.chatHistories
import me.kanmodel.oct18.concurrency.net.DataManager.socketsMutex
import me.kanmodel.oct18.concurrency.net.DataManager.userSockets
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.util.Vector
import java.util.concurrent.Executors

import javax.swing.JOptionPane
import kotlin.math.max

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
        val exec = Executors.newCachedThreadPool()//接受信息线程池创建
        while (flag) {//开启循环，等待接收客户端
            try {
                s = serverSocket!!.accept()//接收客户端
                s.tcpNoDelay = true
                val id = s!!.inetAddress.hostName
                socketsMutex.acquire()
                try {
                    userSockets.add(s)//将客户端的socket添加到容器中
                }finally {
                    socketsMutex.release()
                    Log.log("来自 $id 连接，当前连接数：${userSockets.size}")
                }

                //打印客户端信息

                //启动与客户端相对应的信息接收线程
                Log.log("为该连接启动信息接受线程")
                exec.execute(ServerReceiver(s))
//                Thread(ServerReceiver(s)).start()
                if (chatHistories.isNotEmpty()) {
                    for (i in max(chatHistories.size - 11, 0) until chatHistories.size) {
                        ServerSender(s, chatHistories[i], "1")//发送聊天记录
//                        Thread.sleep(10)
                    }
                    ServerSender(s, "---以上为未读记录---", "1")//发送聊天记录
                }

            } catch (e: IOException) {
                JOptionPane.showMessageDialog(Main.mainFrame, "服务器关闭！")
                exec.shutdown()
            }
        }
    }

    companion object {
        val userNames: Vector<String> = Vector()
        var serverSocket: ServerSocket? = null
        var flag = true
    }
}


