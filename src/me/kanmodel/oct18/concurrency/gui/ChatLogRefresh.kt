package me.kanmodel.oct18.concurrency.gui

import me.kanmodel.oct18.concurrency.net.DataManager.CHAT_LOCKER
import me.kanmodel.oct18.concurrency.net.DataManager.chatQueue
import me.kanmodel.oct18.concurrency.net.DataManager.NOT_EMPTY_LOCKER
import me.kanmodel.oct18.concurrency.net.DataManager.chatMutex
import me.kanmodel.oct18.concurrency.net.DataManager.notEmpty
import me.kanmodel.oct18.concurrency.net.StartServer
import me.kanmodel.oct18.concurrency.util.Log

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: KanModel
 * Date: 2018-12-21-22:52
 */
/**
 * @description: ˢ�������¼���
 * @author: KanModel
 * @create: 2018-12-21 22:52
 */
class ChatLogRefresh : Runnable {
    override fun run() {
        while (StartServer.flag) {
            Log.log("ˢ���߳� ���Ի�ȡ$NOT_EMPTY_LOCKER")
            notEmpty.acquire()
            Log.log("ˢ���߳� �õ�$NOT_EMPTY_LOCKER")
            Log.log("ˢ���߳� ���Ի�ȡ$CHAT_LOCKER")
            chatMutex.acquire()
            try {
                Log.log("ˢ���߳� �õ�$CHAT_LOCKER")
                ChatLogPanel.chatLogJTA.append("${chatQueue.poll()}\r\n")
                ChatLogPanel.chatLogJTA.caretPosition = ChatLogPanel.chatLogJTA.text.length//������Ϣ��ʾ����һ�У�Ҳ���ǹ�����������ĩβ����ʾ����һ���������Ϣ
            }finally {
                chatMutex.release()
                Log.log("ˢ���߳� �ͷ�$CHAT_LOCKER")
            }
        }
    }
}