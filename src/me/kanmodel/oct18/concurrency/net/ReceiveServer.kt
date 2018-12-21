package me.kanmodel.oct18.concurrency.net

import me.kanmodel.oct18.concurrency.util.Log
import me.kanmodel.oct18.concurrency.gui.ChatLogPanel
import me.kanmodel.oct18.concurrency.net.DataManager.CHAT_LOCKER
import me.kanmodel.oct18.concurrency.net.DataManager.chatHistories
import me.kanmodel.oct18.concurrency.net.DataManager.chatMutex
import me.kanmodel.oct18.concurrency.net.DataManager.chatQueue
import me.kanmodel.oct18.concurrency.net.DataManager.notEmpty
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
            val flag = true
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
                        chatMutex.acquire()
                        try {
                            Log.log("�߳� $clientName �õ�$CHAT_LOCKER")
                            chatQueue.offer(line)
                            notEmpty.release()
                            chatHistories.add(line)
                            Log.log("�߳� $clientName ������Ϣ")
                            SendServer(line, "1")//����Ϣת�����ͻ���
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            chatMutex.release()
                            Log.log("�߳� $clientName �ͷ�$CHAT_LOCKER")
                        }
                    }
                    '2' -> {//2�������¿ͻ��˽�������
                        Log.log("$line ��������")
                        clientName = line
                        Log.log("�� $clientName ��������")

                        StartServer.userNames.add(line)//���¿ͻ����û�����ӵ�������
                        ChatLogPanel.userJL.setListData(StartServer.userNames)//���·�����û��б�
                        SendServer(StartServer.userNames, "2")//���û��б����ַ�������ʽ�����ͻ���
                    }
                    '3' -> {//3�������û����˳�����
                        Log.log("�߳� $line �˳�����")

                        StartServer.userNames.remove(line)//�Ƴ����������˳��Ŀͻ����û���
                        StartServer.userSockets.remove(socket)//�Ƴ����������˳��Ŀͻ��˵�socket
                        ChatLogPanel.userJL.setListData(StartServer.userNames)//���·�����û��б�
                        SendServer(StartServer.userNames, "3")//���û��б����ַ�������ʽ�����ͻ���

                        socket.close()//�رոÿͻ��˵�socket
                        throw InterruptedException()//�׳��жϽ������߳�
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

}