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

    val chatHistories = ArrayList<String>()//�����¼
    /**
     * �ٽ���Դ
     * description: ���������¼��ˢ�¶���
     * @author: KanModel
     */
    val chatQueue = LinkedList<String>()
}