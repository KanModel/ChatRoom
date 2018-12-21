package me.kanmodel.oct18.concurrency.net

import me.kanmodel.oct18.concurrency.util.Log
import me.kanmodel.oct18.concurrency.gui.ChatLogPanel
import me.kanmodel.oct18.concurrency.net.DataManager.CHAT_LOCKER
import me.kanmodel.oct18.concurrency.net.DataManager.chatHistories
import me.kanmodel.oct18.concurrency.net.DataManager.chatMutex
import me.kanmodel.oct18.concurrency.net.DataManager.chatQueue
import me.kanmodel.oct18.concurrency.net.DataManager.notEmpty
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.net.Socket
import java.util.concurrent.Semaphore

/**
 * 服务端信息接收线程，为每一个客户端的Socket建立一个线程
 */
class ReceiveServer(private val socket: Socket) : Runnable {
    var clientName: String = ""//记录客户端名字

    override fun run() {
        try {
            //读取信息流
            val flag = true
            val brIn = BufferedReader(InputStreamReader(socket.getInputStream()))//通过缓存方式读取信息流中的内容

            while (flag) {
                if (!StartServer.flag) {
                    throw InterruptedException()//抛出中断异常，当服务器关闭时中断所有信息接收线程
                }

                val info = brIn.read().toChar()//先读取信息流的首字符，用于判断信息类型
                val line = brIn.readLine()//读取信息流的信息内容

                when (info) {//1代表收到的是信息
                    '1' -> {
                        if (clientName == "") {
                            Log.log("对新用户尝试获取$CHAT_LOCKER")
                        } else {
                            Log.log("线程 $clientName 尝试获取$CHAT_LOCKER")
                        }
                        chatMutex.acquire()
                        try {
                            Log.log("线程 $clientName 得到$CHAT_LOCKER")
                            chatQueue.offer(line)
                            notEmpty.release()
                            chatHistories.add(line)
                            Log.log("线程 $clientName 发送信息")
                            SendServer(line, "1")//将信息转发给客户端
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            chatMutex.release()
                            Log.log("线程 $clientName 释放$CHAT_LOCKER")
                        }
                    }
                    '2' -> {//2代表有新客户端建立连接
                        Log.log("$line 加入聊天")
                        clientName = line
                        Log.log("与 $clientName 建立连接")

                        StartServer.userNames.add(line)//将新客户端用户名添加到容器中
                        ChatLogPanel.userJL.setListData(StartServer.userNames)//更新服务端用户列表
                        SendServer(StartServer.userNames, "2")//将用户列表以字符串的形式发给客户端
                    }
                    '3' -> {//3代表有用户端退出连接
                        Log.log("线程 $line 退出聊天")

                        StartServer.userNames.remove(line)//移除容器中已退出的客户端用户名
                        StartServer.userSockets.remove(socket)//移除容器中已退出的客户端的socket
                        ChatLogPanel.userJL.setListData(StartServer.userNames)//更新服务端用户列表
                        SendServer(StartServer.userNames, "3")//将用户列表以字符串的形式发给客户端

                        socket.close()//关闭该客户端的socket
                        throw InterruptedException()//抛出中断结束本线程
                    }
                }
            }
            Log.log("线程 $clientName 作业完毕")
        } catch (e: InterruptedException) {
            Log.log("线程 $clientName 中断退出")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}