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
 * @param port �˿�
 * @description ��������˽��տͻ��˵��̣߳�����һ���߳�
 */
class StartServer @Throws(IOException::class)
constructor(private val port: Int) : Runnable {
    override fun run() {
        var s: Socket? = null
        userList = ArrayList()//�ͻ��˶˿�����
        userName = Vector()//�û���������
        //System.out.println("���������");
        try {
            serverSocket = ServerSocket(port)//���������
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        while (flag) {//����ѭ�����ȴ����տͻ���
            try {
                s = serverSocket!!.accept()//���տͻ���
                userList!!.add(s)//���ͻ��˵�socket��ӵ�������

                //��ӡ�ͻ�����Ϣ
                val id = s!!.inetAddress.hostName
                Log.log("$id ���ӣ���ǰ�ͻ��˸���Ϊ��" + userList!!.size)

                //������ͻ������Ӧ����Ϣ�����߳�
                Log.log("������Ϣ�����߳�")
                Thread(ReceiveServer(s, userList!!, userName!!)).start()

            } catch (e: IOException) {
                JOptionPane.showMessageDialog(Main.mainFrame, "������˳���")
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


