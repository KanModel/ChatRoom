package me.kanmodel.oct18.concurrency.net

import java.util.*
import java.util.concurrent.Semaphore

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: KanModel
 * Date: 2018-12-17-9:47
 */
object DataManager {
    const val CHAT_LOCKER = "chatMutex"
    const val NOT_EMPTY_LOCKER = "notEmpty"
    val chatMutex = Semaphore(1, true)

    val notEmpty = Semaphore(0)

    val chatHistories = ArrayList<String>()//聊天记录
    /**
     * 临界资源
     * description: 新添聊天记录待刷新队列
     * @author: KanModel
     */
    val chatQueue = LinkedList<String>()
}