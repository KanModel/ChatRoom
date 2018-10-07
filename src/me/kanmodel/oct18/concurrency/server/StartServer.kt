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

//��������˽��տͻ��˵��߳�
//����˿ں�
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
                Thread(ReceiveServer(s, userList!!, userName!!)).start()
                Log.log("������Ϣ�����߳�")

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

//�������Ϣ�����߳�
internal class ReceiveServer(private val socket: Socket, private val userList: ArrayList<Socket>, private val userName: Vector<String>) : Runnable {

    override fun run() {
        try {
            //��ȡ��Ϣ��
            val brIn = BufferedReader(InputStreamReader(socket.getInputStream()))
            while (true) {
                val info = brIn.read().toChar()//�ȶ�ȡ��Ϣ�������ַ������ж���Ϣ����
                val line = brIn.readLine()//��ȡ��Ϣ������Ϣ����

                if (info == '1') {//1�����յ�������Ϣ
                    ChatLogPanel.textMessage.append(line + "\r\n")//����Ϣ��ӵ�����������¼��
                    //������Ϣ��ʾ����һ�У�Ҳ���ǹ�����������ĩβ����ʾ����һ���������Ϣ
                    ChatLogPanel.textMessage.setCaretPosition(ChatLogPanel.textMessage.getText().length)
                    SendServer(userList, line, "1")//����Ϣת�����ͻ���
                }

                if (info == '2') {//2�������¿ͻ��˽�������
                    Log.log("$line ��������")
                    userName.add(line)//���¿ͻ����û�����ӵ�������
                    ChatLogPanel.user.setListData(userName)//���·�����û��б�
                    SendServer(userList, userName, "2")//���û��б����ַ�������ʽ�����ͻ���
                }

                if (info == '3') {//3�������û����˳�����
                    Log.log("$line �˳�����")
                    userName.remove(line)//�Ƴ����������˳��Ŀͻ����û���
                    userList.remove(socket)//�Ƴ����������˳��Ŀͻ��˵�socket
                    ChatLogPanel.user.setListData(userName)//���·�����û��б�
                    SendServer(userList, userName, "3")//���û��б����ַ�������ʽ�����ͻ���
                    socket.close()//�رոÿͻ��˵�socket
                    break//�����ÿͻ��˶��ڵ���Ϣ�����߳�
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}

//����˷�����Ϣ
internal class SendServer @Throws(IOException::class)
constructor(userList: ArrayList<Socket>, message: Any, info: String) {
    init {
        val messages = info + message//�����Ϣͷ���
        var pwOut: PrintWriter? = null
        for (s in userList) {//����Ϣ���͸�ÿ���ͻ���
            pwOut = PrintWriter(s.getOutputStream(), true)
            pwOut.println(messages)
        }
    }
}
