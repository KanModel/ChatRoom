package me.kanmodel.oct18.concurrency.server

import me.kanmodel.oct18.concurrency.Log
import me.kanmodel.oct18.concurrency.Main
import me.kanmodel.oct18.concurrency.gui.ChatLogPanel
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.ArrayList
import java.util.Vector

import javax.swing.JOptionPane

//启动服务端接收客户端的线程
//传入端口号
class StartServer @Throws(IOException::class)
constructor(private val port: Int) : Runnable {
    override fun run() {
        var s: Socket? = null
        userList = ArrayList()//客户端端口容器
        userName = Vector()//用户名称容器
        //System.out.println("启动服务端");
        try {
            serverSocket = ServerSocket(port)//启动服务端
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        while (flag) {//开启循环，等待接收客户端
            try {
                s = serverSocket!!.accept()//接收客户端
                userList!!.add(s)//将客户端的socket添加到容器中

                //打印客户端信息
                val id = s!!.inetAddress.hostName
                Log.log("$id 连接，当前客户端个数为：" + userList!!.size)

                //启动与客户端相对应的信息接收线程
                Thread(ReceiveServer(s, userList!!, userName!!)).start()
                Log.log("启动信息接受线程")

            } catch (e: IOException) {
                JOptionPane.showMessageDialog(Main.mainFrame, "服务端退出！")
            }

        }
    }

    companion object {
        var userList: ArrayList<Socket>? = null
        var userName: Vector<String>? = null
        var serverSocket: ServerSocket? = null
        var flag = true
    }
}

//服务端信息接收线程
internal class ReceiveServer(private val socket: Socket, private val userList: ArrayList<Socket>, private val userName: Vector<String>) : Runnable {

    override fun run() {
        try {
            //读取信息流
            val brIn = BufferedReader(InputStreamReader(socket.getInputStream()))
            while (true) {
                val info = brIn.read().toChar()//先读取信息流的首字符，并判断信息类型
                val line = brIn.readLine()//读取信息流的信息内容

                if (info == '1') {//1代表收到的是信息
                    ChatLogPanel.textMessage.append(line + "\r\n")//将信息添加到服务端聊天记录中
                    //设置消息显示最新一行，也就是滚动条出现在末尾，显示最新一条输入的信息
                    ChatLogPanel.textMessage.setCaretPosition(ChatLogPanel.textMessage.getText().length)
                    SendServer(userList, line, "1")//将信息转发给客户端
                }

                if (info == '2') {//2代表有新客户端建立连接
                    Log.log("$line 加入聊天")
                    userName.add(line)//将新客户端用户名添加到容器中
                    ChatLogPanel.user.setListData(userName)//更新服务端用户列表
                    SendServer(userList, userName, "2")//将用户列表以字符串的形式发给客户端
                }

                if (info == '3') {//3代表有用户端退出连接
                    Log.log("$line 退出聊天")
                    userName.remove(line)//移除容器中已退出的客户端用户名
                    userList.remove(socket)//移除容器中已退出的客户端的socket
                    ChatLogPanel.user.setListData(userName)//更新服务端用户列表
                    SendServer(userList, userName, "3")//将用户列表以字符串的形式发给客户端
                    socket.close()//关闭该客户端的socket
                    break//结束该客户端对于的信息接收线程
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}

//服务端发送信息
internal class SendServer @Throws(IOException::class)
constructor(userList: ArrayList<Socket>, message: Any, info: String) {
    init {
        val messages = info + message//添加信息头标记
        var pwOut: PrintWriter? = null
        for (s in userList) {//将信息发送给每个客户端
            pwOut = PrintWriter(s.getOutputStream(), true)
            pwOut.println(messages)
        }
    }
}
