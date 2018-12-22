package me.kanmodel.oct18.concurrency.net

import me.kanmodel.oct18.concurrency.util.Log
import me.kanmodel.oct18.concurrency.gui.ChatLogPanel
import me.kanmodel.oct18.concurrency.net.DataManager.CHAT_MUTEX
import me.kanmodel.oct18.concurrency.net.DataManager.LIST_MUTEX
import me.kanmodel.oct18.concurrency.net.DataManager.chatHistories
import me.kanmodel.oct18.concurrency.net.DataManager.chatMutex
import me.kanmodel.oct18.concurrency.net.DataManager.chatQueue
import me.kanmodel.oct18.concurrency.net.DataManager.listMutex
import me.kanmodel.oct18.concurrency.net.DataManager.notEmpty
import me.kanmodel.oct18.concurrency.net.DataManager.socketsMutex
import me.kanmodel.oct18.concurrency.net.DataManager.userSockets
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.net.Socket
import java.net.SocketException
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.SwingUtilities

/**
 * �������Ϣ�����̣߳�Ϊÿһ���ͻ��˵�Socket����һ���߳�
 */
class ReceiveServer(private val socket: Socket) : Runnable {
    var clientName: String = ""//��¼�ͻ�������

    override fun run() {
        try {
            //��ȡ��Ϣ��
            val brIn = BufferedReader(InputStreamReader(socket.getInputStream()))//ͨ�����淽ʽ��ȡ��Ϣ���е�����

            while (StartServer.flag) {
                if (!StartServer.flag) {
                    throw InterruptedException()//�׳��ж��쳣�����������ر�ʱ�ж�������Ϣ�����߳�
                }

                var info: Char
                var line: String
                try {
                    info = brIn.read().toChar()//�ȶ�ȡ��Ϣ�������ַ��������ж���Ϣ����
                    line = brIn.readLine()//��ȡ��Ϣ������Ϣ����
                } catch (e: SocketException) {
                    throw InterruptedException()//�׳��жϽ������߳�
                }

                when (info) {//1�����յ�������Ϣ
                    '1' -> {
                        if (clientName == "") {
                            Log.log("�����û����Ի�ȡ$CHAT_MUTEX")
                        } else {
                            Log.log("�߳� $clientName ���Ի�ȡ$CHAT_MUTEX")
                        }
                        chatMutex.acquire()
                        try {
                            Log.log("�߳� $clientName �õ�$CHAT_MUTEX")
                            line = "${SimpleDateFormat("HH:mm:ss").format(Date())} [$clientName]:$line"
                            chatQueue.offer(line)
                            notEmpty.release()
                            chatHistories.add(line)
//                            Log.log("�߳� $clientName ������Ϣ")
                            SendServer(line, "1")//����Ϣת�����ͻ���
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            chatMutex.release()
                            Log.log("�߳� $clientName �ͷ�$CHAT_MUTEX")
                        }
                    }
                    '2' -> {//2�������¿ͻ��˽�������
                        Log.log("$line ��������")
                        clientName = line
                        Log.log("�� $clientName ��������")

                        Log.log("�߳� $clientName ���Ի�ȡ$LIST_MUTEX")
                        listMutex.acquire()
                        try {
                            Log.log("�߳� $clientName �õ�$LIST_MUTEX")
                            SwingUtilities.invokeLater{
                                StartServer.userNames.add(line)//���¿ͻ����û�����ӵ�������
                                ChatLogPanel.userJL.setListData(StartServer.userNames)//���·�����û��б�
                                SendServer(StartServer.userNames, "2")//���û��б����ַ�������ʽ�����ͻ���
                            }
                        }finally {
                            listMutex.release()
                            Log.log("�߳� $clientName �ͷ�$LIST_MUTEX")
                        }
                    }
                    '3' -> {//3�������û����˳�����

                        socketsMutex.acquire()
                        try {
                            userSockets.remove(socket)//�Ƴ����������˳��Ŀͻ��˵�socket
                            socket.close()//�رոÿͻ��˵�socket
                        }finally {
                            socketsMutex.release()
                        }
                        Log.log("�߳� $clientName ���Ի�ȡ$LIST_MUTEX")
                        listMutex.acquire()
                        try {
                            Log.log("�߳� $clientName �õ�$LIST_MUTEX")
                            SwingUtilities.invokeLater {
                                StartServer.userNames.remove(line)//�Ƴ����������˳��Ŀͻ����û���
                                ChatLogPanel.userJL.setListData(StartServer.userNames)//���·�����û��б�
                                SendServer(StartServer.userNames, "3")//���û��б����ַ�������ʽ�����ͻ���
                            }
                        }finally {
                            listMutex.release()
                            Log.log("�߳� $clientName �ͷ�$LIST_MUTEX")
                        }
                        Log.log("�߳� $line �˳�����, ��ǰ�ͻ��˸���Ϊ��${userSockets.size}")
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