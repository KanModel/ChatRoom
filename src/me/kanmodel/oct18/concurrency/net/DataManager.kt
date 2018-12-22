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
    val chatMutex = Semaphore(1, true)
    val notEmpty = Semaphore(0)
    val socketsMutex = Semaphore(1, true)
    val listMutex = Semaphore(1, true)

    val chatHistories = Vector<String>()//聊天记录
    /**
     * 临界资源
     * description: 新添聊天记录待刷新队列
     * @author: KanModel
     */
    val chatQueue = LinkedList<String>()
    val userSockets: Vector<Socket> = Vector()
}