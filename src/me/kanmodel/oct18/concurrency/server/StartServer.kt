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

/**
 * @param port 端口
 * @description 启动服务端接收客户端的线程，仅有一个线程
 */
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
                Log.log("启动信息接受线程")
                Thread(ReceiveServer(s, userList!!, userName!!)).start()

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


