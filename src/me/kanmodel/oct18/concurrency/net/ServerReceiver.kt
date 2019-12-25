package me.kanmodel.oct18.concurrency.net

import me.kanmodel.oct18.concurrency.util.Log
import me.kanmodel.oct18.concurrency.gui.ChatLogPanel
import me.kanmodel.oct18.concurrency.net.DataManager.CHAT_MUTEX
import me.kanmodel.oct18.concurrency.net.DataManager.LIST_MUTEX
import me.kanmodel.oct18.concurrency.net.DataManager.chatHistories
import me.kanmodel.oct18.concurrency.net.DataManager.chatMutex
import me.kanmodel.oct18.concurrency.net.DataManager.chatQueue
import me.kanmodel.oct18.concurrency.net.DataManager.listMutex
import me.kanmodel.oct18.concurrency.net.DataManager.notEmpty
import me.kanmodel.oct18.concurrency.net.DataManager.socketsMutex
import me.kanmodel.oct18.concurrency.net.DataManager.userSockets
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.IllegalStateException
import java.net.Socket
import java.net.SocketException
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.SwingUtilities

/**
 * 服务端信息接收线程，为每一个客户端的Socket建立一个线程
 */
class ServerReceiver(private val socket: Socket) : Runnable {
    var clientName: String = ""//记录客户端名字
    var proofTemp: String = ""

    override fun run() {
        try {
            //读取信息流
            val brIn = BufferedReader(InputStreamReader(socket.getInputStream()))//通过缓存方式读取信息流中的内容

            while (StartServer.flag) {
                if (!StartServer.flag) {
                    throw InterruptedException()//抛出中断异常，当服务器关闭时中断所有信息接收线程
                }

                var info: Char
                var line: String
                try {
                    info = brIn.read().toChar()//先读取信息流的首字符，用于判断信息类型
                    line = brIn.readLine()//读取信息流的信息内容
                } catch (e: SocketException) {
                    throw InterruptedException()//抛出中断结束本线程
                } catch (ei: IllegalStateException) {
                    throw InterruptedException()//抛出中断结束本线程
                }

                when (info) {
                    '1' -> {//1代表文本信息
                        if (clientName == "") {
                            Log.log("对新用户尝试获取$CHAT_MUTEX")
                        } else {
                            Log.log("线程 $clientName 尝试获取$CHAT_MUTEX")
                        }
                        chatMutex.acquire()
                        try {
                            Log.log("线程 $clientName 得到$CHAT_MUTEX")
                            line = "${SimpleDateFormat("HH:mm:ss").format(Date())} [$clientName]:$line"
                            chatQueue.offer(line)
                            notEmpty.release()
                            chatHistories.add(line)
//                            Log.log("线程 $clientName 发送信息")
                            ServerSender(line, "1")//将信息转发给客户端
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            chatMutex.release()
                            Log.log("线程 $clientName 释放$CHAT_MUTEX")
                        }
                    }
                    '5' -> {//5代表图片消息
                        if (clientName == "") {
                            Log.log("对新用户尝试获取$CHAT_MUTEX")
                        } else {
                            Log.log("线程 $clientName 尝试获取$CHAT_MUTEX")
                        }
                        chatMutex.acquire()
                        try {
                            Log.log("线程 $clientName 得到$CHAT_MUTEX")
                            val pre = "${SimpleDateFormat("HH:mm:ss").format(Date())} [$clientName]:"
                            chatQueue.offer(pre)
                            chatQueue.offer(line)
                            notEmpty.release()
                            chatHistories.add(pre)
                            chatHistories.add(line)
                            ServerSender(pre, "1")//将信息转发给客户端
                            ServerSender(line, "5")//将信息转发给客户端
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            chatMutex.release()
                            Log.log("线程 $clientName 释放$CHAT_MUTEX")
                        }
                    }
                    '2' -> {//2代表有新客户端建立连接
                        Log.log("$line 加入聊天")
                        clientName = line
                        Log.log("与 $clientName 建立连接")

                        Log.log("线程 $clientName 尝试获取$LIST_MUTEX")
                        listMutex.acquire()
                        try {
                            Log.log("线程 $clientName 得到$LIST_MUTEX")
                            SwingUtilities.invokeLater {
                                StartServer.userNames.add(line)//将新客户端用户名添加到容器中
                                ChatLogPanel.userJL.setListData(StartServer.userNames)//更新服务端用户列表
                                ServerSender(StartServer.userNames, "2")//将用户列表以字符串的形式发给客户端
                            }
                        } finally {
                            listMutex.release()
                            Log.log("线程 $clientName 释放$LIST_MUTEX")
                        }
                    }
                    '3' -> {//3代表用户端退出连接
                        proofTemp = line
                        throw InterruptedException()//抛出中断结束本线程
                    }
                }
            }
            Log.log("线程 $clientName 作业完毕")
        } catch (e: InterruptedException) {
            Log.log("线程 $clientName 中断退出")

//            if (clientName != proofTemp) Log.log("$clientName != $proofTemp 退出用户名不一致")
            Log.log("线程 $clientName 尝试获取$LIST_MUTEX")
            listMutex.acquire()
            try {
                Log.log("线程 $clientName 得到$LIST_MUTEX")
                SwingUtilities.invokeLater {
                    StartServer.userNames.remove(clientName)//移除容器中已退出的客户端用户名
                    ChatLogPanel.userJL.setListData(StartServer.userNames)//更新服务端用户列表
                    ServerSender(StartServer.userNames, "3")//将用户列表以字符串的形式发给客户端
                }
            } finally {
                listMutex.release()
                Log.log("线程 $clientName 释放$LIST_MUTEX")
            }

            socketsMutex.acquire()
            try {
                userSockets.remove(socket)//移除容器中已退出的客户端的socket
                socket.close()//关闭该客户端的socket
            } finally {
                socketsMutex.release()
                Log.log("连接 $clientName 断开, 当前连接数：${userSockets.size}")
                Log.log("线程 $clientName 结束")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}