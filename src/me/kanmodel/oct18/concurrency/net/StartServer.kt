package me.kanmodel.oct18.concurrency.net

import me.kanmodel.oct18.concurrency.util.Log
import me.kanmodel.oct18.concurrency.Main
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
        var s: Socket? = null
//        userSocketList = ArrayList()//客户端端口容器
//        userNames = Vector()//用户名称容器
        //System.out.println("启动服务端");
        try {
            serverSocket = ServerSocket(port)//启动服务端
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        while (flag) {//开启循环，等待接收客户端
            try {
                s = serverSocket!!.accept()//接收客户端

                Log.log("服务端 尝试获取socket锁")
                ReceiveServer.socketListSem.acquire()
                try {
                    Log.log("服务端 得到socket锁")
                    userSocketList.add(s)//将客户端的socket添加到容器中
                } finally {
                    ReceiveServer.socketListSem.release()
                    Log.log("服务端 释放socket锁")
                }

                //打印客户端信息
                val id = s!!.inetAddress.hostName
                Log.log("$id 连接，当前客户端个数为：" + userSocketList.size)

                //启动与客户端相对应的信息接收线程
                Log.log("启动信息接受线程")
//                Thread(ReceiveServer(s, userSocketList!!, userNames!!)).start()
                Thread(ReceiveServer(s)).start()

            } catch (e: IOException) {
                JOptionPane.showMessageDialog(Main.mainFrame, "服务端退出！")
            }

        }
    }

    companion object {
        val userSocketList: ArrayList<Socket> = ArrayList()
        val userNames: Vector<String> = Vector()
        var serverSocket: ServerSocket? = null
        var flag = true
    }
}


