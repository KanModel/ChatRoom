package me.kanmodel.oct18.concurrency.server

import me.kanmodel.oct18.concurrency.Log
import me.kanmodel.oct18.concurrency.Main
import java.io.IOException
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
//        userSocketList = ArrayList()//�ͻ��˶˿�����
//        userNames = Vector()//�û���������
        //System.out.println("���������");
        try {
            serverSocket = ServerSocket(port)//���������
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        while (flag) {//����ѭ�����ȴ����տͻ���
            try {
                s = serverSocket!!.accept()//���տͻ���

                Log.log("����� ���Ի�ȡsocket��")
                ReceiveServer.socketListSem.acquire()
                try {
                    Log.log("����� �õ�socket��")
                    userSocketList.add(s)//���ͻ��˵�socket��ӵ�������
                } finally {
                    ReceiveServer.socketListSem.release()
                    Log.log("����� �ͷ�socket��")
                }

                //��ӡ�ͻ�����Ϣ
                val id = s!!.inetAddress.hostName
                Log.log("$id ���ӣ���ǰ�ͻ��˸���Ϊ��" + userSocketList.size)

                //������ͻ������Ӧ����Ϣ�����߳�
                Log.log("������Ϣ�����߳�")
//                Thread(ReceiveServer(s, userSocketList!!, userNames!!)).start()
                Thread(ReceiveServer(s)).start()

            } catch (e: IOException) {
                JOptionPane.showMessageDialog(Main.mainFrame, "������˳���")
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


