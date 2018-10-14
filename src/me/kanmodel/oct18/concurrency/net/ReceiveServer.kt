package me.kanmodel.oct18.concurrency.net

import me.kanmodel.oct18.concurrency.util.Log
import me.kanmodel.oct18.concurrency.gui.ChatLogPanel
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
            var flag = true
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
                            Log.log("对新用户尝试获取chatLog锁")
                        } else {
                            Log.log("用户 $clientName 尝试获取chatLog锁")
                        }
                        chatLogSem.acquire()
                        try {
                            Log.log("用户 $clientName 得到chatLog锁")
                            ChatLogPanel.textMessage.append(line + "\r\n")//将信息添加到服务端聊天记录中
                            ChatLogPanel.textMessage.caretPosition = ChatLogPanel.textMessage.text.length//设置消息显示最新一行，也就是滚动条出现在末尾，显示最新一条输入的信息
                            Log.log("用户 $clientName 发送信息")

//                        socketListSem.acquire()
//                        try {
//                        SendServer(userSocketList, line, "1")//将信息转发给客户端
                            SendServer(line, "1")//将信息转发给客户端
//                        } finally {
//                            socketListSem.release()
//                        }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            chatLogSem.release()
                            Log.log("用户 $clientName 释放chatLog锁")
                        }
                    }
                    '2' -> {//2代表有新客户端建立连接
                        Log.log("$line 加入聊天")
                        clientName = line
                        Log.log("与 $clientName 建立连接")

                        Log.log("用户 $clientName 尝试获取name锁")
                        nameListSem.acquire()
                        socketListSem.acquire()
                        try {
                            Log.log("用户 $clientName 得到name锁")
                            StartServer.userNames.add(line)//将新客户端用户名添加到容器中
                            ChatLogPanel.userList.setListData(StartServer.userNames)//更新服务端用户列表
                            SendServer(StartServer.userNames, "2")//将用户列表以字符串的形式发给客户端
                        } finally {
                            nameListSem.release()
                            socketListSem.release()
                            Log.log("用户 $clientName 释放name锁")
                        }
                    }
                    '3' -> {//3代表有用户端退出连接
                        Log.log("用户 $line 退出聊天")

                        nameListSem.acquire()
                        socketListSem.acquire()
                        try {
                            StartServer.userNames.remove(line)//移除容器中已退出的客户端用户名
                            StartServer.userSocketList.remove(socket)//移除容器中已退出的客户端的socket
                            ChatLogPanel.userList.setListData(StartServer.userNames)//更新服务端用户列表
                            SendServer(StartServer.userNames, "3")//将用户列表以字符串的形式发给客户端
                        } finally {
                            nameListSem.release()
                            socketListSem.release()
                        }

                        socket.close()//关闭该客户端的socket
                        throw InterruptedException()
//                    flag = false
                    }
                }

            }
            Log.log("用户 $clientName 断开连接")
        } catch (e: InterruptedException) {
            Log.log("用户 $clientName 中断")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        //        val lock: ReentrantLock = ReentrantLock(true)
        val chatLogSem = Semaphore(1, true)
        val socketListSem = Semaphore(1, true)
        val nameListSem = Semaphore(1, true)

//        val userNames: Vector<String>
    }
}