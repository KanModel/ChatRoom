package me.kanmodel.oct18.concurrency.net

import me.kanmodel.oct18.concurrency.util.Log
import me.kanmodel.oct18.concurrency.gui.ChatLogPanel
import me.kanmodel.oct18.concurrency.net.LockerManager.CHAT_LOCKER
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
                            Log.log("�����û����Ի�ȡ$CHAT_LOCKER")
                        } else {
                            Log.log("�߳� $clientName ���Ի�ȡ$CHAT_LOCKER")
                        }
                        chatLogSem.acquire()
                        try {
                            Log.log("�߳� $clientName �õ�$CHAT_LOCKER")
                            ChatLogPanel.textMessage.append(line + "\r\n")//����Ϣ��ӵ�����������¼��
                            ChatLogPanel.chatLogger.add(line)
                            ChatLogPanel.textMessage.caretPosition = ChatLogPanel.textMessage.text.length//������Ϣ��ʾ����һ�У�Ҳ���ǹ�����������ĩβ����ʾ����һ���������Ϣ
                            Log.log("�߳� $clientName ������Ϣ")

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
                            Log.log("�߳� $clientName �ͷ�$CHAT_LOCKER")
                        }
                    }
                    '2' -> {//2�������¿ͻ��˽�������
                        Log.log("$line ��������")
                        clientName = line
                        Log.log("�� $clientName ��������")

                        Log.log("�߳� $clientName ���Ի�ȡname��")
                        nameListSem.acquire()
                        socketListSem.acquire()
                        try {
                            Log.log("�߳� $clientName �õ�name��")
                            StartServer.userNames.add(line)//���¿ͻ����û�����ӵ�������
                            ChatLogPanel.userList.setListData(StartServer.userNames)//���·�����û��б�
                            SendServer(StartServer.userNames, "2")//���û��б����ַ�������ʽ�����ͻ���
                        } finally {
                            socketListSem.release()
                            nameListSem.release()
                            Log.log("�߳� $clientName �ͷ�name��")
                        }
                    }
                    '3' -> {//3�������û����˳�����
                        Log.log("�߳� $line �˳�����")

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
            Log.log("�߳� $clientName ��ҵ���")
        } catch (e: InterruptedException) {
            Log.log("�߳� $clientName �ж��˳�")
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