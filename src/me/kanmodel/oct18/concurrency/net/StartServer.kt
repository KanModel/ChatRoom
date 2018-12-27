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
import java.util.ArrayList
import java.util.Vector

import javax.swing.JOptionPane
import kotlin.math.max

/**
 * @param port �˿�
 * @description ��������˽��տͻ��˵��̣߳�����һ���߳�
 */
class StartServer @Throws(IOException::class)
constructor(private val port: Int) : Runnable {
    override fun run() {
        var s: Socket?

        try {
            serverSocket = ServerSocket(port)//���������
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        Thread(ChatLogRefresh()).start()
        Log.log("ˢ���߳� ������ɣ�")
        while (flag) {//����ѭ�����ȴ����տͻ���
            try {
                s = serverSocket!!.accept()//���տͻ���
                s.tcpNoDelay = true
                val id = s!!.inetAddress.hostName
                socketsMutex.acquire()
                try {
                    userSockets.add(s)//���ͻ��˵�socket��ӵ�������
                }finally {
                    socketsMutex.release()
                    Log.log("���� $id ���ӣ���ǰ��������${userSockets.size}")
                }

                //��ӡ�ͻ�����Ϣ

                //������ͻ������Ӧ����Ϣ�����߳�
                Log.log("Ϊ������������Ϣ�����߳�")
                Thread(ReceiveServer(s)).start()
                if (chatHistories.isNotEmpty()) {
                    for (i in max(chatHistories.size - 11, 0) until chatHistories.size) {
                        SendServer(s, chatHistories[i], "1")//���������¼
//                        Thread.sleep(10)
                    }
                    SendServer(s, "---����Ϊδ����¼---", "1")//���������¼
                }

            } catch (e: IOException) {
                JOptionPane.showMessageDialog(Main.mainFrame, "�������رգ�")
            }
        }
    }

    companion object {
        val userNames: Vector<String> = Vector()
        var serverSocket: ServerSocket? = null
        var flag = true
    }
}


