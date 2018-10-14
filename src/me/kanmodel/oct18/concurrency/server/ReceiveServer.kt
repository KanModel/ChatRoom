package me.kanmodel.oct18.concurrency.server

import me.kanmodel.oct18.concurrency.util.Log
import me.kanmodel.oct18.concurrency.gui.ChatLogPanel
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.net.Socket
import java.util.concurrent.Semaphore

/**
 * �������Ϣ�����̣߳�Ϊÿһ���ͻ��˵�Socket����һ���߳�
 */
class ReceiveServer(private val socket: Socket) : Runnable {
    var clientName: String = ""//��¼�ͻ�������

    override fun run() {
        try {
            //��ȡ��Ϣ��
            var flag = true
            val brIn = BufferedReader(InputStreamReader(socket.getInputStream()))//ͨ�����淽ʽ��ȡ��Ϣ���е�����

            while (flag) {
                if (!StartServer.flag) {
                    throw InterruptedException()//�׳��ж��쳣�����������ر�ʱ�ж�������Ϣ�����߳�
                }

                val info = brIn.read().toChar()//�ȶ�ȡ��Ϣ�������ַ��������ж���Ϣ����
                val line = brIn.readLine()//��ȡ��Ϣ������Ϣ����

                when (info) {//1�����յ�������Ϣ
                    '1' -> {
                        if (clientName == "") {
                            Log.log("�����û����Ի�ȡchatLog��")
                        } else {
                            Log.log("�û� $clientName ���Ի�ȡchatLog��")
                        }
                        chatLogSem.acquire()
                        try {
                            Log.log("�û� $clientName �õ�chatLog��")
                            ChatLogPanel.textMessage.append(line + "\r\n")//����Ϣ��ӵ�����������¼��
                            ChatLogPanel.textMessage.caretPosition = ChatLogPanel.textMessage.text.length//������Ϣ��ʾ����һ�У�Ҳ���ǹ�����������ĩβ����ʾ����һ���������Ϣ
                            Log.log("�û� $clientName ������Ϣ")

//                        socketListSem.acquire()
//                        try {
//                        SendServer(userSocketList, line, "1")//����Ϣת�����ͻ���
                            SendServer(line, "1")//����Ϣת�����ͻ���
//                        } finally {
//                            socketListSem.release()
//                        }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            chatLogSem.release()
                            Log.log("�û� $clientName �ͷ�chatLog��")
                        }
                    }
                    '2' -> {//2�������¿ͻ��˽�������
                        Log.log("$line ��������")
                        clientName = line
                        Log.log("�� $clientName ��������")

                        Log.log("�û� $clientName ���Ի�ȡname��")
                        nameListSem.acquire()
                        socketListSem.acquire()
                        try {
                            Log.log("�û� $clientName �õ�name��")
                            StartServer.userNames.add(line)//���¿ͻ����û�����ӵ�������
                            ChatLogPanel.userList.setListData(StartServer.userNames)//���·�����û��б�
                            SendServer(StartServer.userNames, "2")//���û��б����ַ�������ʽ�����ͻ���
                        } finally {
                            nameListSem.release()
                            socketListSem.release()
                            Log.log("�û� $clientName �ͷ�name��")
                        }
                    }
                    '3' -> {//3�������û����˳�����
                        Log.log("�û� $line �˳�����")

                        nameListSem.acquire()
                        socketListSem.acquire()
                        try {
                            StartServer.userNames.remove(line)//�Ƴ����������˳��Ŀͻ����û���
                            StartServer.userSocketList.remove(socket)//�Ƴ����������˳��Ŀͻ��˵�socket
                            ChatLogPanel.userList.setListData(StartServer.userNames)//���·�����û��б�
                            SendServer(StartServer.userNames, "3")//���û��б����ַ�������ʽ�����ͻ���
                        } finally {
                            nameListSem.release()
                            socketListSem.release()
                        }

                        socket.close()//�رոÿͻ��˵�socket
                        throw InterruptedException()
//                    flag = false
                    }
                }

            }
            Log.log("�û� $clientName �Ͽ�����")
        } catch (e: InterruptedException) {
            Log.log("�û� $clientName �ж�")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        //        val lock: ReentrantLock = ReentrantLock(true)
        val chatLogSem = Semaphore(1, true)
        val socketListSem = Semaphore(1, true)
        val nameListSem = Semaphore(1, true)

//        val userNames: Vector<String>
    }
}