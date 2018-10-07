package me.kanmodel.oct18.concurrency.server

import me.kanmodel.oct18.concurrency.Log
import me.kanmodel.oct18.concurrency.gui.ChatLogPanel
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.net.Socket
import java.util.*
import java.util.concurrent.locks.ReentrantLock

/**
 * 服务端信息接收线程，为每一个客户端的Socket建立一个线程
 */
class ReceiveServer(private val socket: Socket, private val userList: ArrayList<Socket>, private val userName: Vector<String>) : Runnable {
    var clientName: String = ""

    override fun run() {
        try {
            //读取信息流
            val brIn = BufferedReader(InputStreamReader(socket.getInputStream()))
            while (true) {
                val info = brIn.read().toChar()//先读取信息流的首字符，并判断信息类型
                val line = brIn.readLine()//读取信息流的信息内容

                if (clientName == "") {
                    Log.log("对新用户尝试获取锁")
                } else {
                    Log.log("用户 $clientName 尝试获取锁")
                }
                lock.lock()
                try {
                    if (info == '1') {//1代表收到的是信息
                        ChatLogPanel.textMessage.append(line + "\r\n")//将信息添加到服务端聊天记录中
                        //设置消息显示最新一行，也就是滚动条出现在末尾，显示最新一条输入的信息
                        ChatLogPanel.textMessage.caretPosition = ChatLogPanel.textMessage.text.length
                        Log.log("用户 $clientName 发送信息")
                        SendServer(userList, line, "1")//将信息转发给客户端
                    }
                    if (info == '2') {//2代表有新客户端建立连接
                        Log.log("$line 加入聊天")
                        userName.add(line)//将新客户端用户名添加到容器中
                        clientName = line
                        Log.log("与 $clientName 建立连接")
                        ChatLogPanel.user.setListData(userName)//更新服务端用户列表
                        SendServer(userList, userName, "2")//将用户列表以字符串的形式发给客户端
                    }
                    if (info == '3') {//3代表有用户端退出连接
                        Log.log("用户 $line 退出聊天")
                        userName.remove(line)//移除容器中已退出的客户端用户名
                        userList.remove(socket)//移除容器中已退出的客户端的socket
                        ChatLogPanel.user.setListData(userName)//更新服务端用户列表
                        SendServer(userList, userName, "3")//将用户列表以字符串的形式发给客户端
                        socket.close()//关闭该客户端的socket
                        break//结束该客户端对于的信息接收线程
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    lock.unlock()
                    Log.log("用户 $clientName 释放锁")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        val lock: ReentrantLock = ReentrantLock(true)
    }
}