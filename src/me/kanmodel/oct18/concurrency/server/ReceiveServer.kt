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
 * �������Ϣ�����̣߳�Ϊÿһ���ͻ��˵�Socket����һ���߳�
 */
class ReceiveServer(private val socket: Socket, private val userList: ArrayList<Socket>, private val userName: Vector<String>) : Runnable {
    var clientName: String = ""

    override fun run() {
        try {
            //��ȡ��Ϣ��
            val brIn = BufferedReader(InputStreamReader(socket.getInputStream()))
            while (true) {
                val info = brIn.read().toChar()//�ȶ�ȡ��Ϣ�������ַ������ж���Ϣ����
                val line = brIn.readLine()//��ȡ��Ϣ������Ϣ����

                if (clientName == "") {
                    Log.log("�����û����Ի�ȡ��")
                } else {
                    Log.log("�û� $clientName ���Ի�ȡ��")
                }
                lock.lock()
                try {
                    if (info == '1') {//1�����յ�������Ϣ
                        ChatLogPanel.textMessage.append(line + "\r\n")//����Ϣ��ӵ�����������¼��
                        //������Ϣ��ʾ����һ�У�Ҳ���ǹ�����������ĩβ����ʾ����һ���������Ϣ
                        ChatLogPanel.textMessage.caretPosition = ChatLogPanel.textMessage.text.length
                        Log.log("�û� $clientName ������Ϣ")
                        SendServer(userList, line, "1")//����Ϣת�����ͻ���
                    }
                    if (info == '2') {//2�������¿ͻ��˽�������
                        Log.log("$line ��������")
                        userName.add(line)//���¿ͻ����û�����ӵ�������
                        clientName = line
                        Log.log("�� $clientName ��������")
                        ChatLogPanel.user.setListData(userName)//���·�����û��б�
                        SendServer(userList, userName, "2")//���û��б����ַ�������ʽ�����ͻ���
                    }
                    if (info == '3') {//3�������û����˳�����
                        Log.log("�û� $line �˳�����")
                        userName.remove(line)//�Ƴ����������˳��Ŀͻ����û���
                        userList.remove(socket)//�Ƴ����������˳��Ŀͻ��˵�socket
                        ChatLogPanel.user.setListData(userName)//���·�����û��б�
                        SendServer(userList, userName, "3")//���û��б����ַ�������ʽ�����ͻ���
                        socket.close()//�رոÿͻ��˵�socket
                        break//�����ÿͻ��˶��ڵ���Ϣ�����߳�
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    lock.unlock()
                    Log.log("�û� $clientName �ͷ���")
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