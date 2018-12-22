package me.kanmodel.oct18.concurrency.gui

import me.kanmodel.oct18.concurrency.net.DataManager.CHAT_MUTEX
import me.kanmodel.oct18.concurrency.net.DataManager.chatQueue
import me.kanmodel.oct18.concurrency.net.DataManager.NOT_EMPTY_LOCKER
import me.kanmodel.oct18.concurrency.net.DataManager.chatMutex
import me.kanmodel.oct18.concurrency.net.DataManager.notEmpty
import me.kanmodel.oct18.concurrency.net.StartServer
import me.kanmodel.oct18.concurrency.util.Log
import javax.swing.SwingUtilities

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: KanModel
 * Date: 2018-12-21-22:52
 */
/**
 * @description: 刷新聊天记录面板
 * @author: KanModel
 * @create: 2018-12-21 22:52
 */
class ChatLogRefresh : Runnable {
    override fun run() {
        while (StartServer.flag) {
            Log.log("刷新线程 尝试获取$NOT_EMPTY_LOCKER")
            notEmpty.acquire()
            Log.log("刷新线程 得到$NOT_EMPTY_LOCKER")
            Log.log("刷新线程 尝试获取$CHAT_MUTEX")
            chatMutex.acquire()
            try {
                Log.log("刷新线程 得到$CHAT_MUTEX")
                SwingUtilities.invokeLater {
                    ChatLogPanel.chatLogJTA.append("${chatQueue.poll()}\r\n")
                    ChatLogPanel.chatLogJTA.caretPosition = ChatLogPanel.chatLogJTA.text.length//设置消息显示最新一行，也就是滚动条出现在末尾，显示最新一条输入的信息
                }
            }finally {
                chatMutex.release()
                Log.log("刷新线程 释放$CHAT_MUTEX")
            }
        }
    }
}