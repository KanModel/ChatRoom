package me.kanmodel.oct18.concurrency.net

import java.net.Socket
import java.util.*
import java.util.concurrent.Semaphore

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: KanModel
 * Date: 2018-12-17-9:47
 */
object DataManager {
    const val CHAT_MUTEX = "chatMutex"
    const val NOT_EMPTY_LOCKER = "notEmpty"
    const val SOCKETS_MUTEX = "socketsMutex"
    const val LIST_MUTEX = "listMutex"
    val chatMutex = Semaphore(1)
    val notEmpty = Semaphore(0)
    val socketsMutex = Semaphore(1)
    val listMutex = Semaphore(1)

    val chatHistories = Vector<String>()//�����¼
    /**
     * �ٽ���Դ
     * description: ���������¼��ˢ�¶���
     * @author: KanModel
     */
    val chatQueue = LinkedList<String>()
    val userSockets: Vector<Socket> = Vector()
}